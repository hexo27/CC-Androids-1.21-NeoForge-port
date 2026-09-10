package com.thunderbear06.recipe;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.recipe.CustomShapedRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CCAndroids.MOD_ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WrenchShapedRecipe>> WRENCH_SHAPED = RECIPE_TYPES.register("wrench_shaped", () -> CustomShapedRecipe.serialiser(WrenchShapedRecipe::new));

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		CCAndroids.LOGGER.info("Registered Recipes");
	}
}
