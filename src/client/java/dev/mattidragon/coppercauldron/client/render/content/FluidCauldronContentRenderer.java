package dev.mattidragon.coppercauldron.client.render.content;

import dev.mattidragon.coppercauldron.behaviour.FluidCauldronContent;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class FluidCauldronContentRenderer implements CauldronContentRenderer<FluidCauldronContent> {
    @Override
    public void render(FluidCauldronContent content, BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var fluidLevel = MathHelper.lerp((float) content.amount() / FluidConstants.BUCKET, 5f, 15f);

        var sprite = FluidVariantRendering.getSprite(content.fluid());
        var color = FluidVariantRendering.getColor(content.fluid(), entity.getWorld(), entity.getPos());
        if (sprite == null) return;

        var minU = sprite.getMinU();
        var maxU = sprite.getMaxU();
        var minV = sprite.getMinV();
        var maxV = sprite.getMaxV();

        var u1 = minU + (maxU - minU) * (2 / 16f);
        var u2 = maxU - (maxU - minU) * (2 / 16f);
        var v1 = minV + (maxV - minV) * (2 / 16f);
        var v2 = maxV - (maxV - minV) * (2 / 16f);

        var renderLayer = RenderLayer.getEntityTranslucent(sprite.getAtlasId());
        var vertexConsumer = vertexConsumers.getBuffer(renderLayer);

        matrices.push();
        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);

        vertexConsumer.vertex(matrices.peek(), 2, fluidLevel, 2).color(color).texture(u1, v1).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 14, fluidLevel, 2).color(color).texture(u2, v1).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 14, fluidLevel, 14).color(color).texture(u2, v2).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 2, fluidLevel, 14).color(color).texture(u1, v2).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);

        matrices.pop();
    }
}
