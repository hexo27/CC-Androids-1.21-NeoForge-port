package com.thunderbear06.forge;

import com.thunderbear06.AndroidPlatformHelper;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.impl.Peripherals;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class AndroidPlatformHelperForge extends AndroidPlatformHelper {
	@Override
	public IPeripheral getPeripheral(ServerWorld world, BlockPos pos, Direction side, Runnable invalidate) {
		return Peripherals.getPeripheral(world, pos, side, invalidate::run);
	}

	@Override
	public Supplier<SpawnEggItem> getSpawnEggItem(RegistrySupplier<? extends EntityType<? extends MobEntity>> entityType, int color1, int color2, Item.Settings settings) {
		return () -> new ForgeSpawnEggItem(entityType, color1, color2, settings);
	}

	public static void init() {
		if (INSTANCE == null) {
			INSTANCE = new AndroidPlatformHelperForge();
		}
	}
}
