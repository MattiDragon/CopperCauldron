package dev.mattidragon.coppercauldron.content;

import dev.mattidragon.coppercauldron.CopperCauldron;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CauldronBrews {
    public static final RegistryKey<CauldronBrew> EMPTY = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("empty"));
    public static final RegistryKey<CauldronBrew> WATER = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("water"));
    public static final RegistryKey<CauldronBrew> POTION = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("potion"));
    public static final RegistryKey<CauldronBrew> HONEY = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("honey"));
    public static final RegistryKey<CauldronBrew> MUSHROOM_STEW = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("mushroom_stew"));
    public static final RegistryKey<CauldronBrew> RABBIT_STEW = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("rabbit_stew"));
    public static final RegistryKey<CauldronBrew> BEETROOT_SOUP = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("beetroot_soup"));
    public static final RegistryKey<CauldronBrew> SUSPICIOUS_STEW = RegistryKey.of(CauldronBrew.REGISTRY_KEY, CopperCauldron.id("suspicious_stew"));

    private CauldronBrews() {
    }

    public static void bootstrap(Registerable<CauldronBrew> registerable) {
        registerable.register(EMPTY, new CauldronBrew(
                new BrewVisual.EmptyVisual(),
                Optional.empty(),
                Optional.empty(),
                ComponentMap.EMPTY
        ));
        registerable.register(WATER, new CauldronBrew(
                new BrewVisual.FluidVisual(Fluids.WATER),
                Optional.of(new CauldronBrew.FluidForm(Fluids.WATER)),
                Optional.empty(), // Both buckets and bottles are handled by transfer api
                ComponentMap.EMPTY
        ));
        registerable.register(POTION, new CauldronBrew(
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
        registerable.register(HONEY, new CauldronBrew(
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
        registerable.register(MUSHROOM_STEW, new CauldronBrew(
                new BrewVisual.TextureVisual(CopperCauldron.id("cauldron_brew/mushroom_stew")),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.MUSHROOM_STEW,
                        Optional.of(Ingredient.ofItem(Items.BOWL)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BOTTLE
                )),
                ComponentMap.EMPTY
        ));
        registerable.register(RABBIT_STEW, new CauldronBrew(
                new BrewVisual.TextureVisual(CopperCauldron.id("cauldron_brew/rabbit_stew")),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.RABBIT_STEW,
                        Optional.of(Ingredient.ofItem(Items.BOWL)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BOTTLE
                )),
                ComponentMap.EMPTY
        ));
        registerable.register(BEETROOT_SOUP, new CauldronBrew(
                new BrewVisual.TextureVisual(CopperCauldron.id("cauldron_brew/beetroot_soup")),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.BEETROOT_SOUP,
                        Optional.of(Ingredient.ofItem(Items.BOWL)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BOTTLE
                )),
                ComponentMap.EMPTY
        ));
        registerable.register(SUSPICIOUS_STEW, new CauldronBrew(
                new BrewVisual.TextureVisual(CopperCauldron.id("cauldron_brew/suspicious_stew")),
                Optional.empty(),
                Optional.of(new CauldronBrew.ItemForm(
                        Items.SUSPICIOUS_STEW,
                        Optional.of(Ingredient.ofItem(Items.BOWL)),
                        Optional.of(SoundEvents.ITEM_BOTTLE_FILL),
                        Optional.of(SoundEvents.ITEM_BOTTLE_EMPTY),
                        FluidConstants.BOTTLE
                )),
                ComponentMap.builder().add(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT).build()
        ));
    }
}
