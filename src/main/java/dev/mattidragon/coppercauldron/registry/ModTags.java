package dev.mattidragon.coppercauldron.registry;

import dev.mattidragon.coppercauldron.CopperCauldron;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    private ModTags() {
    }

    public static class BlockTags {
        public static final TagKey<Block> TEMPERATURE_VERY_HOT = TagKey.of(RegistryKeys.BLOCK, CopperCauldron.id("temperature/very_hot"));
        public static final TagKey<Block> TEMPERATURE_HOT = TagKey.of(RegistryKeys.BLOCK, CopperCauldron.id("temperature/hot"));
        public static final TagKey<Block> TEMPERATURE_COLD = TagKey.of(RegistryKeys.BLOCK, CopperCauldron.id("temperature/cold"));
        public static final TagKey<Block> TEMPERATURE_VERY_COLD = TagKey.of(RegistryKeys.BLOCK, CopperCauldron.id("temperature/very_cold"));
    }
}
