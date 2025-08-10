package dev.mattidragon.coppercauldron.behaviour;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public enum EmptyCauldronContent implements CauldronContent {
    INSTANCE;

    private static final MapCodec<EmptyCauldronContent> CODEC = MapCodec.unit(EmptyCauldronContent.INSTANCE);
    private static final PacketCodec<PacketByteBuf, EmptyCauldronContent> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    public static final CauldronContentType<EmptyCauldronContent> TYPE = new CauldronContentType<>(CODEC, PACKET_CODEC);

    @Override
    public CauldronContentType<?> type() {
        return TYPE;
    }
}
