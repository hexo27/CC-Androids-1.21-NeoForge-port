package com.thunderbear06;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.api.AndroidAPI;
import com.thunderbear06.config.CCAndroidsConfig;
import com.thunderbear06.config.ConfigLoader;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.menu.MenuRegistry;
import com.thunderbear06.recipe.RecipeRegistry;
import com.thunderbear06.sounds.SoundRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CCAndroids.MOD_ID)
public class CCAndroids {
	public static final String MOD_ID = "cc_androids";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static CCAndroidsConfig CONFIG;

	public CCAndroids(IEventBus modBus) {
		ComputerCraftAPI.registerAPIFactory(computer -> {
			AndroidBrain brain = computer.getComponent(ComputerComponents.ANDROID_COMPUTER);
			return brain == null ? null : new AndroidAPI(brain);
		});

		CONFIG = ConfigLoader.loadConfig(MOD_ID, new CCAndroidsConfig());
		LOGGER.info("Loaded Config File");

		MenuRegistry.register(modBus);
		EntityRegistry.register(modBus);
		ItemRegistry.register(modBus);
		SoundRegistry.register(modBus);
		RecipeRegistry.register(modBus);

		modBus.addListener(EntityRegistry::registerAttributes);
		modBus.addListener((BuildCreativeModeTabContentsEvent event) -> {
			if (event.getTab() == ItemRegistry.ANDROIDS_ITEM_GROUP.get()) {
				event.accept(ItemRegistry.WRENCH);
				event.accept(ItemRegistry.COMPONENTS);
				event.accept(ItemRegistry.ANDROID_CPU);
				event.accept(ItemRegistry.REDSTONE_REACTOR);
				event.accept(ItemRegistry.ANDROID_FRAME);
				event.accept(ItemRegistry.ANDROID_SPAWN_EGG);
				event.accept(ItemRegistry.ANDROID_ADVANCED_SPAWN_EGG);
				event.accept(ItemRegistry.ANDROID_COMMAND_SPAWN_EGG);
				event.accept(ItemRegistry.ANDROID_ROGUE_SPAWN_EGG);
			}
		});

		// Replaces the old PlayerManagerMixin: forward player chat to nearby androids.
		NeoForge.EVENT_BUS.addListener((ServerChatEvent event) -> {
			var player = event.getPlayer();
			player.level().getEntitiesOfClass(BaseAndroidEntity.class, player.getBoundingBox().inflate(50)).forEach(android -> {
				android.readChatMessage(event.getMessage().getString(), player.getGameProfile().getName(), player.getUUID());
			});
		});
	}
}
