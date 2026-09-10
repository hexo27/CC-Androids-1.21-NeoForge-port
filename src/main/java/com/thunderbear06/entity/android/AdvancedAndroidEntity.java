package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AdvancedAndroidEntity extends AndroidEntity{
    public AdvancedAndroidEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.ADVANCED);
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.AdvAndroidMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.AdvAndroidDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.AdvAndroidSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.AdvAndroidArmor);
    }

    @Override
    public double getEntitySearchRadius() {
        return super.getEntitySearchRadius() * 3;
    }

    @Override
    public int getBlockSearchRadius() {
        return super.getBlockSearchRadius() * 3;
    }

    @Override
    protected void dropIngots(boolean full) {
        this.dropStack(Items.GOLD_INGOT.getDefaultInstance().copyWithCount((int) (CCAndroids.CONFIG.IngotsForConstruction * (full ? 1.0 : 0.5))));
    }
}
