package com.thunderbear06.component;

import com.mojang.serialization.Codec;
import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.ModRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ComponentRegistry {
    public static final ComponentType<Integer> COMPUTER_ID_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "computer_id"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static void Register() { }
}
