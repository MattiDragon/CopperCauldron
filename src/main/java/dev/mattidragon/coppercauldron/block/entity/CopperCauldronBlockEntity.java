package dev.mattidragon.coppercauldron.block.entity;

import dev.mattidragon.coppercauldron.behaviour.CauldronContent;
import dev.mattidragon.coppercauldron.behaviour.EmptyCauldronContent;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public class CopperCauldronBlockEntity extends BlockEntity {
    private int heat = 0;
    private CauldronContent content = EmptyCauldronContent.INSTANCE;

    public CopperCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COPPER_CAULDRON, blockPos, blockState);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
    }

    @Override
    protected void readData(ReadView readView) {
        heat = readView.getInt("heat", 0);
        content = readView.read("content", CauldronContent.CODEC).orElse(EmptyCauldronContent.INSTANCE);
    }

    @Override
    protected void writeData(WriteView writeView) {
        writeView.putInt("heat", heat);
        writeView.put("content", CauldronContent.CODEC, content);
    }

    public int heat() {
        return heat;
    }

    public CauldronContent content() {
        return content;
    }
}
