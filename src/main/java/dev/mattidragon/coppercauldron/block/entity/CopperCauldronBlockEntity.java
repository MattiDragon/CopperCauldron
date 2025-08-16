package dev.mattidragon.coppercauldron.block.entity;

import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import dev.mattidragon.coppercauldron.content.CauldronContent;
import dev.mattidragon.coppercauldron.recipe.CauldronRecipeContent;
import dev.mattidragon.coppercauldron.registry.ModBlockEntities;
import dev.mattidragon.coppercauldron.registry.ModRecipes;
import dev.mattidragon.coppercauldron.registry.ModTags;
import dev.mattidragon.coppercauldron.storage.CauldronContentStorage;
import dev.mattidragon.coppercauldron.storage.CauldronFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CopperCauldronBlockEntity extends BlockEntity {
    private int cauldronHeat = 0;
    private double contentHeat = 0;

    private CauldronContent content;
    private long amount;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);

    private final CauldronFluidStorage fluidStorage;
    private final SlottedStorage<ItemVariant> itemStorage;

    private RegistryKey<Recipe<?>> processingRecipe;
    private int processingProgress = 0;

    public CopperCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COPPER_CAULDRON, blockPos, blockState);
        var contentStorage = new CauldronContentStorage(this);
        fluidStorage = new CauldronFluidStorage(contentStorage, this::getRegistryManager);
        itemStorage = new CombinedSlottedStorage<>(
                IntStream.range(0, items.size())
                        .mapToObj(i -> new SingleStackStorage() {
                            @Override
                            protected ItemStack getStack() {
                                return items.get(i);
                            }

                            @Override
                            protected void setStack(ItemStack stack) {
                                items.set(i, stack);
                            }

                            @Override
                            protected void onFinalCommit() {
                                markDirty();
                            }
                        })
                        .toList()
        );
    }

    static {
        FluidStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.fluidStorage, ModBlockEntities.COPPER_CAULDRON);
        ItemStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.itemStorage, ModBlockEntities.COPPER_CAULDRON);
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

        cauldronHeat = readView.getInt("cauldron_heat", 0);
        contentHeat = readView.getDouble("content_heat", 0);
        processingProgress = readView.getInt("processing_progress", 0);
        processingRecipe = readView.read("processing_recipe", RegistryKey.createCodec(RegistryKeys.RECIPE)).orElse(null);

        content = readView.read("content", CauldronContent.CODEC).orElseGet(() -> CauldronContent.getEmpty(registries));
        amount = readView.getLong("amount", 0);
        Inventories.readData(readView, items);

        // Ensure sane state
        if (amount == 0) {
            content = CauldronContent.getEmpty(registries);
        } else if (content.brew().matchesKey(CauldronBrews.EMPTY)) {
            amount = 0;
        }
    }

    @Override
    protected void writeData(WriteView writeView) {
        writeView.putInt("cauldron_heat", cauldronHeat);
        writeView.putDouble("content_heat", contentHeat);
        writeView.putInt("processing_progress", processingProgress);
        if (processingRecipe != null) {
            writeView.put("processing_recipe", RegistryKey.createCodec(RegistryKeys.RECIPE), processingRecipe);
        }
        writeView.put("content", CauldronContent.CODEC, content);
        writeView.putLong("amount", amount);
        Inventories.writeData(writeView, items);
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

    public void updateCauldronHeat() {
        var world = Objects.requireNonNull(getWorld(), "World may not be null");

        var cauldronHeat = 0;

        var biomeTemperature = world.getBiome(pos).value().getTemperature();
        if (biomeTemperature < 0.5) {
            cauldronHeat = -1; // Cold biome
        } else if (biomeTemperature > 0.8) {
            cauldronHeat = 1; // Hot biome
        }

        for (var dir : Direction.values()) {
            var blockPos = pos.offset(dir);
            var block = world.getBlockState(blockPos);

            if (block.isIn(ModTags.BlockTags.TEMPERATURE_VERY_HOT)) {
                cauldronHeat += 10; // Very hot block
            } else if (block.isIn(ModTags.BlockTags.TEMPERATURE_HOT)) {
                cauldronHeat += 5; // Hot block
            } else if (block.isIn(ModTags.BlockTags.TEMPERATURE_COLD)) {
                cauldronHeat -= 5; // Cold block
            } else if (block.isIn(ModTags.BlockTags.TEMPERATURE_VERY_COLD)) {
                cauldronHeat -= 10; // Very cold block
            }
        }

        if (cauldronHeat != this.cauldronHeat) {
            this.cauldronHeat = cauldronHeat;
            markDirty();
        }
    }

    private void updateContentHeat() {
        var difference = cauldronHeat - contentHeat;
        if (difference == 0) return;
        var change = Math.max(Math.abs(difference / 50), 0.1) * Math.signum(difference);
        change = MathHelper.clamp(change, -Math.abs(difference), Math.abs(difference));
        contentHeat += change;
    }

    public double contentHeat() {
        return contentHeat;
    }

    public double processingAmount() {
        return processingProgress;
    }

    public void setContentValues(double contentHeat) {
        this.contentHeat = contentHeat;
        markDirty();
    }

    public void diluteContentValues(double factor) {
        contentHeat *= factor;
        markDirty();
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
        updateListeners();
    }

    public boolean isCooking() {
        return processingProgress > 0;
    }

    private void updateListeners() {
        Objects.requireNonNull(getWorld(), "World may not be null")
                .updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        markDirty();
    }

    public boolean extractWithContainer(PlayerEntity player, Hand hand) {
        var handStack = player.getStackInHand(hand);

        var brew = content.brew().value();
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
        for (var component : brew.value().components()) {
            copyComponent(component, handStack.getComponents(), components);
        }

        var content = new CauldronContent(brew, components.build());
        var itemForm = brew.value().itemForm().orElseThrow();
        var toInsert = itemForm.amountPerItem();
        if (amount + toInsert > FluidConstants.BUCKET) {
            return false; // Can't insert more than 1 bucket
        }
        if (!this.content.brew().matchesKey(CauldronBrews.EMPTY) && !this.content.equals(content)) {
            return false; // Can't insert a different brew
        }

        this.content = content;
        var oldAmount = this.amount;
        this.amount += toInsert;
        diluteContentValues((double) oldAmount / this.amount);

        var remainder = handStack.get(DataComponentTypes.USE_REMAINDER);
        var remainderStack = remainder != null ? remainder.convertInto() : handStack.getRecipeRemainder();

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
        var handStorage = ContainerItemContext.ofPlayerHand(player, hand).getMainSlot();

        var moved = StorageUtil.move(handStorage, itemStorage, v -> true, Long.MAX_VALUE, null);
        if (moved > 0) {
            updateListeners();
            player.getWorld().playSound(null, getPos(), SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.PLAYERS);
            return true;
        }

        return false;
    }

    public boolean extractItems(PlayerEntity player) {
        var playerStorage = PlayerInventoryStorage.of(player);

        var slot = -1;
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                slot = i;
                break;
            }
        }
        if (slot == -1) return false;

        var moved = StorageUtil.move(itemStorage.getSlot(slot), playerStorage, v -> true, Long.MAX_VALUE, null);
        if (moved > 0) {
            updateListeners();
            player.getWorld().playSound(null, getPos(), SoundEvents.BLOCK_DECORATED_POT_INSERT_FAIL, SoundCategory.PLAYERS);
            return true;
        }

        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState blockState, CopperCauldronBlockEntity entity) {
        entity.tryCraft();
        entity.updateContentHeat();
        if (entity.processingRecipe != null) {
            entity.processingProgress += 1;
            entity.updateListeners();
        }
    }

    private void tryCraft() {
        if (!(world instanceof ServerWorld serverWorld)) return;

        var input = new CauldronRecipeContent(content, amount, getItems(), contentHeat);
        serverWorld.getRecipeManager().getFirstMatch(ModRecipes.CAULDRON_RECIPE_TYPE, input, serverWorld)
                .ifPresentOrElse(recipe -> {
                    if (!recipe.id().equals(processingRecipe)) {
                        processingRecipe = recipe.id();
                        processingProgress = 0;
                        updateListeners();
                        return;
                    }
                    if (processingProgress < recipe.value().processingTime()) return;

                    var output = recipe.value().apply(input, serverWorld.getRegistryManager());
                    var amount = output.content().brew().matchesKey(CauldronBrews.EMPTY) ? 0 : output.amount();
                    setContent(output.content(), amount);
                    setContentValues(output.heat());
                    items.clear();
                    for (var i = 0; i < output.items().size(); i++) {
                        items.set(i, output.items().get(i));
                    }
                }, () -> {
                    processingRecipe = null;
                    processingProgress = 0;
                    updateListeners();
                });
    }

    private <T> void copyComponent(Component<T> component, ComponentMap stackComponents, ComponentMap.Builder contentComponents) {
        contentComponents.add(component.type(), stackComponents.getOrDefault(component.type(), component.value()));
    }

    private DynamicRegistryManager getRegistryManager() {
        return Objects.requireNonNull(world, "World should exist for registries").getRegistryManager();
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(items);
    }
}
