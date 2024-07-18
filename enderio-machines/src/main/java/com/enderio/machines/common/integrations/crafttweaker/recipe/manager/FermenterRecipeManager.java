package com.enderio.machines.common.integrations.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.natives.ingredient.ExpandCTFluidIngredientNeoForge;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/enderio/recipe/Vat")
@ZenCodeType.Name("mods.enderio.recipe.manager.FermenterRecipeManager")
public class FermenterRecipeManager implements IRecipeManager<FermentingRecipe> {
    @Override
    public RecipeType<FermentingRecipe> getRecipeType() {
        return MachineRecipes.VAT_FERMENTING.type().get();
    }


    @ZenCodeType.Method
    public void addRecipe(String name, CTFluidIngredient inputFluid, KnownTag<Item> leftItem, KnownTag<Item> rightItem, IFluidStack output, int ticks) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        final SizedFluidIngredient fluid = ExpandCTFluidIngredientNeoForge.asSizedFluidIngredient(inputFluid);
        final FermentingRecipe recipe = new FermentingRecipe(fluid, TagKey.create(Registries.ITEM, leftItem.id()), TagKey.create(Registries.ITEM, rightItem.id()), output.getInternal(), ticks);
        final RecipeHolder<FermentingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }
}
