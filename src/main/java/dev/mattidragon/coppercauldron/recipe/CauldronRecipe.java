package dev.mattidragon.coppercauldron.recipe;

import dev.mattidragon.coppercauldron.registry.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;

public interface CauldronRecipe extends Recipe<CauldronRecipeContent> {
    @Override
    default ItemStack craft(CauldronRecipeContent recipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        throw new UnsupportedOperationException("This method doesn't make sense for cauldron recipes");
    }

    CauldronRecipeContent apply(CauldronRecipeContent input, RegistryWrapper.WrapperLookup wrapperLookup);

    @Override
    default IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    default RecipeBookCategory getRecipeBookCategory() {
        return ModRecipes.CAULDRON_CATEGORY;
    }

    @Override
    default RecipeType<? extends Recipe<CauldronRecipeContent>> getType() {
        return ModRecipes.CAULDRON_RECIPE_TYPE;
    }

    int processingTime();
}
