package dev.mattidragon.coppercauldron.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public sealed interface BrewVisual {
    Codec<BrewVisual> CODEC = Codec.STRING.partialDispatch(
            "type",
            form -> switch (form) {
                case EmptyVisual emptyVisual -> DataResult.success("empty");
                case PotionVisual potionVisual -> DataResult.success("potion");
                case FluidVisual fluidVisual -> DataResult.success("fluid");
                case TextureVisual textureVisual -> DataResult.success("texture");
            },
            type -> switch (type) {
                case "empty" -> DataResult.success(EmptyVisual.CODEC);
                case "potion" -> DataResult.success(PotionVisual.CODEC);
                case "fluid" -> DataResult.success(FluidVisual.CODEC);
                case "texture" -> DataResult.success(TextureVisual.CODEC);
                default -> DataResult.error(() -> "Unknown visual type: " + type);
            }
    );

    record EmptyVisual() implements BrewVisual {
        public static final MapCodec<EmptyVisual> CODEC = MapCodec.unit(new EmptyVisual());
    }

    // Potions need to be special cased (unless some mod provides a potion fluid)
    record PotionVisual() implements BrewVisual {
        public static final MapCodec<PotionVisual> CODEC = MapCodec.unit(new PotionVisual());
    }

    record FluidVisual(Fluid fluid) implements BrewVisual {
        public static final MapCodec<FluidVisual> CODEC = Registries.FLUID.getCodec().fieldOf("fluid").xmap(FluidVisual::new, FluidVisual::fluid);
    }

    record TextureVisual(Identifier id) implements BrewVisual {
        public static final MapCodec<TextureVisual> CODEC = Identifier.CODEC.fieldOf("id").xmap(TextureVisual::new, TextureVisual::id);
    }
}
