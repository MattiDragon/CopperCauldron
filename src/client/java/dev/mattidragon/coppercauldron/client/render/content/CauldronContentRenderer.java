package dev.mattidragon.coppercauldron.client.render.content;

import dev.mattidragon.coppercauldron.behaviour.CauldronContent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface CauldronContentRenderer<T extends CauldronContent> {
    void render(T content, BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
