package dev.mattidragon.coppercauldron.registry;

import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.recipe.CauldronBrewingRecipe;
import dev.mattidragon.coppercauldron.recipe.CauldronRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<CauldronBrewingRecipe> CAULDRON_BREWING_SERIALIZER = new CauldronBrewingRecipe.Serializer();

    public static final RecipeType<CauldronRecipe> CAULDRON_RECIPE_TYPE = createType(CopperCauldron.id("cauldron_brewing"));

    public static final RecipeBookCategory CAULDRON_CATEGORY = new RecipeBookCategory();

    private static <T extends Recipe<?>> RecipeType<T> createType(Identifier id) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }

    public static void register() {
        Registry.register(Registries.RECIPE_TYPE, CopperCauldron.id("cauldron_brewing"), CAULDRON_RECIPE_TYPE);

        Registry.register(Registries.RECIPE_SERIALIZER, CopperCauldron.id("cauldron_brewing"), CAULDRON_BREWING_SERIALIZER);

        Registry.register(Registries.RECIPE_BOOK_CATEGORY, CopperCauldron.id("cauldron"), CAULDRON_CATEGORY);
    }
}
