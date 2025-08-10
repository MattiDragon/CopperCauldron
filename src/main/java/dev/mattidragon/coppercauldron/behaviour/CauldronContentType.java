package dev.mattidragon.coppercauldron.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.Map;

public record CauldronContentType<T extends CauldronContent>(
        MapCodec<T> codec,
        PacketCodec<? super RegistryByteBuf, T> packetCodec
) {
    private static final Map<String, CauldronContentType<?>> REGISTRY = new HashMap<>();
    private static final Map<CauldronContentType<?>, String> REVERSE_REGISTRY = new HashMap<>();

    public static final Codec<CauldronContentType<?>> CODEC = Codec.STRING.comapFlatMap(
            id -> REGISTRY.containsKey(id) ? DataResult.success(REGISTRY.get(id)) : DataResult.error(() -> "Unknown cauldron content type: " + id),
            REVERSE_REGISTRY::get
    );
    public static final PacketCodec<ByteBuf, CauldronContentType<?>> PACKET_CODEC
            = PacketCodecs.STRING.xmap(REGISTRY::get, REVERSE_REGISTRY::get);

    public static final CauldronContentType<EmptyCauldronContent> EMPTY = register("empty", EmptyCauldronContent.TYPE);
    public static final CauldronContentType<FluidCauldronContent> FLUID = register("fluid", FluidCauldronContent.TYPE);

    public static <T extends CauldronContent> CauldronContentType<T> register(String id, CauldronContentType<T> type) {
        REGISTRY.put(id, type);
        REVERSE_REGISTRY.put(type, id);
        return type;
    }

    public static void register() {
    }
}
