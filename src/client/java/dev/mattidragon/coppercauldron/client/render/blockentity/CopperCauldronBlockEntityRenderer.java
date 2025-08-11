package dev.mattidragon.coppercauldron.client.render.blockentity;

import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import dev.mattidragon.coppercauldron.content.BrewVisual;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CopperCauldronBlockEntityRenderer implements BlockEntityRenderer<CopperCauldronBlockEntity> {
    public CopperCauldronBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CopperCauldronBlockEntity blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        renderContent(blockEntity, matrices, vertexConsumers, light, overlay);
    }

    private void renderContent(CopperCauldronBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var content = blockEntity.content();

        Sprite sprite;
        int color = 0xffffffff;
        switch (content.brew().comp_349().visualForm()) {
            case BrewVisual.EmptyVisual emptyVisual -> {
                return;
            }
            case BrewVisual.FluidVisual fluidVisual -> {
                var variant = content.toFluidVariant();
                sprite = FluidVariantRendering.getSprite(variant);
                color = FluidVariantRendering.getColor(variant, blockEntity.getWorld(), blockEntity.getPos());
            }
            case BrewVisual.PotionVisual potionVisual -> {
                // TODO: custom sprite?
                sprite = FluidVariantRendering.getSprite(FluidVariant.of(Fluids.WATER));
                color = content.components()
                        .getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
                        .getColor();
            }
            case BrewVisual.TextureVisual textureVisual -> {
                @SuppressWarnings("deprecation")
                var atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                sprite = atlas.apply(textureVisual.id());
            }
        }

        var level = MathHelper.lerp((float) blockEntity.amount() / FluidConstants.BUCKET, 5f, 15f);

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

        vertexConsumer.vertex(matrices.peek(), 2, level, 2).color(color).texture(u1, v1).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 14, level, 2).color(color).texture(u2, v1).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 14, level, 14).color(color).texture(u2, v2).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);
        vertexConsumer.vertex(matrices.peek(), 2, level, 14).color(color).texture(u1, v2).overlay(overlay).light(light).normal(matrices.peek(), 0, 1, 0);

        matrices.pop();
    }
}
