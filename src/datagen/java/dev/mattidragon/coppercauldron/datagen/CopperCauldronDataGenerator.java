package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

public class CopperCauldronDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(ReadmeDataProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(BrewProvider::new);
	}

    @Override
    public @Nullable String getEffectiveModId() {
        return CopperCauldron.MOD_ID;
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(CauldronBrew.REGISTRY_KEY, CauldronBrews::bootstrap);
    }
}
