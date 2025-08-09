package dev.mattidragon.coppercauldron.block.entity;

import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CopperCauldronBlockEntity extends BlockEntity {
    public CopperCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COPPER_CAULDRON, blockPos, blockState);
    }
}
