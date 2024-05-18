package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.concurrent.CompletableFuture;

public class EnchanterRecipeProvider extends RecipeProvider {

    public EnchanterRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        //vanilla
        build(Enchantments.PROTECTION, SizedIngredient.of(EIOTags.Items.INGOTS_DARK_STEEL, 16), 1, recipeOutput);
        build(Enchantments.FIRE_PROTECTION, SizedIngredient.of(Items.MAGMA_CREAM, 16), 1, recipeOutput); //TODO
        build(Enchantments.FEATHER_FALLING, SizedIngredient.of(Tags.Items.FEATHERS, 1), 1, recipeOutput);
        build(Enchantments.BLAST_PROTECTION, SizedIngredient.of(Items.GUNPOWDER, 16), 1, recipeOutput);
        build(Enchantments.PROJECTILE_PROTECTION, SizedIngredient.of(Tags.Items.LEATHERS, 16), 1, recipeOutput);//change arrow->leather?
        build(Enchantments.RESPIRATION, SizedIngredient.of(Items.GLASS_BOTTLE, 1), 1, recipeOutput);
        build(Enchantments.AQUA_AFFINITY, SizedIngredient.of(Items.LILY_PAD, 1), 1, recipeOutput);
        build(Enchantments.DEPTH_STRIDER, SizedIngredient.of(Items.PRISMARINE_SHARD, 1), 1, recipeOutput);
        build(Enchantments.FROST_WALKER, SizedIngredient.of(Items.ICE, 16), 1, recipeOutput);
        build(Enchantments.THORNS, SizedIngredient.of(Items.ROSE_BUSH, 4), 1, recipeOutput);
        build(Enchantments.SHARPNESS, SizedIngredient.of(Tags.Items.GEMS_QUARTZ, 12), 1, recipeOutput);
        build(Enchantments.SMITE, SizedIngredient.of(Items.ROTTEN_FLESH, 12), 1, recipeOutput);
        build(Enchantments.BANE_OF_ARTHROPODS, SizedIngredient.of(Items.SPIDER_EYE, 12), 1, recipeOutput);
        build(Enchantments.KNOCKBACK, SizedIngredient.of(Items.PISTON, 1), 1, recipeOutput);
        build(Enchantments.FIRE_ASPECT, SizedIngredient.of(Items.BLAZE_ROD, 8), 1, recipeOutput);
        build(Enchantments.LOOTING, SizedIngredient.of(Items.SKELETON_SKULL, 1), 1, recipeOutput);
        build(Enchantments.EFFICIENCY, SizedIngredient.of(Tags.Items.DUSTS_REDSTONE, 12), 1, recipeOutput);
        build(Enchantments.SILK_TOUCH, SizedIngredient.of(Tags.Items.SLIMEBALLS, 1), 1, recipeOutput);
        build(Enchantments.UNBREAKING, SizedIngredient.of(Tags.Items.OBSIDIANS, 1), 1, recipeOutput);
        build(Enchantments.MENDING, SizedIngredient.of(EIOItems.EXPERIENCE_ROD.get(), 1), 1, recipeOutput);
        build(Enchantments.FORTUNE, SizedIngredient.of(Tags.Items.GEMS_EMERALD, 1), 1, recipeOutput);
        build(Enchantments.POWER, SizedIngredient.of(Items.FLINT, 12), 1, recipeOutput);
        build(Enchantments.PUNCH, SizedIngredient.of(Tags.Items.STRINGS, 1), 1, recipeOutput);
        build(Enchantments.FLAME, SizedIngredient.of(Tags.Items.NETHERRACKS, 1), 1, recipeOutput);
        build(Enchantments.INFINITY, SizedIngredient.of(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY, 1), 1, recipeOutput);
        build(Enchantments.LUCK_OF_THE_SEA, SizedIngredient.of(Tags.Items.GEMS_LAPIS, 1), 1, recipeOutput);
        build(Enchantments.LURE, SizedIngredient.of(ItemTags.FISHES, 1), 1, recipeOutput);
        build(Enchantments.SWEEPING_EDGE, SizedIngredient.of(Tags.Items.INGOTS_IRON, 8), 1, recipeOutput);
        //new
        build(Enchantments.CHANNELING, SizedIngredient.of(Items.LIGHTNING_ROD, 1), 1, recipeOutput);
        build(Enchantments.IMPALING, SizedIngredient.of(Tags.Items.INGOTS_IRON, 8), 1, recipeOutput);
        build(Enchantments.LOYALTY, SizedIngredient.of(Items.LEAD, 1), 1, recipeOutput);
        build(Enchantments.MULTISHOT, SizedIngredient.of(ItemTags.ARROWS, 16), 1, recipeOutput);//TODO
        build(Enchantments.PIERCING, SizedIngredient.of(Tags.Items.GEMS_PRISMARINE, 8), 1, recipeOutput);
        build(Enchantments.QUICK_CHARGE, SizedIngredient.of(Items.SUGAR, 16), 1, recipeOutput);
        build(Enchantments.RIPTIDE, SizedIngredient.of(Items.FIREWORK_ROCKET, 8), 1, recipeOutput);
        build(Enchantments.SOUL_SPEED, SizedIngredient.of(Items.SOUL_SOIL, 16), 1, recipeOutput);


        //enderio
        build(EIOEnchantments.SOULBOUND.get(), SizedIngredient.of(EIOTags.Items.GEMS_ENDER_CRYSTAL, 1), 1, recipeOutput);
        build(EIOEnchantments.WITHERING.get(), SizedIngredient.of(EIOItems.WITHERING_POWDER.get(), 4), 1, recipeOutput);
        build(EIOEnchantments.REPELLENT.get(), SizedIngredient.of(Items.ENDER_PEARL, 4), 2, recipeOutput);
        build(EIOEnchantments.AUTO_SMELT.get(), SizedIngredient.of(Items.BLAZE_POWDER, 16), 1, recipeOutput); //TODO
        build(EIOEnchantments.XP_BOOST.get(), SizedIngredient.of(Items.EXPERIENCE_BOTTLE, 16), 1, recipeOutput); //TODO
    }

    protected void build(Enchantment enchantment, SizedIngredient input, int levelModifier, RecipeOutput recipeOutput) {
        recipeOutput.accept(
            EnderIO.loc("enchanting/" + BuiltInRegistries.ENCHANTMENT.getKey(enchantment).getPath()),
            new EnchanterRecipe(enchantment, levelModifier, input),
            null);
    }

}
