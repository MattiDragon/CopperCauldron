package dev.mattidragon.coppercauldron.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class FluidCauldronContent implements CauldronContent {
    private static final MapCodec<FluidCauldronContent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidVariant.CODEC.fieldOf("fluid").forGetter(FluidCauldronContent::fluid),
            Codec.LONG.fieldOf("amount").forGetter(FluidCauldronContent::amount)
    ).apply(instance, (fluid, amount) -> new FluidCauldronContent(fluid).amount(amount)));
    private static final PacketCodec<RegistryByteBuf, FluidCauldronContent> PACKET_CODEC = PacketCodec.tuple(
            FluidVariant.PACKET_CODEC, FluidCauldronContent::fluid,
            PacketCodecs.LONG, FluidCauldronContent::amount,
            (fluid, amount) -> new FluidCauldronContent(fluid).amount(amount)
    );

    public static final CauldronContentType<FluidCauldronContent> TYPE = new CauldronContentType<>(CODEC, PACKET_CODEC);

    private final FluidVariant fluid;
    private long amount;

    public FluidCauldronContent(FluidVariant fluid) {
        this.fluid = fluid;
    }

    public FluidVariant fluid() {
        return fluid;
    }

    public long amount() {
        return amount;
    }

    public FluidCauldronContent amount(long amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public CauldronContentType<?> type() {
        return TYPE;
    }
}
