package com.enderio.machines.common.integrations.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.natives.entity.ExpandEntityType;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;
import net.minecraft.world.entity.MobCategory;

@ZenRegister
@Document("mods/enderio/recipes/SoulBinder")
@ZenCodeType.Name("mods.enderio.recipe.manager.SoulBinderRecipeManager")
public class SoulBinderRecipeManager implements IRecipeManager<SoulBindingRecipe> {

    /**
     *
     * @param name The recipe name
     * @param output The output item
     * @param input The input items
     * @param energy The required power
     * @param experience The number of levels needed
     * @param type The entity type to use
     * @docParam name "totem_from_evoker"
     * @docParam output <item:minecraft:totem_of_undying>
     * @docParam input <tag:item:c:storage_blocks/gold>
     * @docParam energy 1000
     * @docParam experience 1
     * @docParam type <entitytype:minecraft:evoker>
     */
    @ZenCodeType.Method
    public void addRecipe(String name, Item output, IIngredient input, int energy, int experience, EntityType<Entity> type) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        final ResourceLocation entity = ExpandEntityType.getRegistryName(type);
        final SoulBindingRecipe recipe = new SoulBindingRecipe(output, input.asVanillaIngredient(), energy, experience, entity);
        final RecipeHolder<SoulBindingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }

    /**
     *
     * @param name The recipe name
     * @param output The output item
     * @param input The input items
     * @param energy The required power
     * @param experience The number of levels needed
     * @param type The mob category to use
     * @docParam name "grass_from_passive_mob"
     * @docParam output <item:minecraft:grass_block>
     * @docParam input <item:minecraft:dirt>
     * @docParam energy 1000
     * @docParam experience 1
     * @docParam type <constant:minecraft:mobcategory:creature>
     */
    @ZenCodeType.Method
    public void addRecipe(String name, Item output, IIngredient input, int energy, int experience, MobCategory type) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        final SoulBindingRecipe recipe = new SoulBindingRecipe(output, input.asVanillaIngredient(), energy, experience, type);
        final RecipeHolder<SoulBindingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }

    /**
     *
     * @param name The recipe name
     * @param output The output item
     * @param input The input items
     * @param energy The required power
     * @param experience The number of levels needed
     * @param soulData The needed soul vial nbt
     */
    @ZenCodeType.Method
    public void addRecipe(String name, Item output, IIngredient input, int energy, int experience, String soulData) {
        final ResourceLocation location = ResourceLocation.parse("crafttweaker:" + name);
        final SoulBindingRecipe recipe = new SoulBindingRecipe(output, input.asVanillaIngredient(), energy, experience, soulData);
        final RecipeHolder<SoulBindingRecipe> holder = new RecipeHolder<>(location, recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, holder));
    }

    @Override
    public RecipeType<SoulBindingRecipe> getRecipeType() {
        return MachineRecipes.SOUL_BINDING.type().get();
    }
}
