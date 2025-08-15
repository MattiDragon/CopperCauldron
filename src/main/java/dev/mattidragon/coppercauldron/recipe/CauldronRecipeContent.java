package dev.mattidragon.coppercauldron.recipe;

import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronContent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;
import java.util.Objects;

public record CauldronRecipeContent(
        CauldronContent content,
        long amount,
        List<ItemStack> items,
        double heat
) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }

    public boolean hasContent(RegistryEntry<CauldronBrew> brew, ComponentChanges filter) {
        if (!Objects.equals(brew.getKeyOrValue(), content.brew().getKeyOrValue())) return false;

        var pair = filter.toAddedRemovedPair();
        for (var removed : pair.removed()) {
            if (content.components().contains(removed)) return false;
        }
        for (var added : pair.added()) {
            if (!content.components().contains(added.type())) return false;
            if (!Objects.equals(added.value(), content.components().get(added.type()))) return false;
        }

        return true;
    }
}
