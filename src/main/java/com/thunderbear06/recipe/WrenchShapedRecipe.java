package com.thunderbear06.recipe;

import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.recipe.CustomShapedRecipe;
import dan200.computercraft.shared.recipe.ShapedRecipeSpec;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.NonNullList;

public class WrenchShapedRecipe extends CustomShapedRecipe {
	public WrenchShapedRecipe(ShapedRecipeSpec recipe) {
		super(recipe);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inventory) {
		NonNullList<ItemStack> remainder = super.getRemainingItems(inventory);
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.getItem() == ItemRegistry.WRENCH.get())
			{
				stack.setDamageValue(stack.getDamageValue() + 1);
				if (stack.getDamageValue() < stack.getMaxDamage())
					remainder.set(i, stack.copyWithCount(1));
			}
		}
		return remainder;
	}

	@Override
	public RecipeSerializer<WrenchShapedRecipe> getSerializer() {
		return RecipeRegistry.WRENCH_SHAPED.get();
	}
}
