package dev.mattidragon.coppercauldron.client;

import dev.mattidragon.coppercauldron.client.render.blockentity.CopperCauldronBlockEntityRenderer;
import dev.mattidragon.coppercauldron.client.render.content.CauldronContentRenderers;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CopperCauldronClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.COPPER_CAULDRON, CopperCauldronBlockEntityRenderer::new);
        CauldronContentRenderers.registerDefaultRenderers();
	}
}