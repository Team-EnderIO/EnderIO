package com.enderio.base.common.recipe;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.PotDecorations;

import java.util.List;

@Deprecated(forRemoval = true, since = "1.21.2")
//replace with a normal crafting_transmute recipe
public class SoulPotCrafting extends CustomRecipe {
    public SoulPotCrafting(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if (craftingInput.items().size() != 2)
            return false;

        boolean foundPot = false;
        boolean foundVial = false;
        for (ItemStack item : craftingInput.items()) {
            if (item.is(Items.DECORATED_POT)) {
                if (foundPot)
                    return false;
                foundPot = true;
            }
            if (item.is(EIOItems.SOUL_VIAL)) {
                if (foundVial)
                    return false;
                foundVial = true;
            }
        }
        return foundVial && foundPot;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        ItemStack soulPot = EIOBlocks.SOUL_POT.toStack();
        for (ItemStack item : craftingInput.items()) {
            Soul soul = item.get(EIODataComponents.SOUL);
            PotDecorations potDecorations = item.get(DataComponents.POT_DECORATIONS);
            if (soul != null) {
                soulPot.set(EIODataComponents.SOUL, soul);
            }
            if (potDecorations != null) {
                soulPot.set(DataComponents.POT_DECORATIONS, potDecorations);
            }
        }
        return soulPot;
    }

    @Override
    public boolean canCraftInDimensions(int xSize, int ySize) {
        return xSize * ySize > 2;
    }

    @Override
    public RecipeSerializer<SoulPotCrafting> getSerializer() {
        return EIORecipes.SOUL_POT.serializer().get();
    }
}
