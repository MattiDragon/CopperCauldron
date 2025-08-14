package dev.mattidragon.coppercauldron.content;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;

import java.util.Map;

public record CauldronContent(RegistryEntry<CauldronBrew> brew, ComponentMap components) {
    public static final Codec<CauldronContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryFixedCodec.of(CauldronBrew.REGISTRY_KEY).fieldOf("brew").forGetter(CauldronContent::brew),
            ComponentMap.CODEC.fieldOf("components").forGetter(CauldronContent::components)
    ).apply(instance, CauldronContent::new));

    private static final Map<CauldronContent, FluidVariant> TO_FLUID_VARIANT_CACHE = new MapMaker().weakKeys().makeMap();
    // Each registry manager gets its own cache because CauldronContent contains registry entries that can't be shared
    private static final Map<RegistryWrapper.WrapperLookup, Map<FluidVariant, CauldronContent>> FROM_FLUID_VARIANT_CACHE
            = new MapMaker().weakKeys().makeMap();


    public static CauldronContent getEmpty(RegistryWrapper.WrapperLookup registries) {
        var brew = registries.getEntryOrThrow(CauldronBrews.EMPTY);
        return new CauldronContent(brew, brew.value().components());
    }

    public FluidVariant toFluidVariant() {
        return TO_FLUID_VARIANT_CACHE.computeIfAbsent(this, content -> {
            var brew = content.brew().value();
            if (brew.fluidForm().isPresent()) {
                var components = ComponentChanges.builder();
                brew.components().forEach(components::add);

                return FluidVariant.of(brew.fluidForm().get().fluid(), components.build());
            } else {
                return FluidVariant.blank();
            }
        });
    }

    public static CauldronContent fromFluidVariant(FluidVariant variant, RegistryWrapper.WrapperLookup registries) {
        return FROM_FLUID_VARIANT_CACHE.computeIfAbsent(registries, r -> new MapMaker().weakValues().makeMap())
                .computeIfAbsent(variant, v -> {
                    if (v.isBlank()) {
                        return getEmpty(registries);
                    }

                    return registries.getOrThrow(CauldronBrew.REGISTRY_KEY)
                            .streamEntries()
                            .filter(entry -> {
                                var brew = entry.value();

                                if (brew.fluidForm().isEmpty()) return false;
                                var fluid = brew.fluidForm().get().fluid();

                                if (fluid != v.getFluid()) return false;
                                return v.getComponentMap()
                                        .getTypes()
                                        .stream()
                                        .allMatch(brew.components()::contains);
                            })
                            .findFirst()
                            .map(brew -> new CauldronContent(brew, variant.getComponentMap()))
                            .orElse(null);
                });
    }
}
