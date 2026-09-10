package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.sounds.SoundRegistry;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RogueDroidEntity extends Monster {
    public RogueDroidEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.RogueMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.RogueDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.RogueSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.RogueArmor);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.NATURAL) {
            if (!CCAndroids.CONFIG.RoguesSpawnNaturally)
                return false;
            if (level.canSeeSky(this.blockPosition()))
                return false;
            if (level.getRawBrightness(this.blockPosition(), 0) > 3)
                return false;
        }

        return super.checkSpawnRules(level, spawnReason);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason.equals(MobSpawnType.NATURAL) && CCAndroids.CONFIG.RoguesSpawnWithTools) {
            int rng = this.getRandom().nextInt(10);

            ItemStack handStack = switch (rng) {
                case 5 -> Items.WOODEN_SHOVEL.getDefaultInstance();
                case 6 -> Items.WOODEN_HOE.getDefaultInstance();
                case 7 -> Items.WOODEN_PICKAXE.getDefaultInstance();
                case 8 -> Items.WOODEN_SWORD.getDefaultInstance();
                case 9 -> Items.STICK.getDefaultInstance();
                case 10 -> Items.IRON_SHOVEL.getDefaultInstance();
                default -> ItemStack.EMPTY;
            };

            setItemInHand(InteractionHand.MAIN_HAND, handStack);
        }
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f, 1.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.5, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    @Override
    public int getAirSupply() {
        return 10;
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
}
