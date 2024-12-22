package com.enderio.machines.data.recipes;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.blocks.enchanter.EnchanterRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class EnchanterRecipeProvider extends RecipeProvider {

    private final CompletableFuture<HolderLookup.Provider> registries;

    public EnchanterRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // We know that the registries are now available.
        HolderLookup.Provider lookupProvider = registries.resultNow();
        HolderGetter<Enchantment> enchantmentRegistry = lookupProvider.lookupOrThrow(Registries.ENCHANTMENT);

        // vanilla
        build(enchantmentRegistry, Enchantments.PROTECTION, SizedIngredient.of(EIOTags.Items.INGOTS_DARK_STEEL, 16), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FIRE_PROTECTION, SizedIngredient.of(Items.MAGMA_CREAM, 16), 1,
                recipeOutput); // TODO
        build(enchantmentRegistry, Enchantments.FEATHER_FALLING, SizedIngredient.of(Tags.Items.FEATHERS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.BLAST_PROTECTION, SizedIngredient.of(Items.GUNPOWDER, 16), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.PROJECTILE_PROTECTION, SizedIngredient.of(Tags.Items.LEATHERS, 16), 1,
                recipeOutput);// change arrow->leather?
        build(enchantmentRegistry, Enchantments.RESPIRATION, SizedIngredient.of(Items.GLASS_BOTTLE, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.AQUA_AFFINITY, SizedIngredient.of(Items.LILY_PAD, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.DEPTH_STRIDER, SizedIngredient.of(Items.PRISMARINE_SHARD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FROST_WALKER, SizedIngredient.of(Items.ICE, 16), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.THORNS, SizedIngredient.of(Items.ROSE_BUSH, 4), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SHARPNESS, SizedIngredient.of(Tags.Items.GEMS_QUARTZ, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.SMITE, SizedIngredient.of(Items.ROTTEN_FLESH, 12), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.BANE_OF_ARTHROPODS, SizedIngredient.of(Items.SPIDER_EYE, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.KNOCKBACK, SizedIngredient.of(Items.PISTON, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.FIRE_ASPECT, SizedIngredient.of(Items.BLAZE_ROD, 8), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.LOOTING, SizedIngredient.of(Items.SKELETON_SKULL, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.EFFICIENCY, SizedIngredient.of(Tags.Items.DUSTS_REDSTONE, 12), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.SILK_TOUCH, SizedIngredient.of(Tags.Items.SLIMEBALLS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.UNBREAKING, SizedIngredient.of(Tags.Items.OBSIDIANS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.MENDING, SizedIngredient.of(EIOItems.EXPERIENCE_ROD.get(), 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.FORTUNE, SizedIngredient.of(Tags.Items.GEMS_EMERALD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.POWER, SizedIngredient.of(Items.FLINT, 12), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.PUNCH, SizedIngredient.of(Tags.Items.STRINGS, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.FLAME, SizedIngredient.of(Tags.Items.NETHERRACKS, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.INFINITY, SizedIngredient.of(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY, 1),
                1, recipeOutput);
        build(enchantmentRegistry, Enchantments.LUCK_OF_THE_SEA, SizedIngredient.of(Tags.Items.GEMS_LAPIS, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.LURE, SizedIngredient.of(ItemTags.FISHES, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SWEEPING_EDGE, SizedIngredient.of(Tags.Items.INGOTS_IRON, 8), 1,
                recipeOutput);
        // new
        build(enchantmentRegistry, Enchantments.CHANNELING, SizedIngredient.of(Items.LIGHTNING_ROD, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.IMPALING, SizedIngredient.of(Tags.Items.STORAGE_BLOCKS_IRON, 1), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.LOYALTY, SizedIngredient.of(Items.LEAD, 1), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.MULTISHOT, SizedIngredient.of(ItemTags.ARROWS, 16), 1, recipeOutput);// TODO
        build(enchantmentRegistry, Enchantments.PIERCING, SizedIngredient.of(Tags.Items.GEMS_PRISMARINE, 8), 1,
                recipeOutput);
        build(enchantmentRegistry, Enchantments.QUICK_CHARGE, SizedIngredient.of(Items.SUGAR, 16), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.RIPTIDE, SizedIngredient.of(Items.FIREWORK_ROCKET, 8), 1, recipeOutput);
        build(enchantmentRegistry, Enchantments.SOUL_SPEED, SizedIngredient.of(Items.SOUL_SOIL, 16), 1, recipeOutput);
//
//
//        //enderio
//        build(EIOEnchantments.SOULBOUND.get(), SizedIngredient.of(EIOTags.Items.GEMS_ENDER_CRYSTAL, 1), 1, recipeOutput);
//        build(EIOEnchantments.WITHERING.get(), SizedIngredient.of(EIOItems.WITHERING_POWDER.get(), 4), 1, recipeOutput);
//        build(EIOEnchantments.REPELLENT.get(), SizedIngredient.of(Items.ENDER_PEARL, 4), 2, recipeOutput);
//        build(EIOEnchantments.AUTO_SMELT.get(), SizedIngredient.of(Items.BLAZE_POWDER, 16), 1, recipeOutput); //TODO
//        build(EIOEnchantments.XP_BOOST.get(), SizedIngredient.of(Items.EXPERIENCE_BOTTLE, 16), 1, recipeOutput); //TODO
    }

    protected void build(HolderGetter<Enchantment> enchantmentRegistry, ResourceKey<Enchantment> enchantment,
            SizedIngredient input, int levelModifier, RecipeOutput recipeOutput) {
        build(enchantmentRegistry.getOrThrow(enchantment), input, levelModifier, recipeOutput);
    }

    protected void build(Holder<Enchantment> enchantment, SizedIngredient input, int levelModifier,
            RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIOBase.loc("enchanting/" + enchantment.getKey().location().getPath()),
                new EnchanterRecipe(enchantment, levelModifier, input), null);
    }

}
