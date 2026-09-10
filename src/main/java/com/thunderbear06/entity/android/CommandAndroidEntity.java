package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class CommandAndroidEntity extends AdvancedAndroidEntity{
    public CommandAndroidEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.COMMAND);
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.ComAndroidMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.ComAndroidDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.ComAndroidSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.ComAndroidArmor);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.getEntity() instanceof net.minecraft.world.entity.player.Player player && player.isCreative() && super.hurt(source, amount);
    }

    @Override
    public boolean hasFuel() {
        return true;
    }
}
