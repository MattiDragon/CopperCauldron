package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ModTags.BlockTags.TEMPERATURE_VERY_HOT)
                .add(Blocks.LAVA, Blocks.SOUL_FIRE, Blocks.SOUL_CAMPFIRE);
        valueLookupBuilder(ModTags.BlockTags.TEMPERATURE_HOT)
                .add(Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.CAMPFIRE);
        valueLookupBuilder(ModTags.BlockTags.TEMPERATURE_COLD)
                .add(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.ICE, Blocks.PACKED_ICE, Blocks.FROSTED_ICE);
        valueLookupBuilder(ModTags.BlockTags.TEMPERATURE_VERY_COLD)
                .add(Blocks.BLUE_ICE);
    }
}
