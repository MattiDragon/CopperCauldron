package dev.mattidragon.coppercauldron.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronContent;
import dev.mattidragon.coppercauldron.registry.ModRecipes;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record CauldronBrewingRecipe(
        RegistryEntry<CauldronBrew> inputBrew,
        long inputAmount,
        ComponentChanges componentFilter,
        RegistryEntry<CauldronBrew> outputBrew,
        long outputAmount,
        ComponentChanges outputComponents,
        Set<ComponentType<?>> copiedComponents,
        List<SizedIngredient> ingredients,
        int minHeat,
        int maxHeat,
        int processingTime
) implements CauldronRecipe {
    private static final MapCodec<CauldronBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryFixedCodec.of(CauldronBrew.REGISTRY_KEY).fieldOf("input_brew").forGetter(CauldronBrewingRecipe::inputBrew),
            Codec.LONG.fieldOf("input_amount").forGetter(CauldronBrewingRecipe::inputAmount),
            ComponentChanges.CODEC.optionalFieldOf("component_filter")
                    .xmap(
                            optional -> optional.orElse(ComponentChanges.EMPTY),
                            outputComponents -> outputComponents.isEmpty() ? Optional.empty() : Optional.of(outputComponents)
                    )
                    .forGetter(CauldronBrewingRecipe::componentFilter),
            RegistryFixedCodec.of(CauldronBrew.REGISTRY_KEY).fieldOf("output_brew").forGetter(CauldronBrewingRecipe::outputBrew),
            Codec.LONG.fieldOf("output_amount").forGetter(CauldronBrewingRecipe::outputAmount),
            ComponentChanges.CODEC.optionalFieldOf("output_components")
                    .xmap(
                            optional -> optional.orElse(ComponentChanges.EMPTY),
                            outputComponents -> outputComponents.isEmpty() ? Optional.empty() : Optional.of(outputComponents)
                    )
                    .forGetter(CauldronBrewingRecipe::outputComponents),
            Registries.DATA_COMPONENT_TYPE.getCodec().listOf()
                    .xmap(Set::copyOf, List::copyOf)
                    .optionalFieldOf("copied_components")
                    .xmap(
                            optional -> optional.orElse(Set.of()),
                            copiedComponents -> copiedComponents.isEmpty() ? Optional.empty() : Optional.of(copiedComponents)
                    )
                    .forGetter(CauldronBrewingRecipe::copiedComponents),
            SizedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(CauldronBrewingRecipe::ingredients),
            Codec.INT.fieldOf("min_heat").orElse(0).forGetter(CauldronBrewingRecipe::minHeat),
            Codec.INT.optionalFieldOf("max_heat")
                    .xmap(o -> o.orElse(Integer.MAX_VALUE), i -> i == Integer.MAX_VALUE ? Optional.empty() : Optional.of(i))
                    .forGetter(CauldronBrewingRecipe::maxHeat),
            Codec.INT.fieldOf("processing_time").orElse(40).forGetter(CauldronBrewingRecipe::processingTime)
    ).apply(instance, CauldronBrewingRecipe::new));

    private static final PacketCodec<RegistryByteBuf, CauldronBrewingRecipe> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.registryEntry(CauldronBrew.REGISTRY_KEY), CauldronBrewingRecipe::inputBrew,
            PacketCodecs.LONG, CauldronBrewingRecipe::inputAmount,
            ComponentChanges.PACKET_CODEC, CauldronBrewingRecipe::componentFilter,
            PacketCodecs.registryEntry(CauldronBrew.REGISTRY_KEY), CauldronBrewingRecipe::outputBrew,
            PacketCodecs.LONG, CauldronBrewingRecipe::outputAmount,
            ComponentChanges.PACKET_CODEC, CauldronBrewingRecipe::outputComponents,
            PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE).collect(PacketCodecs.toCollection(HashSet::new)), CauldronBrewingRecipe::copiedComponents,
            SizedIngredient.PACKET_CODEC.collect(PacketCodecs.toList()), CauldronBrewingRecipe::ingredients,
            PacketCodecs.INTEGER, CauldronBrewingRecipe::minHeat,
            PacketCodecs.INTEGER, CauldronBrewingRecipe::maxHeat,
            PacketCodecs.INTEGER, CauldronBrewingRecipe::processingTime,
            CauldronBrewingRecipe::new
    );

    @Override
    public boolean matches(CauldronRecipeContent input, World world) {
        if (input.heat() < minHeat || input.heat() > maxHeat) return false;
        if (!input.hasContent(inputBrew, componentFilter)) return false;

        var numCrafts = input.amount() / inputAmount;

        var testItems = input.items().stream()
                .map(ItemStack::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        if (!performIngredientRemoval((int) numCrafts, testItems)) {
            return false;
        }

        return numCrafts >= 1 && inputAmount * numCrafts == input.amount();
    }

    @Override
    public CauldronRecipeContent apply(CauldronRecipeContent input, RegistryWrapper.WrapperLookup wrapperLookup) {
        var outputComponents = ComponentMap.builder();
        for (var type : copiedComponents) {
            addComponent(type, input.content().components(), outputComponents);
        }
        var merged = MergedComponentMap.create(outputComponents.build(), this.outputComponents)
                .filtered(outputBrew.value().components()::contains);
        var outputContent = new CauldronContent(outputBrew, merged);

        var numCrafts = (int) (input.amount() / inputAmount);

        var outputItems = input.items().stream()
                .map(ItemStack::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        if (!performIngredientRemoval(numCrafts, outputItems)) {
            CopperCauldron.LOGGER.warn("Failed to remove ingredients for recipe {}. Not enough ingredients in input.", this);
            return input;
        }

        return new CauldronRecipeContent(
                outputContent,
                outputAmount * numCrafts,
                outputItems.stream()
                        .filter(Predicate.not(ItemStack::isEmpty))
                        .toList(),
                input.heat()
        );
    }

    private boolean performIngredientRemoval(int numCrafts, List<ItemStack> items) {
        for (var ingredient : ingredients) {
            var totalToRemove = ingredient.count() * numCrafts;
            if (totalToRemove <= 0) continue;

            for (var item : items) {
                if (ingredient.ingredient().test(item)) {
                    var countToRemove = Math.min(totalToRemove, item.getCount());
                    item.decrement(countToRemove);
                    totalToRemove -= countToRemove;
                }
            }

            if (totalToRemove > 0) {
                return false;
            }
        }
        return true;
    }

    private static <T> void addComponent(ComponentType<T> type, ComponentMap components, ComponentMap.Builder outputComponents) {
        outputComponents.add(type, components.get(type));
    }

    @Override
    public RecipeSerializer<? extends Recipe<CauldronRecipeContent>> getSerializer() {
        return ModRecipes.CAULDRON_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<CauldronRecipeContent>> getType() {
        return ModRecipes.CAULDRON_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
        @Override
        public MapCodec<CauldronBrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        @Deprecated
        public PacketCodec<RegistryByteBuf, CauldronBrewingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
