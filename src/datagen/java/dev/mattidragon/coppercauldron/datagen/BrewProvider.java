package dev.mattidragon.coppercauldron.datagen;

import dev.mattidragon.coppercauldron.content.BrewVisual;
import dev.mattidragon.coppercauldron.content.CauldronBrew;
import dev.mattidragon.coppercauldron.content.CauldronBrews;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BrewProvider extends FabricDynamicRegistryProvider {
    public BrewProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.add(CauldronBrews.EMPTY, new CauldronBrew(
                new BrewVisual.EmptyVisual(),
                Optional.of(new CauldronBrew.FluidForm(Fluids.EMPTY)),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.AIR,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        0
                )),
                ComponentMap.EMPTY
        ));
        entries.add(CauldronBrews.WATER, new CauldronBrew(
                new BrewVisual.FluidVisual(Fluids.WATER),
                Optional.of(new CauldronBrew.FluidForm(Fluids.WATER)),
                Optional.empty(), // Both buckets and bottles are handled by transfer api
                ComponentMap.EMPTY
        ));
        entries.add(CauldronBrews.POTION, new CauldronBrew(
                new BrewVisual.PotionVisual(),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.POTION,
                        Optional.of(Ingredient.ofItem(Items.GLASS_BOTTLE)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BOTTLE
                )),
                ComponentMap.builder().add(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).build()
        ));
        entries.add(CauldronBrews.HONEY, new CauldronBrew(
                new BrewVisual.TextureVisual(Identifier.ofVanilla("block/honey_block_bottom")),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.HONEY_BOTTLE,
                        Optional.of(Ingredient.ofItem(Items.GLASS_BOTTLE)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BLOCK / 4
                )),
                ComponentMap.EMPTY
        ));
    }

    @Override
    public String getName() {
        return "Cauldron Brews";
    }
}
