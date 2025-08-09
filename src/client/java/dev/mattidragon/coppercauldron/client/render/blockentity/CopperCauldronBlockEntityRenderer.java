package dev.mattidragon.coppercauldron.client.render.blockentity;

import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class CopperCauldronBlockEntityRenderer implements BlockEntityRenderer<CopperCauldronBlockEntity> {
    public CopperCauldronBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CopperCauldronBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {

    }
}
