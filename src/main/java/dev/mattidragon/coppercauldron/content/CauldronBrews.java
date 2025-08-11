package dev.mattidragon.coppercauldron.content;

import dev.mattidragon.coppercauldron.CopperCauldron;
import net.minecraft.registry.RegistryKey;

public class CauldronBrews {
    public static final RegistryKey<CauldronBrew> EMPTY = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("empty"));
    public static final RegistryKey<CauldronBrew> WATER = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("water"));
    public static final RegistryKey<CauldronBrew> POTION = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("potion"));
    public static final RegistryKey<CauldronBrew> HONEY = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("honey"));

    private CauldronBrews() {
    }
}
