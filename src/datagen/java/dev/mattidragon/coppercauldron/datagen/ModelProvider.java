package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.registry.ModBlocks;
import dev.mattidragon.coppercauldron.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.minecraft.client.data.TextureMap.getSubId;

public class ModelProvider extends FabricModelProvider {
    public static final Model CAULDRON_MODEL = new Model(
            Optional.of(Identifier.ofVanilla("block/cauldron")),
            Optional.empty(),
            TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE
    );

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(
                ModBlocks.COPPER_CAULDRON,
                BlockStateModelGenerator.createWeightedVariant(
                        CAULDRON_MODEL.upload(ModBlocks.COPPER_CAULDRON, makeTextureMap(), generator.modelCollector))));
        generator.registerItemModel(ModItems.COPPER_CAULDRON);
    }

    private static TextureMap makeTextureMap() {
        return new TextureMap()
                .put(TextureKey.PARTICLE, getSubId(ModBlocks.COPPER_CAULDRON, "_side"))
                .put(TextureKey.SIDE, getSubId(ModBlocks.COPPER_CAULDRON, "_side"))
                .put(TextureKey.TOP, getSubId(ModBlocks.COPPER_CAULDRON, "_top"))
                .put(TextureKey.BOTTOM, getSubId(ModBlocks.COPPER_CAULDRON, "_bottom"))
                .put(TextureKey.INSIDE, getSubId(ModBlocks.COPPER_CAULDRON, "_inner"));
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {

    }
}
