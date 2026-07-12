package com.thunderbear06.forge;

import com.thunderbear06.CCAndroids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CCAndroids.MOD_ID)
public final class CCAndroidsNeoForge {
	public CCAndroidsNeoForge(IEventBus bus) {
		AndroidPlatformHelperNeoForge.init();

		// Run our common setup.
		CCAndroids.init();
	}
}
