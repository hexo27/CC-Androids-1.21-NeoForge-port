package com.thunderbear06.menu;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, CCAndroids.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AndroidMenu>> ANDROID = MENUS.register("android",
            () -> ContainerData.toType(ComputerContainerData.STREAM_CODEC, AndroidMenu::ofData));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
        CCAndroids.LOGGER.info("Registered Menus");
    }
}
