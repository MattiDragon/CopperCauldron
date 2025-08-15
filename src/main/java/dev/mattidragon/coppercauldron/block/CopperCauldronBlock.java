package dev.mattidragon.coppercauldron.block;

import com.mojang.serialization.MapCodec;
import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import dev.mattidragon.coppercauldron.mixin.AbstractCauldronBlockAccess;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class CopperCauldronBlock extends BlockWithEntity {
    public CopperCauldronBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (!(world.getBlockEntity(pos)instanceof CopperCauldronBlockEntity blockEntity)) return ActionResult.FAIL;

        if (FluidStorageUtil.interactWithFluidStorage(blockEntity.fluidStorage(), player, hand)) {
            return ActionResult.SUCCESS;
        } else if (blockEntity.extractWithContainer(player, hand)) {
            return ActionResult.SUCCESS;
        } else if (blockEntity.insertWithContainer(player, hand)) {
            return ActionResult.SUCCESS;
        } else if (blockEntity.insertItems(player, hand)) {
            return ActionResult.CONSUME;
        } else {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos)instanceof CopperCauldronBlockEntity blockEntity)) return ActionResult.FAIL;

        if (blockEntity.extractItems(player)) {
            return ActionResult.CONSUME;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!(world.getBlockEntity(pos)instanceof CopperCauldronBlockEntity blockEntity)) return;
        blockEntity.updateCauldronHeat();
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

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, ModBlockEntities.COPPER_CAULDRON, CopperCauldronBlockEntity::tick);
    }
}
