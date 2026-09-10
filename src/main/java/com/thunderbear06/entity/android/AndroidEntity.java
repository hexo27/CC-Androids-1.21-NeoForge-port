package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.AndroidLookAtEntityGoal;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.sounds.SoundRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AndroidEntity extends BaseAndroidEntity {
    private static final EntityDataAccessor<Boolean> IS_LOCKED = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FACE = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BYTE);

    public AndroidEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);

        this.brain = new AndroidBrain(this);
        this.computerContainer.setFamily(ComputerFamily.NORMAL);

        initAndroidGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_LOCKED, false);
        builder.define(VARIANT, (byte) 0);
        builder.define(FACE, (byte) 0);
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.AndroidMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.AndroidDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.AndroidSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.AndroidArmor);
    }

    protected void initAndroidGoals() {
        this.goalSelector.addGoal(0, new AndroidLookAtEntityGoal(this, Player.class, 10));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (hasFuel())
            brain.getTaskManager().tick();
        else if (!this.getNavigation().isDone())
            this.getNavigation().stop();
    }

    @Override
    protected boolean isIdle() {
        return brain.getTaskManager().isIdle();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isLocked() && !this.brain.isOwningPlayer(player)) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.NEUTRAL, 1.0f, 1.0f);
            player.displayClientMessage(Component.translatable("entity.cc_androids.android.locked"), true);
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, swapHandStack(player.getItemInHand(hand)));
            return InteractionResult.SUCCESS;
        }

        ItemStack playerHandStack = player.getItemInHand(hand);

        InteractionResult itemUseResult = handleItemUse(playerHandStack);

        if (itemUseResult != null)
            return itemUseResult;

        if (!level().isClientSide()) {
            if (playerHandStack.is(Items.TRIPWIRE_HOOK) && this.brain.isOwningPlayer(player)) {
                setLocked(!isLocked());
                return InteractionResult.SUCCESS;
            }

            if (this.brain.getOwningPlayerProfile() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            this.getComputer().openComputer((ServerPlayer) player);
        }

        return InteractionResult.CONSUME;
    }

    private InteractionResult handleItemUse(ItemStack stack) {
        if (stack.is(ItemRegistry.WRENCH.get())) {
            return InteractionResult.PASS;
        }

        if (stack.is(ItemRegistry.COMPONENTS.get())) {
            repair(stack);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.GRAY_DYE)) {
            setVariant((byte) 1);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.PINK_DYE)) {
            setVariant((byte) 2);
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    public boolean isLocked() {
        return this.entityData.get(IS_LOCKED);
    }

    public void setLocked(boolean locked) {
        this.entityData.set(IS_LOCKED, locked);
    }

    public byte getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(byte variant) {
        this.entityData.set(VARIANT, variant);
    }

    public boolean hasVariant() {
        return getVariant() > 0;
    }

    public void setFace(String faceName) {
        byte face = switch (faceName) {
            case "angry" -> 1;
            case "annoyed" -> 2;
            case "happy" -> 3;
            case "sad" -> 4;
            case "woozy" -> 5;
            default -> 0;
        };

        this.entityData.set(FACE, face);
    }

    public byte getFace() {
        return this.entityData.get(FACE);
    }

    public void deconstruct() {
        super.dropInventory();
        this.dropComponents(true);
        this.dropIngots(true);

        AndroidFrame frame = this.convertTo(EntityRegistry.ANDROID_FRAME_ENTITY.get(), false);
        frame.moveTo(this.position(), this.getYRot(), this.getXRot());
        this.level().playSound(null, blockPosition(), SoundEvents.ANVIL_DESTROY, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    protected ItemStack swapHandStack(ItemStack stack) {
        ItemStack heldStack = this.getMainHandStack();

        if (stack.is(ItemTags.FLOWERS))
            spawnHearts();

        this.setStackInHand(InteractionHand.MAIN_HAND, stack);
        return heldStack;
    }

    public boolean repair(ItemStack stack) {
        if (this.getHealth() < this.getMaxHealth()) {
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0f, 1.0f);
            this.heal(5);
            stack.shrink(1);
            return true;
        }
        return false;
    }

    private void spawnHearts() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundRegistry.ANDROID_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.ANDROID_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.ANDROID_DEATH.get();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();

        dropIngots(false);
        dropComponents(false);
    }

    protected void dropIngots(boolean full) {
        this.dropStack(Items.IRON_INGOT.getDefaultInstance().copyWithCount((int) (CCAndroids.CONFIG.IngotsForConstruction * (full ? 1.0 : CCAndroids.CONFIG.IngotsDroppedOnDeathPercentage))));
    }

    protected void dropComponents(boolean full) {
        this.dropStack(ItemRegistry.COMPONENTS.get().getDefaultInstance().copyWithCount((int) (CCAndroids.CONFIG.CompsForConstruction * (full ? 1.0 : CCAndroids.CONFIG.CompsDroppedOnDeathPercentage))));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(VARIANT, nbt.getByte("Variant"));
    }
}
