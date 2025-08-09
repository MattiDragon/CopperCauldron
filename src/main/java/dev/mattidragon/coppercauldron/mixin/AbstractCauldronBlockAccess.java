package dev.mattidragon.coppercauldron.mixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractCauldronBlock.class)
public interface AbstractCauldronBlockAccess {
    @Accessor("RAYCAST_SHAPE")
    static VoxelShape getRaycastShape() {
        throw new AssertionError("Accessor fail");
    }

    @Accessor("OUTLINE_SHAPE")
    static VoxelShape getOutlineShape() {
        throw new AssertionError("Accessor fail");
    }
}
