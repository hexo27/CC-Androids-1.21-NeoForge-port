package com.thunderbear06.sounds;

import com.thunderbear06.CCAndroids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CCAndroids.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_AMBIENT = registerSound("android_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_HURT = registerSound("android_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_DEATH = registerSound("android_death");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, id)));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
        CCAndroids.LOGGER.info("Registered Sound Events");
    }
}
