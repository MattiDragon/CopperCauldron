package dev.mattidragon.coppercauldron.datagen.util;

import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.recipe.CauldronBrewingRecipe;
import dev.mattidragon.coppercauldron.recipe.SizedIngredient;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class CauldronBrewingRecipeJsonBuilder {
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    private final RegistryEntry<CauldronBrew> inputBrew;
    private long inputAmount = FluidConstants.BUCKET;
    private final ComponentChanges.Builder componentFilter = ComponentChanges.builder();

    private final RegistryEntry<CauldronBrew> outputBrew;
    private long outputAmount = FluidConstants.BUCKET;
    private final ComponentChanges.Builder outputComponents = ComponentChanges.builder();

    private final List<SizedIngredient> ingredients = new ArrayList<>();
    private final Set<ComponentType<?>> copiedComponents = new HashSet<>();

    private int minHeat = 0;
    private int maxHeat = Integer.MAX_VALUE;
    private boolean heatSet = false;
    private int processingAmount = 40;

    private CauldronBrewingRecipeJsonBuilder(RegistryEntry<CauldronBrew> inputBrew, RegistryEntry<CauldronBrew> outputBrew) {
        this.inputBrew = inputBrew;
        this.outputBrew = outputBrew;
    }

    public static CauldronBrewingRecipeJsonBuilder of(RegistryEntry<CauldronBrew> inputBrew, RegistryEntry<CauldronBrew> outputBrew) {
        return new CauldronBrewingRecipeJsonBuilder(inputBrew, outputBrew);
    }

    public CauldronBrewingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder ingredient(ItemConvertible item) {
        return this.ingredient(1, Ingredient.ofItem(item));
    }

    public CauldronBrewingRecipeJsonBuilder ingredient(Ingredient ingredient) {
        return this.ingredient(1, ingredient);
    }

    public CauldronBrewingRecipeJsonBuilder ingredient(int amount, ItemConvertible item) {
        return this.ingredient(amount, Ingredient.ofItem(item));
    }

    public CauldronBrewingRecipeJsonBuilder ingredient(int amount, Ingredient ingredient) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        ingredients.add(new SizedIngredient(ingredient, amount));
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder amount(long amount) {
        return amount(amount, amount);
    }

    public CauldronBrewingRecipeJsonBuilder amount(long inputAmount, long outputAmount) {
        if (inputAmount <= 0 || outputAmount <= 0) {
            throw new IllegalArgumentException("Amounts must be greater than zero");
        }
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder processingTime(int processingAmount) {
        if (processingAmount < 1) {
            throw new IllegalArgumentException("Processing amount must be greater than zero");
        }
        this.processingAmount = processingAmount;
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder minHeat(int minHeat) {
        if (heatSet) {
            throw new IllegalStateException("Heat range has already been set");
        }
        this.minHeat = minHeat;
        this.maxHeat = Integer.MAX_VALUE;
        this.heatSet = true;
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder maxHeat(int maxHeat) {
        if (heatSet) {
            throw new IllegalStateException("Heat range has already been set");
        }
        this.minHeat = Integer.MIN_VALUE;
        this.maxHeat = maxHeat;
        this.heatSet = true;
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder heatRange(int minHeat, int maxHeat) {
        if (heatSet) {
            throw new IllegalStateException("Heat range has already been set");
        }
        this.minHeat = minHeat;
        this.maxHeat = maxHeat;
        this.heatSet = true;
        return this;
    }

    public <T> CauldronBrewingRecipeJsonBuilder requireComponent(ComponentType<T> type, T value) {
        componentFilter.add(type, value);
        return this;
    }

    public <T> CauldronBrewingRecipeJsonBuilder outputComponent(ComponentType<T> type, T value) {
        outputComponents.add(type, value);
        return this;
    }

    public CauldronBrewingRecipeJsonBuilder copyComponents(ComponentType<?>... types) {
        copiedComponents.addAll(Arrays.asList(types));
        return this;
    }

    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> registryKey) {
        var advancementBuilder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(registryKey))
                .rewards(AdvancementRewards.Builder.recipe(registryKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        criteria.forEach(advancementBuilder::criterion);

        if (minHeat > maxHeat) {
            throw new IllegalArgumentException("Minimum heat cannot be greater than maximum heat");
        }

        var recipe = new CauldronBrewingRecipe(
                inputBrew,
                inputAmount,
                componentFilter.build(),
                outputBrew,
                outputAmount,
                outputComponents.build(),
                copiedComponents,
                ingredients,
                minHeat,
                maxHeat,
                processingAmount
        );

        exporter.accept(registryKey, recipe, advancementBuilder.build(registryKey.getValue().withPrefixedPath("recipes/cauldron_brewing/")));
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, getAutoId()));
    }

    public void offerTo(RecipeExporter exporter, String recipePath) {
        var autoId = getAutoId();
        var manualId = Identifier.of(recipePath);
        if (manualId.equals(autoId)) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its path argument as it is equal to default one");
        } else {
            offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, manualId));
        }
    }

    private Identifier getAutoId() {
        return outputBrew.getKey().orElseThrow().getValue();
    }
}
