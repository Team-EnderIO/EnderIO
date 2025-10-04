package com.enderio.enderio.api.conduits;

import com.enderio.enderio.common.conduits.ConduitBlockItem;
import com.enderio.enderio.common.init.ConduitBlocks;
import com.enderio.enderio.common.init.ConduitComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

@ApiStatus.AvailableSince("8.0.5")
@ApiStatus.Experimental
public class ConduitIngredient implements ICustomIngredient {

    public static final MapCodec<ConduitIngredient> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(Conduit.CODEC.fieldOf("conduit_type").forGetter(ConduitIngredient::conduit))
                    .apply(builder, ConduitIngredient::new));

    public static final IngredientType<ConduitIngredient> TYPE = new IngredientType<>(CODEC);

    private final Holder<Conduit<?, ?>> conduit;

    private ConduitIngredient(Holder<Conduit<?, ?>> conduit) {
        this.conduit = conduit;
    }

    public static Ingredient of(Holder<Conduit<?, ?>> conduit) {
        return new ConduitIngredient(conduit).toVanilla();
    }

    public Holder<Conduit<?, ?>> conduit() {
        return conduit;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!stack.is(ConduitBlocks.CONDUIT.asItem())) {
            return false;
        }

        if (!stack.has(ConduitComponents.CONDUIT)) {
            return false;
        }

        Holder<Conduit<?, ?>> conduit = stack.get(ConduitComponents.CONDUIT);
        if (conduit == null) {
            return false;
        }
        return this.conduit.is(conduit);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(ConduitBlockItem.getStackFor(conduit, 1));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return TYPE;
    }
}
