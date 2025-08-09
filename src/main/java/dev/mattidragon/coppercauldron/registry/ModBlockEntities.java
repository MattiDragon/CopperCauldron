package dev.mattidragon.coppercauldron.registry;

import dev.mattidragon.coppercauldron.CopperCauldron;
import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

public class ModBlockEntities {
    public static final BlockEntityType<CopperCauldronBlockEntity> COPPER_CAULDRON
            = FabricBlockEntityTypeBuilder.create(CopperCauldronBlockEntity::new, ModBlocks.COPPER_CAULDRON).build();

    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, CopperCauldron.id("copper_cauldron"), COPPER_CAULDRON);
    }
}
