package com.enderio.machines.common.integrations.vanilla;

import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;

public class VanillaAlloySmeltingRecipe extends AlloySmeltingRecipe {
    public static final int RF_PER_ITEM = ForgeHooks.getBurnTime(new ItemStack(Items.COAL, 1), RecipeType.SMELTING) * 10 / 8;

    private final SmeltingRecipe vanillaRecipe;

    public VanillaAlloySmeltingRecipe(SmeltingRecipe vanillaRecipe) {
        // Provide some dummy values.
        super(vanillaRecipe.getId(), List.of(), ItemStack.EMPTY, 0, 0);
        this.vanillaRecipe = vanillaRecipe;
    }

    // Override base behaviour to insert the vanilla recipe

    @Override
    public List<CountedIngredient> getInputs() {
        return List.of(CountedIngredient.of(vanillaRecipe.getIngredients().get(0)));
    }

    @Override
    public int getBaseEnergyCost() {
        return RF_PER_ITEM;
    }

    @Override
    public int getEnergyCost(ContainerWrapper container) {
        return RF_PER_ITEM * container.getInputsTaken();
    }

    @Override
    public float getExperience() {
        return vanillaRecipe.getExperience();
    }

    @Override
    public boolean matches(ContainerWrapper container, Level level) {
        for (int i = 0; i < AlloySmelterBlockEntity.INPUTS.size(); i++) {
            if (vanillaRecipe.matches(new AlloySmelterBlockEntity.ContainerSubWrapper(container, i), level))
                return true;
        }
        return false;
    }

    @Override
    public List<OutputStack> craft(ContainerWrapper container, RegistryAccess registryAccess) {
        ItemStack result = vanillaRecipe.assemble(container, registryAccess);
        result.setCount(result.getCount() * container.getInputsTaken());
        return List.of(OutputStack.of(result));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(vanillaRecipe.getResultItem(registryAccess)));
    }

    /**
     * @deprecated Cannot serialize a wrapped recipe.
     */
    @Deprecated
    @Override
    public RecipeSerializer<?> getSerializer() {
        throw new UnsupportedOperationException("Cannot serialize a wrapped recipe!");
    }

    @Override
    public RecipeType<?> getType() {
        return vanillaRecipe.getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SmeltingRecipe recipe && recipe.equals(vanillaRecipe)) {
            return true;
        }
        if (obj instanceof VanillaAlloySmeltingRecipe alloySmeltingRecipe && vanillaRecipe.equals(alloySmeltingRecipe.vanillaRecipe)){
            return true;
        }
        return super.equals(obj);
    }
}
