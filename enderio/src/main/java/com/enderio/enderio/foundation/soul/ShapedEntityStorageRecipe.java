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
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.Objects;
import java.util.Optional;

/**
 * Based upon BackpackUpgradeRecipe from Sophisticated Backpacks. Thanks!
 */
// TODO: This probably wants to be reworked/renamed, but for now it'll borrow from the bindable soul source.
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
            var inputSoulStorage = Objects.requireNonNull(ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE));
            SoulBoundUtils.tryBindSoul(result, inputSoulStorage.availableSoul());
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
            var soulStorage = ItemAccess.forStack(stack).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
            if (soulStorage != null && soulStorage.isSoulAvailable()) {
                return Optional.of(stack);
            }
        }

        return Optional.empty();
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        // Expect this to break, its quite gross...
        //noinspection rawtypes,unchecked
        return (RecipeSerializer<ShapedRecipe>) (RecipeSerializer) SERIALIZER;
    }
}
