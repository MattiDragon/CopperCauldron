package dev.mattidragon.coppercauldron.registry;

import dev.mattidragon.coppercauldron.CopperCauldron;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModItems {
    private static final Map<Identifier, Item> UNREGISTERED = new HashMap<>();

    public static final BlockItem COPPER_CAULDRON = register(
            "copper_cauldron",
            new Item.Settings().useBlockPrefixedTranslationKey(),
            settings -> new BlockItem(ModBlocks.COPPER_CAULDRON, settings)
    );

    private static <T extends Item> T register(String path, Item.Settings settings, Function<Item.Settings, T> factory) {
        var id = CopperCauldron.id(path);
        settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, id));
        var block = factory.apply(settings);
        UNREGISTERED.put(id, block);
        return block;
    }

    public static void register() {
        UNREGISTERED.forEach((id, block) -> Registry.register(Registries.ITEM, id, block));
        UNREGISTERED.clear();
    }
}
