package com.enderio.machines.common.integrations.vanilla;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class VanillaAlloySmeltingRecipe extends AlloySmeltingRecipe {
    private final SmeltingRecipe vanillaRecipe;

    public VanillaAlloySmeltingRecipe(SmeltingRecipe vanillaRecipe) {
        // Provide some dummy values.
        super(List.of(), ItemStack.EMPTY, 0, 0);
        this.vanillaRecipe = vanillaRecipe;
    }

    // Override base behaviour to insert the vanilla recipe
    @Override
    public List<SizedIngredient> inputs() {
        return List.of(new SizedIngredient(vanillaRecipe.getIngredients().get(0), 1));
    }

    @Override
    public int getBaseEnergyCost() {
        return MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
    }

    @Override
    public int getEnergyCost(ContainerWrapper container) {
        return getBaseEnergyCost() * container.getInputsTaken();
    }

    @Override
    public float experience() {
        return vanillaRecipe.getExperience();
    }

    @Override
    public boolean matches(ContainerWrapper container, Level level) {
        for (int i = 0; i < AlloySmelterBlockEntity.INPUTS.size(); i++) {
            if (vanillaRecipe.matches(new AlloySmelterBlockEntity.ContainerSubWrapper(container, i), level)) {
                return true;
            }
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
