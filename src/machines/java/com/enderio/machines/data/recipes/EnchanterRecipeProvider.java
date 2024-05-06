package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class EnchanterRecipeProvider extends EnderRecipeProvider {

    public EnchanterRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        //vanilla
        build(Enchantments.PROTECTION, CountedIngredient.of(16, EIOTags.Items.INGOTS_DARK_STEEL), 1, recipeOutput);
        build(Enchantments.FIRE_PROTECTION, CountedIngredient.of(16, Items.MAGMA_CREAM), 1, recipeOutput); //TODO
        build(Enchantments.FEATHER_FALLING, CountedIngredient.of(Tags.Items.FEATHERS), 1, recipeOutput);
        build(Enchantments.BLAST_PROTECTION, CountedIngredient.of(16, Items.GUNPOWDER), 1, recipeOutput);
        build(Enchantments.PROJECTILE_PROTECTION, CountedIngredient.of(16, Tags.Items.LEATHERS), 1, recipeOutput);//change arrow->leather?
        build(Enchantments.RESPIRATION, CountedIngredient.of(Items.GLASS_BOTTLE), 1, recipeOutput);
        build(Enchantments.AQUA_AFFINITY, CountedIngredient.of(Items.LILY_PAD), 1, recipeOutput);
        build(Enchantments.DEPTH_STRIDER, CountedIngredient.of(Items.PRISMARINE_SHARD), 1, recipeOutput);
        build(Enchantments.FROST_WALKER, CountedIngredient.of(16, Items.ICE), 1, recipeOutput);
        build(Enchantments.THORNS, CountedIngredient.of(4, Items.ROSE_BUSH), 1, recipeOutput);
        build(Enchantments.SHARPNESS, CountedIngredient.of(12, Tags.Items.GEMS_QUARTZ), 1, recipeOutput);
        build(Enchantments.SMITE, CountedIngredient.of(12, Items.ROTTEN_FLESH), 1, recipeOutput);
        build(Enchantments.BANE_OF_ARTHROPODS, CountedIngredient.of(12, Items.SPIDER_EYE), 1, recipeOutput);
        build(Enchantments.KNOCKBACK, CountedIngredient.of(Items.PISTON), 1, recipeOutput);
        build(Enchantments.FIRE_ASPECT, CountedIngredient.of(8, Items.BLAZE_ROD), 1, recipeOutput);
        build(Enchantments.LOOTING, CountedIngredient.of(Items.SKELETON_SKULL), 1, recipeOutput);
        build(Enchantments.EFFICIENCY, CountedIngredient.of(12, Tags.Items.DUSTS_REDSTONE), 1, recipeOutput);
        build(Enchantments.SILK_TOUCH, CountedIngredient.of(Tags.Items.SLIMEBALLS), 1, recipeOutput);
        build(Enchantments.UNBREAKING, CountedIngredient.of(Tags.Items.OBSIDIANS), 1, recipeOutput);
        build(Enchantments.MENDING, CountedIngredient.of(EIOItems.EXPERIENCE_ROD.get()), 1, recipeOutput);
        build(Enchantments.FORTUNE, CountedIngredient.of(Tags.Items.GEMS_EMERALD), 1, recipeOutput);
        build(Enchantments.POWER, CountedIngredient.of(12, Items.FLINT), 1, recipeOutput);
        build(Enchantments.PUNCH, CountedIngredient.of(Tags.Items.STRINGS), 1, recipeOutput);
        build(Enchantments.FLAME, CountedIngredient.of(Tags.Items.NETHERRACKS), 1, recipeOutput);
        build(Enchantments.INFINITY, CountedIngredient.of(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY), 1, recipeOutput);
        build(Enchantments.LUCK_OF_THE_SEA, CountedIngredient.of(Tags.Items.GEMS_LAPIS), 1, recipeOutput);
        build(Enchantments.LURE, CountedIngredient.of(ItemTags.FISHES), 1, recipeOutput);
        build(Enchantments.SWEEPING_EDGE, CountedIngredient.of(8, Tags.Items.INGOTS_IRON), 1, recipeOutput);
        //new
        build(Enchantments.CHANNELING, CountedIngredient.of(Items.LIGHTNING_ROD), 1, recipeOutput);
        build(Enchantments.IMPALING, CountedIngredient.of(8, Tags.Items.INGOTS_IRON), 1, recipeOutput);
        build(Enchantments.LOYALTY, CountedIngredient.of(Items.LEAD), 1, recipeOutput);
        build(Enchantments.MULTISHOT, CountedIngredient.of(16, ItemTags.ARROWS), 1, recipeOutput);//TODO
        build(Enchantments.PIERCING, CountedIngredient.of(8, Tags.Items.GEMS_PRISMARINE), 1, recipeOutput);
        build(Enchantments.QUICK_CHARGE, CountedIngredient.of(16, Items.SUGAR), 1, recipeOutput);
        build(Enchantments.RIPTIDE, CountedIngredient.of(8, Items.FIREWORK_ROCKET), 1, recipeOutput);
        build(Enchantments.SOUL_SPEED, CountedIngredient.of(16, Items.SOUL_SOIL), 1, recipeOutput);


        //enderio
        build(EIOEnchantments.SOULBOUND.get(), CountedIngredient.of(EIOTags.Items.GEMS_ENDER_CRYSTAL), 1, recipeOutput);
        build(EIOEnchantments.WITHERING.get(), CountedIngredient.of(4, EIOItems.WITHERING_POWDER.get()), 1, recipeOutput);
        build(EIOEnchantments.REPELLENT.get(), CountedIngredient.of(4, Items.ENDER_PEARL), 2, recipeOutput);
        build(EIOEnchantments.AUTO_SMELT.get(), CountedIngredient.of(16, Items.BLAZE_POWDER), 1, recipeOutput); //TODO
        build(EIOEnchantments.XP_BOOST.get(), CountedIngredient.of(16, Items.EXPERIENCE_BOTTLE), 1, recipeOutput); //TODO
    }

    protected void build(Enchantment enchantment, CountedIngredient input, int levelModifier, RecipeOutput recipeOutput) {
        recipeOutput.accept(
            EnderIO.loc("enchanting/" + BuiltInRegistries.ENCHANTMENT.getKey(enchantment).getPath()),
            new EnchanterRecipe(enchantment, levelModifier, input),
            null);
    }

}
