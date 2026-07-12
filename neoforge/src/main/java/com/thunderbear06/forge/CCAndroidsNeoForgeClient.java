package com.thunderbear06.forge;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.client.CCAndroidsClient;
import com.thunderbear06.client.entity.render.AndroidEntityRenderer;
import com.thunderbear06.client.entity.render.AndroidFrameEntityRenderer;
import com.thunderbear06.client.entity.render.RogueAndroidEntityRenderer;
import com.thunderbear06.entity.EntityRegistry;
import net.minecraft.client.render.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class CCAndroidsNeoForgeClient {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CCAndroidsClient.init();

		EntityRenderers.register(EntityRegistry.ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ADVANCED_ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.COMMAND_ANDROID_ENTITY.get(), (AndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ROGUE_ANDROID_ENTITY.get(), (RogueAndroidEntityRenderer::new));
		EntityRenderers.register(EntityRegistry.ANDROID_FRAME_ENTITY.get(), (AndroidFrameEntityRenderer::new));

		CCAndroids.LOGGER.info("Registered Forge client");
	}
}
