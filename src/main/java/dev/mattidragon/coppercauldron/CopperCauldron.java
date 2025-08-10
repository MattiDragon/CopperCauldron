package dev.mattidragon.coppercauldron;

import dev.mattidragon.coppercauldron.behaviour.CauldronContentType;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import dev.mattidragon.coppercauldron.registry.ModBlocks;
import dev.mattidragon.coppercauldron.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopperCauldron implements ModInitializer {
	public static final String MOD_ID = "copper_cauldron";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        CauldronContentType.register();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> entries.addAfter(Items.CAULDRON, ModItems.COPPER_CAULDRON));
	}

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}