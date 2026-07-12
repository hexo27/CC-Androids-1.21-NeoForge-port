package com.thunderbear06.forge;

import com.thunderbear06.AndroidPlatformHelper;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.impl.Peripherals;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AndroidPlatformHelperNeoForge extends AndroidPlatformHelper {
	@Override
	public IPeripheral getPeripheral(ServerWorld world, BlockPos pos, Direction side) {
		return Peripherals.getGenericPeripheral(world, pos, side, world.getBlockEntity(pos));
	}

	@Override
	public Supplier<SpawnEggItem> getSpawnEggItem(RegistrySupplier<? extends EntityType<? extends MobEntity>> entityType, int color1, int color2, Item.Settings settings) {
		return () -> new SpawnEggItem(entityType.get(), color1, color2, settings);
	}

	public static void init() {
		if (INSTANCE == null) {
			INSTANCE = new AndroidPlatformHelperNeoForge();
		}
	}
}
