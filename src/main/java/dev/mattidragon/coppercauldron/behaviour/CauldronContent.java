package dev.mattidragon.coppercauldron.behaviour;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface CauldronContent {
    Codec<CauldronContent> CODEC = CauldronContentType.CODEC.dispatch(CauldronContent::type, CauldronContentType::codec);
    PacketCodec<RegistryByteBuf, CauldronContent> PACKET_CODEC = CauldronContentType.PACKET_CODEC
            .<RegistryByteBuf>cast()
            .dispatch(CauldronContent::type, CauldronContentType::packetCodec);

    CauldronContentType<?> type();
}
