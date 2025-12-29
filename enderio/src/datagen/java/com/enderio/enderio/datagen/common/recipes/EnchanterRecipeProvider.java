package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class EnchanterRecipeProvider extends SubRecipeProvider {

    private HolderLookup.RegistryLookup<Item> items;

    protected SizedIngredient sizedFromTag(TagKey<Item> tag, int count) {
        return new SizedIngredient(Ingredient.of(this.items.getOrThrow(tag)), count);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        // We know that the registries are now available.
        HolderGetter<Enchantment> enchantmentRegistry = registries.lookupOrThrow(Registries.ENCHANTMENT);
        this.items = registries.lookupOrThrow(Registries.ITEM);

        // vanilla
        build(enchantmentRegistry, Enchantments.PROTECTION, sizedFromTag(EIOTags.Items.INGOTS_DARK_STEEL, 16), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FIRE_PROTECTION, SizedIngredient.of(Items.MAGMA_CREAM, 16), 1,
                recipeOutput); // TODO
        build(enchantmentRegistry, Enchantments.FEATHER_FALLING, sizedFromTag(Tags.Items.FEATHERS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.BLAST_PROTECTION, SizedIngredient.of(Items.GUNPOWDER, 16), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.PROJECTILE_PROTECTION, sizedFromTag(Tags.Items.LEATHERS, 16), 1,
                recipeOutput);// change arrow->leather?
        build(enchantmentRegistry, Enchantments.RESPIRATION, SizedIngredient.of(Items.GLASS_BOTTLE, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.AQUA_AFFINITY, SizedIngredient.of(Items.LILY_PAD, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.DEPTH_STRIDER, SizedIngredient.of(Items.PRISMARINE_SHARD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FROST_WALKER, SizedIngredient.of(Items.ICE, 16), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.THORNS, SizedIngredient.of(Items.ROSE_BUSH, 4), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SHARPNESS, sizedFromTag(Tags.Items.GEMS_QUARTZ, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.SMITE, SizedIngredient.of(Items.ROTTEN_FLESH, 12), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.BANE_OF_ARTHROPODS, SizedIngredient.of(Items.SPIDER_EYE, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.KNOCKBACK, SizedIngredient.of(Items.PISTON, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.FIRE_ASPECT, SizedIngredient.of(Items.BLAZE_ROD, 8), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.LOOTING, SizedIngredient.of(Items.SKELETON_SKULL, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.EFFICIENCY, sizedFromTag(Tags.Items.DUSTS_REDSTONE, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.SILK_TOUCH, sizedFromTag(Tags.Items.SLIME_BALLS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.UNBREAKING, sizedFromTag(Tags.Items.OBSIDIANS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.MENDING, SizedIngredient.of(EIOItems.VOID_VIAL.get(), 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FORTUNE, sizedFromTag(Tags.Items.GEMS_EMERALD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.POWER, SizedIngredient.of(Items.FLINT, 12), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.PUNCH, sizedFromTag(Tags.Items.STRINGS, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.FLAME, sizedFromTag(Tags.Items.NETHERRACKS, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.INFINITY, sizedFromTag(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY, 1),
                1, recipeOutput);
        build(enchantmentRegistry, Enchantments.LUCK_OF_THE_SEA, sizedFromTag(Tags.Items.GEMS_LAPIS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.LURE, sizedFromTag(ItemTags.FISHES, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SWEEPING_EDGE, sizedFromTag(Tags.Items.INGOTS_IRON, 8), 1,
                recipeOutput);
        // new
        build(enchantmentRegistry, Enchantments.CHANNELING, SizedIngredient.of(Items.LIGHTNING_ROD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.IMPALING, sizedFromTag(Tags.Items.STORAGE_BLOCKS_IRON, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.LOYALTY, SizedIngredient.of(Items.LEAD, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.MULTISHOT, sizedFromTag(ItemTags.ARROWS, 16), 1, recipeOutput);// TODO
        build(enchantmentRegistry, Enchantments.PIERCING, sizedFromTag(Tags.Items.GEMS_PRISMARINE, 8), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.QUICK_CHARGE, SizedIngredient.of(Items.SUGAR, 16), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.RIPTIDE, SizedIngredient.of(Items.FIREWORK_ROCKET, 8), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SOUL_SPEED, SizedIngredient.of(Items.SOUL_SOIL, 16), 1, recipeOutput);
    }

    protected void build(HolderGetter<Enchantment> enchantmentRegistry, ResourceKey<Enchantment> enchantment,
            SizedIngredient input, int levelModifier, RecipeOutput recipeOutput) {
        build(enchantmentRegistry.getOrThrow(enchantment), input, levelModifier, recipeOutput);
    }

    protected void build(Holder<Enchantment> enchantment, SizedIngredient input, int levelModifier,
            RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.rl("enchanting/" + enchantment.getKey().identifier().getPath())),
                new EnchanterRecipe(enchantment, levelModifier, input), null);
    }

}
