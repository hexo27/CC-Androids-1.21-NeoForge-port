package com.thunderbear06.entity.android.frame;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class AndroidFrame extends Mob {
    private static final EntityDataAccessor<Byte> BUILD_PROGRESS = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Byte> COMPONENTS_NEEDED = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> INGOTS_NEEDED = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_CORE = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BOOLEAN);

    private boolean isAdvanced = false;
    private long lastHitTime = 0;

    public AndroidFrame(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BUILD_PROGRESS, (byte) 0);

        builder.define(COMPONENTS_NEEDED, CCAndroids.CONFIG.CompsForConstruction);
        builder.define(INGOTS_NEEDED, CCAndroids.CONFIG.IngotsForConstruction);
        builder.define(HAS_CORE, false);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack handStack = hand == InteractionHand.MAIN_HAND ? player.getMainHandItem() : player.getOffhandItem();

        Level world = player.level();

        if (handStack.is(ItemRegistry.COMPONENTS.get())) {
            if (addComponents(world)) {
                onSuccess(handStack, player, hand);
                return InteractionResult.SUCCESS;
            }
        }

        if (handStack.is(Items.IRON_INGOT) || handStack.is(Items.GOLD_INGOT)) {
            if (addPlates(world, handStack.is(Items.GOLD_INGOT))) {
                onSuccess(handStack, player, hand);
                return InteractionResult.SUCCESS;
            }
        }

        if (handStack.is(ItemRegistry.REDSTONE_REACTOR.get())) {
            if (insertCore(world)) {
                onSuccess(handStack, player, hand);
                return InteractionResult.SUCCESS;
            }
        }

        if ((handStack.is(ItemRegistry.ANDROID_CPU.get()) || handStack.is(Items.COMMAND_BLOCK)) && this.isReadyForCPU()) {
            insertCPU(handStack);
            onSuccess(handStack, player, hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    public void incrementProgress(int inc) {
        setBuildProgress(this.entityData.get(BUILD_PROGRESS) + inc);
    }

    private void setBuildProgress(int progress) {
        this.entityData.set(BUILD_PROGRESS, (byte) progress);
    }

    private void onSuccess(ItemStack stack, Player player, InteractionHand hand) {
        stack.shrink(1);
        player.setItemInHand(hand, stack);
    }

    private boolean addComponents(Level world) {
        byte comps = this.getComponentsNeeded();

        if (comps <= 0)
            return false;

        float pitch = (getRandom().nextInt(10, 12) * 0.1f);

        this.entityData.set(COMPONENTS_NEEDED, --comps);

        if (comps <= 0)
            world.playSound(null, this.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.NEUTRAL, 1.0f, pitch);
        else
            world.playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0f, pitch);

        return true;
    }

    private boolean addPlates(Level world, boolean isGold) {
        if (getComponentsNeeded() > 0)
            return false;

        if (getIngotsNeeded() <= 0)
            return false;

        if (isGold && !this.isAdvanced) {
            if (getIngotsNeeded() < CCAndroids.CONFIG.IngotsForConstruction)
                return false;
            this.isAdvanced = true;
        }

        if (!isGold && this.isAdvanced)
            return false;

        float pitch = (getRandom().nextInt(10, 12) * 0.1f);

        byte ingots = this.entityData.get(INGOTS_NEEDED);

        this.entityData.set(INGOTS_NEEDED, --ingots);

        if (ingots == 0)
            world.playSound(null, this.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.NEUTRAL, 1.0f, pitch);
        else
            world.playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0f, pitch);

        return true;
    }

    private boolean insertCore(Level world) {
        if (hasCore())
            return false;
        this.entityData.set(HAS_CORE, true);

        world.playSound(null, this.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean isReadyForCPU() {
        return getComponentsNeeded() == 0 && hasCore() && getIngotsNeeded() == 0;
    }

    private void insertCPU(ItemStack cpu) {
        ComputerFamily family;
        int computerID = -1;

        if (cpu.is(Items.COMMAND_BLOCK))
            family = ComputerFamily.COMMAND;
        else
            family = this.isAdvanced ? ComputerFamily.ADVANCED : ComputerFamily.NORMAL;

        CustomData data = cpu.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().contains("ComputerID"))
            computerID = data.copyTag().getInt("ComputerID");

        finish(family, computerID);
    }

    private void finish(ComputerFamily family, int computerID) {
        BaseAndroidEntity android;

        switch (family) {
            case NORMAL -> android = EntityRegistry.ANDROID_ENTITY.get().create(level());
            case ADVANCED -> android = EntityRegistry.ADVANCED_ANDROID_ENTITY.get().create(level());
            case COMMAND -> android = EntityRegistry.COMMAND_ANDROID_ENTITY.get().create(level());
            default -> throw new IllegalArgumentException("Unknown ComputerFamily " + family);
        }

        assert android != null;

        android.moveTo(this.position(), this.getYRot(), this.getXRot());
        android.getComputer().setComputerID(computerID);

        this.discard();
        this.level().addFreshEntity(android);

        android.level().playSound(null, android.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    public byte getComponentsNeeded() {
        return this.entityData.get(COMPONENTS_NEEDED);
    }

    public byte getIngotsNeeded() {
        return this.entityData.get(INGOTS_NEEDED);
    }

    public boolean hasCore() {
        return this.entityData.get(HAS_CORE);
    }

    @Override
    public void push(Entity entity) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide() || this.isRemoved())
            return false;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return false;
        }
        if (this.isInvulnerableTo(source))
            return false;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.onBreak();
            this.kill();
            return false;
        }
        if (!(source.getEntity() instanceof Player))
            return false;
        if (!((Player) source.getEntity()).getAbilities().mayBuild)
            return false;

        if (source.getEntity() instanceof Player playerAttacker && playerAttacker.isCreative()) {
            this.playHurtSound(source);
            this.kill();
        } else {
            long l = this.level().getGameTime();

            if (l - this.lastHitTime > 5L) {
                this.level().broadcastEntityEvent(this, (byte) 32);
                this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                this.lastHitTime = l;
            } else {
                this.onBreak();
            }
        }

        return true;
    }

    public void onBreak() {
        this.dropInventory();
        kill();
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ANVIL_FALL;
    }

    protected void dropInventory() {
        byte components_dropped = (byte) (CCAndroids.CONFIG.CompsForConstruction - getComponentsNeeded());

        for (int i = 0; i < components_dropped; i++) {
            this.spawnAtLocation(new ItemStack(ItemRegistry.COMPONENTS.get()));
        }

        int ingots_dropped = CCAndroids.CONFIG.IngotsForConstruction - getIngotsNeeded();

        for (int j = 0; j < ingots_dropped; j++) {
            this.spawnAtLocation(new ItemStack(this.isAdvanced ? Items.GOLD_INGOT : Items.IRON_INGOT));
        }

        if (hasCore())
            this.spawnAtLocation(new ItemStack(ItemRegistry.REDSTONE_REACTOR.get()));

        this.spawnAtLocation(new ItemStack(ItemRegistry.ANDROID_FRAME.get()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putByte("ComponentsNeeded", getComponentsNeeded());
        nbt.putByte("IngotsNeeded", getIngotsNeeded());
        nbt.putBoolean("IsAdvanced", this.isAdvanced);
        nbt.putBoolean("HasCore", hasCore());
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("ComponentsNeeded")) {
            this.entityData.set(COMPONENTS_NEEDED, nbt.getByte("ComponentsNeeded"));
            this.entityData.set(INGOTS_NEEDED, nbt.getByte("IngotsNeeded"));
            this.entityData.set(HAS_CORE, nbt.getBoolean("HasCore"));
            this.isAdvanced = nbt.getBoolean("IsAdvanced");
        }
        super.readAdditionalSaveData(nbt);
    }
}
