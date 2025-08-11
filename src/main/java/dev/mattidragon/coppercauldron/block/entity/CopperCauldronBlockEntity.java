package dev.mattidragon.coppercauldron.block.entity;

import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import dev.mattidragon.coppercauldron.content.CauldronContent;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import dev.mattidragon.coppercauldron.storage.CauldronContentStorage;
import dev.mattidragon.coppercauldron.storage.CauldronFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class CopperCauldronBlockEntity extends BlockEntity {
    private int heat = 0;
    private CauldronContent content;
    private long amount;

    private final CauldronFluidStorage fluidStorage;

    public CopperCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COPPER_CAULDRON, blockPos, blockState);
        var contentStorage = new CauldronContentStorage(this);
        fluidStorage = new CauldronFluidStorage(contentStorage, this::getRegistryManager);
    }

    static {
        FluidStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.fluidStorage, ModBlockEntities.COPPER_CAULDRON);
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
        @SuppressWarnings("deprecation")
        var registries = readView.getRegistries();

        heat = readView.getInt("heat", 0);
        content = readView.read("content", CauldronContent.CODEC).orElseGet(() -> CauldronContent.getEmpty(registries));
        amount = readView.getLong("amount", 0);

        // Ensure sane state
        if (amount == 0) {
            content = CauldronContent.getEmpty(registries);
        } else if (content.brew().matchesKey(CauldronBrews.EMPTY)) {
            amount = 0;
        }
    }

    @Override
    protected void writeData(WriteView writeView) {
        writeView.putInt("heat", heat);
        writeView.put("content", CauldronContent.CODEC, content);
        writeView.putLong("amount", amount);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        // Load brew into field once we get access to DRM if it doesn't yet exist
        if (world != null && content == null) {
            var registries = world.getRegistryManager();
            content = CauldronContent.getEmpty(registries);
        }
    }

    public int heat() {
        return heat;
    }

    public CauldronContent content() {
        return content;
    }

    public long amount() {
        return amount;
    }

    public Storage<FluidVariant> fluidStorage() {
        return fluidStorage;
    }

    public void setContent(CauldronContent content, long amount) {
        this.content = content;
        this.amount = amount;
        markDirty();
    }

    public boolean extractWithContainer(PlayerEntity player, Hand hand) {
        var handStack = player.getStackInHand(hand);

        var brew = content.brew().comp_349();
        if (brew.itemForm().isEmpty()) return false;

        var itemForm = brew.itemForm().get();
        if (itemForm.container().isEmpty()) return false;
        if (!itemForm.container().get().test(handStack)) return false;

        if (amount < itemForm.amountPerItem()) return false;

        handStack.decrement(1);
        var stack = itemForm.item().getDefaultStack();
        stack.applyComponentsFrom(content.components());

        if (handStack.isEmpty()) {
            player.setStackInHand(hand, stack);
        } else {
            player.getInventory().offerOrDrop(stack);
        }

        amount -= itemForm.amountPerItem();
        if (amount <= 0) {
            content = CauldronContent.getEmpty(getRegistryManager());
            amount = 0;
        }

        itemForm.fillSound().ifPresent(sound ->
                player.getWorld().playSound(player, player.getX(), player.getEyeY(), player.getZ(), sound, SoundCategory.PLAYERS, 1, 1));

        return true;
    }

    public boolean insertWithContainer(PlayerEntity player, Hand hand) {
        var handStack = player.getStackInHand(hand);

        var brew = CauldronBrew.fromItem(handStack.getItem(), getRegistryManager());
        if (brew == null) return false;

        var components = ComponentMap.builder();
        for (var component : brew.comp_349().components()) {
            copyComponent(component, handStack.getComponents(), components);
        }

        var content = new CauldronContent(brew, components.build());
        var itemForm = brew.comp_349().itemForm().orElseThrow();
        var toInsert = itemForm.amountPerItem();
        if (amount + toInsert > FluidConstants.BUCKET) {
            return false; // Can't insert more than 1 bucket
        }
        if (!this.content.brew().matchesKey(CauldronBrews.EMPTY) && !this.content.equals(content)) {
            return false; // Can't insert a different brew
        }

        this.content = content;
        this.amount += toInsert;

        var remainder = handStack.get(DataComponentTypes.USE_REMAINDER);
        var remainderStack = remainder != null ? remainder.comp_3093() : handStack.getRecipeRemainder();

        handStack.decrement(1);
        if (!remainderStack.isEmpty()) {
            if (handStack.isEmpty()) {
                player.setStackInHand(hand, remainderStack.copy());
            } else {
                player.getInventory().offerOrDrop(remainderStack.copy());
            }
        }

        itemForm.emptySound().ifPresent(sound ->
                player.getWorld().playSound(player, player.getX(), player.getEyeY(), player.getZ(), sound, SoundCategory.PLAYERS, 1, 1));

        return true;
    }

    public boolean insertItems(PlayerEntity player, Hand hand) {
        return false;
    }

    private <T> void copyComponent(Component<T> component, ComponentMap stackComponents, ComponentMap.Builder contentComponents) {
        contentComponents.add(component.comp_2443(), stackComponents.getOrDefault(component.comp_2443(), component.comp_2444()));
    }

    private DynamicRegistryManager getRegistryManager() {
        return Objects.requireNonNull(world, "World should exist for registries").getRegistryManager();
    }
}
