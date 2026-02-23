package com.enderio.enderio.foundation.soul;

import com.enderio.core.common.recipes.WrappedShapedRecipe;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

/**
 * Based upon BackpackUpgradeRecipe from Sophisticated Backpacks. Thanks!
 */
public class ShapedEntityStorageRecipe extends WrappedShapedRecipe {

    public static final MapCodec<ShapedEntityStorageRecipe> MAP_CODEC = createMapCodec(ShapedEntityStorageRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedEntityStorageRecipe> STREAM_CODEC = createStreamCodec(ShapedEntityStorageRecipe::new);

    public static final RecipeSerializer<ShapedEntityStorageRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    
    public ShapedEntityStorageRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public ItemStack assemble(CraftingInput container) {
        ItemStack result = getWrapped().assemble(container);

        getItemStoringEntity(container).ifPresent(itemStack -> {
            var inputSoulStorage = Objects.requireNonNull(itemStack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM));
            SoulBoundUtils.tryBindSoul(result, inputSoulStorage.getBoundSoul());
        });
        
        return result;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        // Only let this match if there is an entity storage to pull from
        return getItemStoringEntity(inv).isPresent() && super.matches(inv, level);
    }

    private Optional<ItemStack> getItemStoringEntity(CraftingInput container) {
        for (int slot = 0; slot < container.size(); slot++) {
            ItemStack stack = container.getItem(slot);
            var soulStorage = stack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
            if (soulStorage != null && soulStorage.hasSoul()) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        // TODO: 26.1 - this will probably explode...
        return (RecipeSerializer<ShapedRecipe>) (RecipeSerializer) SERIALIZER;
    }
}
