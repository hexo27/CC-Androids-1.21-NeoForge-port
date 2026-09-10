package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CCAndroids.MOD_ID);

    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties().durability(100)));
    public static final DeferredItem<Item> COMPONENTS = ITEMS.register("components", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ANDROID_CPU = ITEMS.register("android_cpu", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REDSTONE_REACTOR = ITEMS.register("redstone_reactor", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ANDROID_FRAME = ITEMS.register("android_frame", () -> new AndroidFrameItem(new Item.Properties()));

    public static final DeferredItem<Item> ANDROID_SPAWN_EGG = registerEgg(EntityRegistry.ANDROID_ENTITY, 0xb2b2b2, 0x8a8c8b, "android_spawn");
    public static final DeferredItem<Item> ANDROID_ADVANCED_SPAWN_EGG = registerEgg(EntityRegistry.ADVANCED_ANDROID_ENTITY, 0xb2b2b2, 0xa5a333, "android_advanced_spawn");
    public static final DeferredItem<Item> ANDROID_COMMAND_SPAWN_EGG = registerEgg(EntityRegistry.COMMAND_ANDROID_ENTITY, 0xfc9e46, 0x9b5c22, "android_command_spawn");
    public static final DeferredItem<Item> ANDROID_ROGUE_SPAWN_EGG = registerEgg(EntityRegistry.ROGUE_ANDROID_ENTITY, 0xf41818, 0x9b2222, "android_rogue_spawn");

    public static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, CCAndroids.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ANDROIDS_ITEM_GROUP = ITEM_GROUPS.register("androids_item_group",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.cc_androids.android_item_group"))
                    .icon(() -> new ItemStack(WRENCH.get()))
                    .build());

    private static DeferredItem<Item> registerEgg(Supplier<? extends EntityType<? extends Mob>> entityType, int color1, int color2, String path) {
        return ITEMS.register(path, () -> new DeferredSpawnEggItem(entityType, color1, color2, new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        ITEM_GROUPS.register(bus);
        CCAndroids.LOGGER.info("Registered Items");
    }
}
