package dev.mattidragon.coppercauldron.block;

import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import dev.mattidragon.coppercauldron.mixin.AbstractCauldronBlockAccess;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CopperCauldronBlock extends Block implements BlockEntityProvider {
    public CopperCauldronBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return AbstractCauldronBlockAccess.getRaycastShape();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        return AbstractCauldronBlockAccess.getOutlineShape();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CopperCauldronBlockEntity(blockPos, blockState);
    }
}
