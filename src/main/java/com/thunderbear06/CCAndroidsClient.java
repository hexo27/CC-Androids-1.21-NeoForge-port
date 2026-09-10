package com.thunderbear06;

import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.render.AndroidEntityRenderer;
import com.thunderbear06.entity.render.AndroidFrameEntityRenderer;
import com.thunderbear06.entity.render.RogueAndroidEntityRenderer;
import com.thunderbear06.menu.MenuRegistry;
import com.thunderbear06.screen.AndroidScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = CCAndroids.MOD_ID, value = Dist.CLIENT)
public class CCAndroidsClient {

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(MenuRegistry.ANDROID.get(), AndroidScreen::new);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.ANDROID_ENTITY.get(), AndroidEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ADVANCED_ANDROID_ENTITY.get(), AndroidEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.COMMAND_ANDROID_ENTITY.get(), AndroidEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ROGUE_ANDROID_ENTITY.get(), RogueAndroidEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANDROID_FRAME_ENTITY.get(), AndroidFrameEntityRenderer::new);

		CCAndroids.LOGGER.info("Registered NeoForge client");
	}
}
