package com.thunderbear06;

import dan200.computercraft.api.peripheral.IPeripheral;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Supplier;

public abstract class AndroidPlatformHelper {
	protected static AndroidPlatformHelper INSTANCE;

	public abstract IPeripheral getPeripheral(ServerWorld world, BlockPos pos, Direction side, Runnable invalidate);

	public abstract Supplier<SpawnEggItem> getSpawnEggItem(RegistrySupplier<? extends EntityType<? extends MobEntity>> entityType, int color1, int color2, Item.Settings settings);

	public static AndroidPlatformHelper get() {
		return INSTANCE;
	}
}
