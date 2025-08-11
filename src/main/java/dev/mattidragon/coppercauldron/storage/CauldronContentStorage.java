package dev.mattidragon.coppercauldron.storage;

import dev.mattidragon.coppercauldron.block.entity.CopperCauldronBlockEntity;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import dev.mattidragon.coppercauldron.content.CauldronContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.Objects;

public class CauldronContentStorage extends SnapshotParticipant<CauldronContentStorage.Snapshot> implements SingleSlotStorage<CauldronContent> {
    private final CopperCauldronBlockEntity entity;

    public CauldronContentStorage(CopperCauldronBlockEntity entity) {
        this.entity = entity;
    }

    @Override
    public long insert(CauldronContent content, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        if (!Objects.equals(content, entity.content()) && !isResourceBlank()) {
            return 0;
        }

        updateSnapshots(transaction);
        long inserted = Math.min(maxAmount, getCapacity() - getAmount());
        entity.setContent(content, getAmount() + inserted);

        return inserted;
    }

    @Override
    public long extract(CauldronContent content, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        if (!Objects.equals(content, entity.content()) || isResourceBlank()) {
            return 0;
        }

        // Without a world we can't safely make the cauldron empty
        var world = entity.getWorld();
        if (world == null) return 0;

        updateSnapshots(transaction);
        long extracted = Math.min(maxAmount, getAmount());

        var newContent = extracted == getAmount() ? CauldronContent.getEmpty(world.getRegistryManager()) : content;
        entity.setContent(newContent, getAmount() - extracted);

        return extracted;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().brew().matchesKey(CauldronBrews.EMPTY);
    }

    @Override
    public CauldronContent getResource() {
        return entity.content();
    }

    @Override
    public long getAmount() {
        return entity.amount();
    }

    @Override
    public long getCapacity() {
        return FluidConstants.BUCKET;
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(entity.content(), entity.amount());
    }

    @Override
    protected void readSnapshot(Snapshot snapshot) {
        entity.setContent(snapshot.content, snapshot.amount);
    }

    @Override
    protected void onFinalCommit() {
        entity.markDirty();
    }

    protected record Snapshot(CauldronContent content, long amount) {
    }
}
