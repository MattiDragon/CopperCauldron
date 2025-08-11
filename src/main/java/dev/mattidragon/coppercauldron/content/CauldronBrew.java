package dev.mattidragon.coppercauldron.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mattidragon.coppercauldron.CopperCauldron;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.component.ComponentMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public record CauldronBrew(
        BrewVisual visualForm,
        Optional<FluidForm> fluidForm,
        Optional<ItemForm> itemForm,
        ComponentMap components
) {
    private static final Codec<CauldronBrew> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BrewVisual.CODEC.fieldOf("visual_form").forGetter(CauldronBrew::visualForm),
            FluidForm.CODEC.optionalFieldOf("fluid_form").forGetter(CauldronBrew::fluidForm),
            ItemForm.CODEC.optionalFieldOf("item_form").forGetter(CauldronBrew::itemForm),
            ComponentMap.CODEC.optionalFieldOf("components")
                    .xmap(
                            optional -> optional.orElse(ComponentMap.EMPTY),
                            map -> Optional.of(map).filter(Predicate.not(ComponentMap::isEmpty))
                    )
                    .forGetter(CauldronBrew::components)
    ).apply(instance, CauldronBrew::new));

    public static final RegistryKey<Registry<CauldronBrew>> REGISTRY_KEY = RegistryKey.ofRegistry(CopperCauldron.id("brew"));

    public static void registerRegistry() {
        DynamicRegistries.registerSynced(REGISTRY_KEY, CODEC);
    }

    public static @Nullable RegistryEntry<CauldronBrew> fromItem(Item item, RegistryWrapper.WrapperLookup registries) {
        return registries.getOrThrow(CauldronBrew.REGISTRY_KEY)
                .streamEntries()
                .filter(entry -> {
                    var brew = entry.comp_349();

                    if (brew.itemForm().isEmpty()) return false;
                    var itemForm = brew.itemForm().get();

                    return itemForm.item() == item;
                })
                .findFirst()
                .orElse(null);
    }

    public record FluidForm(Fluid fluid) {
        public static final Codec<FluidForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.FLUID.getCodec().fieldOf("fluid").forGetter(FluidForm::fluid)
        ).apply(instance, FluidForm::new));
    }

    public record ItemForm(Item item, Optional<Ingredient> container, Optional<SoundEvent> fillSound, Optional<SoundEvent> emptySound, long amountPerItem) {
        public static final Codec<ItemForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemForm::item),
                Ingredient.CODEC.optionalFieldOf("container").forGetter(ItemForm::container),
                SoundEvent.CODEC.optionalFieldOf("fill_sound").forGetter(ItemForm::fillSound),
                SoundEvent.CODEC.optionalFieldOf("empty_sound").forGetter(ItemForm::emptySound),
                Codec.LONG.fieldOf("amount_per_item").forGetter(ItemForm::amountPerItem)
        ).apply(instance, ItemForm::new));
    }
}
