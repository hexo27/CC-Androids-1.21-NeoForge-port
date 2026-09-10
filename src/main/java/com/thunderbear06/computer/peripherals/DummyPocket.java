package com.thunderbear06.computer.peripherals;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.impl.PocketUpgrades;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DummyPocket implements IPocketAccess {
    private final BaseAndroidEntity owner;

    public DummyPocket(BaseAndroidEntity container) {
        this.owner = container;
    }

    public UpgradeData<IPocketUpgrade> createUpgrade(ItemStack stack) {
        return PocketUpgrades.instance().get(this.owner.level().registryAccess(), stack);
    }

    @Override
    public ServerLevel getLevel() {
        return (ServerLevel) this.owner.level();
    }

    @Override
    public Vec3 getPosition() {
        return this.owner.position();
    }

    @Override
    public @Nullable Entity getEntity() {
        return this.owner;
    }

    @Override
    public int getColour() {
        return 0;
    }

    @Override
    public void setColour(int colour) {}

    @Override
    public int getLight() {
        return 0;
    }

    @Override
    public void setLight(int colour) {}

    @Override
    public @Nullable UpgradeData<IPocketUpgrade> getUpgrade() {
        return null;
    }

    @Override
    public void setUpgrade(@Nullable UpgradeData<IPocketUpgrade> upgrade) {}

    @Override
    public DataComponentPatch getUpgradeData() {
        return DataComponentPatch.EMPTY;
    }

    @Override
    public void setUpgradeData(DataComponentPatch patch) {}

    @Override
    public void invalidatePeripheral() {}
}
