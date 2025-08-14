package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.content.CauldronBrews;
import dev.mattidragon.coppercauldron.datagen.util.CauldronBrewingRecipeJsonBuilder;
import dev.mattidragon.coppercauldron.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

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
                    .criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
                    .offerTo(exporter);
        }
    }
}
