package com.thunderbear06.entity;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.*;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CCAndroids.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ANDROID_ENTITY = ENTITY_TYPES.register(
            "android",
            () -> EntityType.Builder.of(AndroidEntity::new, MobCategory.MISC).build("android")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedAndroidEntity>> ADVANCED_ANDROID_ENTITY = ENTITY_TYPES.register(
            "advanced_android",
            () -> EntityType.Builder.of(AdvancedAndroidEntity::new, MobCategory.MISC).build("advanced_android")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<CommandAndroidEntity>> COMMAND_ANDROID_ENTITY = ENTITY_TYPES.register(
            "command_android",
            () -> EntityType.Builder.of(CommandAndroidEntity::new, MobCategory.MISC).build("command_android")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidFrame>> ANDROID_FRAME_ENTITY = ENTITY_TYPES.register(
            "unfinished_android",
            () -> EntityType.Builder.of(AndroidFrame::new, MobCategory.MISC).build("android_frame")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<RogueDroidEntity>> ROGUE_ANDROID_ENTITY = ENTITY_TYPES.register(
            "rogue_android",
            () -> EntityType.Builder.of(RogueDroidEntity::new, MobCategory.MONSTER).build("rogue_android")
    );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        CCAndroids.LOGGER.info("Registered Entities");
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ANDROID_ENTITY.get(), AndroidEntity.createAndroidAttributes().build());
        event.put(ADVANCED_ANDROID_ENTITY.get(), AdvancedAndroidEntity.createAndroidAttributes().build());
        event.put(COMMAND_ANDROID_ENTITY.get(), CommandAndroidEntity.createAndroidAttributes().build());
        event.put(ROGUE_ANDROID_ENTITY.get(), RogueDroidEntity.createAndroidAttributes().build());
        event.put(ANDROID_FRAME_ENTITY.get(), Mob.createMobAttributes().build());
    }
}
