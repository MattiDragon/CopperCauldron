package dev.mattidragon.coppercauldron.client.render.blockentity;

import dev.mattidragon.coppercauldron.behaviour.CauldronContent;
import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import dev.mattidragon.coppercauldron.client.render.content.CauldronContentRenderers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class CopperCauldronBlockEntityRenderer implements BlockEntityRenderer<CopperCauldronBlockEntity> {
    public CopperCauldronBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CopperCauldronBlockEntity blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        renderContent(blockEntity.content(), blockEntity, tickProgress, matrices, vertexConsumers, light, overlay);
    }

    private <T extends CauldronContent> void renderContent(T content, BlockEntity blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        CauldronContentRenderers.getRenderer(content).render(content, blockEntity, tickProgress, matrices, vertexConsumers, light, overlay);
    }
}
