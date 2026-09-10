package com.thunderbear06.tags;

import com.thunderbear06.CCAndroids;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;

public class TagRegistry {
    public static final TagKey<Item> MINOR_ANDROID_FUEL = registerTag("minor_android_fuel");
    public static final TagKey<Item> MEDIUM_ANDROID_FUEL = registerTag("medium_android_fuel");
    public static final TagKey<Item> MAJOR_ANDROID_FUEL = registerTag("major_android_fuel");

    public static TagKey<Item> registerTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, name));
    }
}
