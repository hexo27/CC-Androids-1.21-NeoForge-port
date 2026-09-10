package com.thunderbear06.entity.android;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.computer.AndroidComputerContainer;
import com.thunderbear06.computer.EntityComputer;
import com.thunderbear06.entity.player.AndroidPlayer;
import com.thunderbear06.inventory.AndroidInventory;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.tags.TagRegistry;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BaseAndroidEntity extends PathfinderMob {
    public AndroidBrain brain;

    public final AndroidInventory inventory;

    protected final AndroidComputerContainer computerContainer;
    protected final int maxFuel = 10000;
    protected int fuel = 0;

    public boolean isOn = false;

    protected BaseAndroidEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);

        ((GroundPathNavigation) this.getNavigation()).setCanPassDoors(true);

        this.inventory = new AndroidInventory(9);
        this.computerContainer = new AndroidComputerContainer(this);
    }

    // Disables random attributes on spawn (hopefully)
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        return entityData;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        brain.getModules().interactionModule.tickDoorInteraction();
    }

    @Override
    public void tick() {
        super.tick();

        updateSwingTime();

        if (this.level().isClientSide())
            return;

        this.computerContainer.onTick();

        if (this.tickCount % 20 > 0)
            return;

        if (isIdle())
            updatePeripherals();
        else
            consumeFuel();
    }

    protected boolean isIdle() {
        return true;
    }

    public void shutdown() {
        this.isOn = false;

        this.brain.onShutdown();
    }

    private void updatePeripherals() {
        if (this.computerContainer.getComputerID() < 0 || !this.computerContainer.isOn)
            return;

        if (!(this.level() instanceof ServerLevel serverLevel))
            return;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP)
                continue;

            if (this.getComputer().hasUpgrade(ComputerSide.valueOf(direction.ordinal())))
                continue;

            IPeripheral peripheral = serverLevel.getCapability(PeripheralCapability.get(), this.blockPosition().relative(direction), direction);

            this.computerContainer.setPeripheral(ComputerSide.valueOf(direction.get3DDataValue()), peripheral);
        }
    }

    @Override
    public void setItemInHand(InteractionHand hand, ItemStack stack) {
        super.setItemInHand(hand, stack);

        this.getComputer().onHandItemChanged(hand);

        if (this.getComputer().isOn)
            this.getComputer().getUpgradePeripherals();
    }

    // Compatibility wrappers for the old Yarn-named call sites
    public void setStackInHand(InteractionHand hand, ItemStack stack) {
        setItemInHand(hand, stack);
    }

    public ItemStack getStackInHand(InteractionHand hand) {
        return getItemInHand(hand);
    }

    public ItemStack getMainHandStack() {
        return getMainHandItem();
    }

    public ItemStack getOffHandStack() {
        return getOffhandItem();
    }

    public void dropStack(ItemStack stack) {
        spawnAtLocation(stack);
    }

    protected void consumeFuel() {
        if (this.fuel > 0)
            this.fuel--;
    }

    private int getFuelMultiplier(ItemStack stack) {
        if (stack.is(TagRegistry.MINOR_ANDROID_FUEL))
            return 10;
        if (stack.is(TagRegistry.MEDIUM_ANDROID_FUEL))
            return 80;
        if (stack.is(TagRegistry.MAJOR_ANDROID_FUEL))
            return 800;
        return 0;
    }

    public boolean addFuel(int min, ItemStack stack) {
        int mult = getFuelMultiplier(stack);

        if (mult <= 0)
            return false;

        int fuelAvailable = Math.min(min, stack.getCount());

        int fuelNeeded = this.maxFuel - this.fuel;

        int fuelUsed = Math.min(fuelAvailable, fuelNeeded);

        setFuel(Math.min(this.fuel + (fuelUsed * mult), this.maxFuel));
        stack.shrink(fuelUsed);

        return true;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int newFuel) {
        this.fuel = newFuel;
    }

    public boolean hasFuel() {
        return this.fuel > 0;
    }

    public AndroidComputerContainer getComputer() {
        return this.computerContainer;
    }

    // Container
    public MethodResult pickupGroundItem(ItemEntity itemEntity) {
        if (itemEntity.isRemoved())
            return MethodResult.of("Item does not exist");
        if (itemEntity.getItem().isEmpty())
            return MethodResult.of("Cannot pickup item. Item is broken (Contact mod author)");
        if (itemEntity.hasPickUpDelay())
            return MethodResult.of("Unable to pickup item");

        this.setStackInHand(InteractionHand.MAIN_HAND, itemEntity.getItem().copy());
        itemEntity.discard();

        return MethodResult.of();
    }

    public MethodResult dropHandItem() {
        ItemStack itemStack = this.getMainHandStack();

        if (itemStack.isEmpty())
            return MethodResult.of("InteractionHand is empty");

        this.dropStack(itemStack);
        this.setStackInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return MethodResult.of();
    }

    protected void dropInventory() {
        this.dropCPU();
        this.dropStack(ItemRegistry.REDSTONE_REACTOR.get().getDefaultInstance());

        for (ItemStack stack : this.inventory.removeAllItems()) {
            this.dropStack(stack);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.dropInventory();
    }

    private void dropCPU() {
        boolean isCommand = this.computerContainer.getFamily() == ComputerFamily.COMMAND;

        ItemStack stack = new ItemStack(isCommand ? Items.COMMAND_BLOCK : ItemRegistry.ANDROID_CPU.get());

        if (this.computerContainer.getComputerID() >= 0) {
            CompoundTag compound = new CompoundTag();

            compound.putInt("ComputerID", this.computerContainer.getComputerID());

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }

        this.dropStack(stack);
    }

    public ItemStack stashStack(ItemStack stack, int index) {
        ItemStack storedStack = this.inventory.getItem(index);

        if (storedStack.isEmpty()) {
            this.inventory.setItem(index, stack);
            return ItemStack.EMPTY;
        } else if (storedStack.is(stack.getItem())) {
            int space = storedStack.getMaxStackSize() - storedStack.getCount();
            int transfer = Math.min(stack.getCount(), space);

            storedStack.grow(transfer);

            stack.shrink(transfer);
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    public ItemStack getStashItem(int index, boolean remove) {
        ItemStack storedStack = this.inventory.getItem(index);

        if (remove)
            this.inventory.setItem(index, ItemStack.EMPTY);

        return storedStack;
    }

    public void swapOffHandStack() {
        ItemStack mainHandStack = this.getMainHandStack().copy();
        this.setStackInHand(InteractionHand.MAIN_HAND, this.getOffHandStack().copy());
        this.setStackInHand(InteractionHand.OFF_HAND, mainHandStack);
    }

    public void jump() {
        if (!this.onGround())
            return;

        float velocity = 0.42F;
        if (this.hasEffect(MobEffects.JUMP)) {
            var effect = this.getEffect(MobEffects.JUMP);
            if (effect != null)
                velocity += 0.1F * (effect.getAmplifier() + 1);
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, velocity, motion.z);
        this.hasImpulse = true;
    }

    public @Nullable MethodResult canStash(ItemStack itemStack, int index) {
        if (index < 0 || index > this.inventory.getContainerSize() - 1)
            return MethodResult.of(String.format("Index must be between 0 and %d", inventory.getContainerSize()));

        ItemStack storedStack = this.inventory.getItem(index);

        if (!storedStack.isEmpty() && !ItemStack.isSameItemSameComponents(storedStack, itemStack))
            return MethodResult.of("Index is occupied by another item stack!");

        return null;
    }

    // Chat

    public void sendChatMessage(String msg) {
        if (getServer() == null)
            return;

        getServer().getPlayerList().broadcastSystemMessage(Component.literal("[" + getName().getString() + "] " + msg), false);
    }

    public void readChatMessage(String msg, String senderName, UUID senderUUID) {
        if (!isOn)
            return;

        EntityComputer computer = getComputer().getServerComputer();

        if (computer == null)
            return;

        computer.queueEvent("onChatMessage", new Object[]{
                msg, senderName, senderUUID.toString()
        });
    }

    // Misc

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("Items", this.inventory.toNbtCompound(this.level().registryAccess()));

        nbt.putInt("Fuel", this.getFuel());

        CompoundTag computerCompound = new CompoundTag();

        this.computerContainer.writeNbt(computerCompound);
        this.brain.writeNbt(computerCompound);
        nbt.put("ComputerEntity", computerCompound);

        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        this.inventory.fromNbtCompound(this.level().registryAccess(), nbt.getCompound("Items"));

        if (nbt.contains("Fuel"))
            setFuel(nbt.getInt("Fuel"));

        if (nbt.contains("ComputerEntity")) {
            CompoundTag computerCompound = nbt.getCompound("ComputerEntity");

            this.computerContainer.readNbt(computerCompound);
            this.brain.readNbt(computerCompound);
        }

        super.readAdditionalSaveData(nbt);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.MAGIC))
            return false;

        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return false;
    }


    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);

        ServerComputer computer = this.computerContainer.getServerComputer();

        if (computer != null)
            computer.close();
    }

    public double getEntitySearchRadius() {
        return 10.0;
    }

    public int getBlockSearchRadius() {
        return 10;
    }
}
