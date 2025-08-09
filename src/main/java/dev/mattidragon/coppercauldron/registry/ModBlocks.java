package dev.mattidragon.coppercauldron.registry;

import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.block.CopperCauldronBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    private static final Map<Identifier, Block> UNREGISTERED = new HashMap<>();

    public static final CopperCauldronBlock COPPER_CAULDRON
            = register("copper_cauldron", AbstractBlock.Settings.create(), CopperCauldronBlock::new);

    private static <T extends Block> T register(String path, AbstractBlock.Settings settings, Function<AbstractBlock.Settings, T> factory) {
        var id = CopperCauldron.id(path);
        settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id));
        var block = factory.apply(settings);
        UNREGISTERED.put(id, block);
        return block;
    }

    public static void register() {
        UNREGISTERED.forEach((id, block) -> Registry.register(Registries.BLOCK, id, block));
        UNREGISTERED.clear();
    }
}
