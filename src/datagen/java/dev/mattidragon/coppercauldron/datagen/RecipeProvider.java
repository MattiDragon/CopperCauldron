package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import dev.mattidragon.coppercauldron.datagen.util.CauldronBrewingRecipeJsonBuilder;
import dev.mattidragon.coppercauldron.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new Generator(wrapperLookup, recipeExporter);
    }

    @Override
    public String getName() {
        return "Recipes";
    }

    private static class Generator extends RecipeGenerator {
        protected Generator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
            super(wrapperLookup, recipeExporter);
        }

        @Override
        public void generate() {
            createShaped(RecipeCategory.DECORATIONS, ModBlocks.COPPER_CAULDRON)
                    .input('C', Items.COPPER_INGOT)
                    .pattern("C C")
                    .pattern("C C")
                    .pattern("CCC")
                    .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                    .offerTo(exporter);

            CauldronBrewingRecipeJsonBuilder.of(registries.getEntryOrThrow(CauldronBrews.WATER), registries.getEntryOrThrow(CauldronBrews.HONEY))
                    .amount(FluidConstants.BUCKET, FluidConstants.BUCKET / 4)
                    .ingredient(2, Items.HONEYCOMB)
                    .heatRange(2, 10)
                    .criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
                    .offerTo(exporter);

            CauldronBrewingRecipeJsonBuilder.of(registries.getEntryOrThrow(CauldronBrews.WATER), registries.getEntryOrThrow(CauldronBrews.MUSHROOM_STEW))
                    .ingredient(Items.BROWN_MUSHROOM)
                    .ingredient(Items.RED_MUSHROOM)
                    .heatRange(2, 20)
                    .criterion("has_crafting_recipe", conditionsFromRecipe(Identifier.ofVanilla("mushroom_stew")))
                    .offerTo(exporter);
            CauldronBrewingRecipeJsonBuilder.of(registries.getEntryOrThrow(CauldronBrews.WATER), registries.getEntryOrThrow(CauldronBrews.RABBIT_STEW))
                    .ingredient(Items.COOKED_RABBIT)
                    .ingredient(Items.BAKED_POTATO)
                    .ingredient(Items.CARROT)
                    .ingredient(Ingredient.ofItems(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM))
                    .heatRange(2, 20)
                    .criterion("has_crafting_recipe_red", conditionsFromRecipe(Identifier.ofVanilla("rabbit_stew_from_red_mushroom")))
                    .criterion("has_crafting_recipe_brown", conditionsFromRecipe(Identifier.ofVanilla("rabbit_stew_from_brown_mushroom")))
                    .offerTo(exporter);
            CauldronBrewingRecipeJsonBuilder.of(registries.getEntryOrThrow(CauldronBrews.WATER), registries.getEntryOrThrow(CauldronBrews.BEETROOT_SOUP))
                    .ingredient(4, Items.BEETROOT)
                    .heatRange(2, 20)
                    .criterion("has_crafting_recipe", conditionsFromRecipe(Identifier.ofVanilla("beetroot_soup")))
                    .offerTo(exporter);

            Registries.ITEM.stream().forEach(item -> {
                var stewIngredient = SuspiciousStewIngredient.of(item);
                if (stewIngredient != null) {
                    offerSuspiciousStewCauldronRecipe(item, stewIngredient);
                }
            });
        }

        private void offerSuspiciousStewCauldronRecipe(Item item, SuspiciousStewIngredient stewIngredient) {
            CauldronBrewingRecipeJsonBuilder.of(registries.getEntryOrThrow(CauldronBrews.MUSHROOM_STEW), registries.getEntryOrThrow(CauldronBrews.SUSPICIOUS_STEW))
                    .ingredient(item)
                    .heatRange(2, 20)
                    .outputComponent(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, stewIngredient.getStewEffects())
                    .criterion("has_ingredient", conditionsFromItem(item))
                    .offerTo(exporter, CopperCauldron.MOD_ID + ":suspicious_stew_from_" + getItemPath(item));
        }

        private static AdvancementCriterion<RecipeUnlockedCriterion.Conditions> conditionsFromRecipe(Identifier id) {
            var key = RegistryKey.of(RegistryKeys.RECIPE, id);
            return Criteria.RECIPE_UNLOCKED.create(new RecipeUnlockedCriterion.Conditions(Optional.empty(), key));
        }
    }
}
