package dev.mattidragon.coppercauldron.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;

public record SizedIngredient(Ingredient ingredient, int count) {
    public static final Codec<SizedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
            Codec.intRange(0, 99).fieldOf("count").forGetter(SizedIngredient::count)
    ).apply(instance, SizedIngredient::new));

    public static final PacketCodec<RegistryByteBuf, SizedIngredient> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, SizedIngredient::ingredient,
            PacketCodecs.INTEGER, SizedIngredient::count,
            SizedIngredient::new
    );
}
