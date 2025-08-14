package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.content.CauldronBrew;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BrewProvider extends FabricDynamicRegistryProvider {
    public BrewProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(CauldronBrew.REGISTRY_KEY));
    }

    @Override
    public String getName() {
        return "Cauldron Brews";
    }
}
