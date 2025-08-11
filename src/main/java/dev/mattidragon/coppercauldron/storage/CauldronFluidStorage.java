package dev.mattidragon.coppercauldron.storage;

import dev.mattidragon.coppercauldron.content.CauldronContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.registry.RegistryWrapper;

import java.util.function.Supplier;

public class CauldronFluidStorage implements SingleSlotStorage<FluidVariant> {
    private final CauldronContentStorage delegate;
    private final Supplier<RegistryWrapper.WrapperLookup> registries;

    public CauldronFluidStorage(CauldronContentStorage delegate, Supplier<RegistryWrapper.WrapperLookup> registries) {
        this.delegate = delegate;
        this.registries = registries;
    }

    @Override
    public long insert(FluidVariant variant, long maxAmount, TransactionContext transaction) {
        return delegate.insert(CauldronContent.fromFluidVariant(variant, registries.get()), maxAmount, transaction);
    }

    @Override
    public long extract(FluidVariant variant, long maxAmount, TransactionContext transaction) {
        return delegate.extract(CauldronContent.fromFluidVariant(variant, registries.get()), maxAmount, transaction);
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return delegate.getResource().toFluidVariant();
    }

    @Override
    public long getAmount() {
        return isResourceBlank() ? 0 : delegate.getAmount();
    }

    @Override
    public long getCapacity() {
        return delegate.getCapacity();
    }
}
