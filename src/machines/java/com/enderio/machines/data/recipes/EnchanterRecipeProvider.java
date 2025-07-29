package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnchanterRecipeProvider extends EnderRecipeProvider {

    public EnchanterRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        //vanilla
        build(Enchantments.ALL_DAMAGE_PROTECTION, CountedIngredient.of(16, EIOTags.Items.INGOTS_DARK_STEEL), 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_PROTECTION, CountedIngredient.of(16, Items.MAGMA_CREAM), 1, pFinishedRecipeConsumer); //TODO
        build(Enchantments.FALL_PROTECTION, CountedIngredient.of(Tags.Items.FEATHERS), 1, pFinishedRecipeConsumer);
        build(Enchantments.BLAST_PROTECTION, CountedIngredient.of(16, Items.GUNPOWDER), 1, pFinishedRecipeConsumer);
        build(Enchantments.PROJECTILE_PROTECTION, CountedIngredient.of(16, Tags.Items.LEATHER), 1, pFinishedRecipeConsumer);//change arrow->leather?
        build(Enchantments.RESPIRATION, CountedIngredient.of(Items.GLASS_BOTTLE), 1, pFinishedRecipeConsumer);
        build(Enchantments.AQUA_AFFINITY, CountedIngredient.of(Items.LILY_PAD), 1, pFinishedRecipeConsumer);
        build(Enchantments.DEPTH_STRIDER, CountedIngredient.of(Items.PRISMARINE_SHARD), 1, pFinishedRecipeConsumer);
        build(Enchantments.FROST_WALKER, CountedIngredient.of(16, Items.ICE), 1, pFinishedRecipeConsumer);
        build(Enchantments.THORNS, CountedIngredient.of(4, Items.ROSE_BUSH), 1, pFinishedRecipeConsumer);
        build(Enchantments.SHARPNESS, CountedIngredient.of(12, Tags.Items.GEMS_QUARTZ), 1, pFinishedRecipeConsumer);
        build(Enchantments.SMITE, CountedIngredient.of(12, Items.ROTTEN_FLESH), 1, pFinishedRecipeConsumer);
        build(Enchantments.BANE_OF_ARTHROPODS, CountedIngredient.of(12, Items.SPIDER_EYE), 1, pFinishedRecipeConsumer);
        build(Enchantments.KNOCKBACK, CountedIngredient.of(Items.PISTON), 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_ASPECT, CountedIngredient.of(8, Items.BLAZE_ROD), 1, pFinishedRecipeConsumer);
        build(Enchantments.MOB_LOOTING, CountedIngredient.of(Items.SKELETON_SKULL), 1, pFinishedRecipeConsumer);
        build(Enchantments.BLOCK_EFFICIENCY, CountedIngredient.of(12, Tags.Items.DUSTS_REDSTONE), 1, pFinishedRecipeConsumer);
        build(Enchantments.SILK_TOUCH, CountedIngredient.of(Tags.Items.SLIMEBALLS), 1, pFinishedRecipeConsumer);
        build(Enchantments.UNBREAKING, CountedIngredient.of(Tags.Items.OBSIDIAN), 1, pFinishedRecipeConsumer);
        build(Enchantments.MENDING, CountedIngredient.of(EIOItems.EXPERIENCE_ROD.get()), 1, pFinishedRecipeConsumer);
        build(Enchantments.BLOCK_FORTUNE, CountedIngredient.of(Tags.Items.GEMS_EMERALD), 1, pFinishedRecipeConsumer);
        build(Enchantments.POWER_ARROWS, CountedIngredient.of(12, Items.FLINT), 1, pFinishedRecipeConsumer);
        build(Enchantments.PUNCH_ARROWS, CountedIngredient.of(Tags.Items.STRING), 1, pFinishedRecipeConsumer);
        build(Enchantments.FLAMING_ARROWS, CountedIngredient.of(Tags.Items.NETHERRACK), 1, pFinishedRecipeConsumer);
        build(Enchantments.INFINITY_ARROWS, CountedIngredient.of(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY), 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_LUCK, CountedIngredient.of(Tags.Items.GEMS_LAPIS), 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_SPEED, CountedIngredient.of(ItemTags.FISHES), 1, pFinishedRecipeConsumer);
        build(Enchantments.SWEEPING_EDGE, CountedIngredient.of(8, Tags.Items.INGOTS_IRON), 1, pFinishedRecipeConsumer);
        //new
        build(Enchantments.CHANNELING, CountedIngredient.of(Items.LIGHTNING_ROD), 1, pFinishedRecipeConsumer);
        build(Enchantments.IMPALING, CountedIngredient.of(Tags.Items.STORAGE_BLOCKS_IRON), 1, pFinishedRecipeConsumer);
        build(Enchantments.LOYALTY, CountedIngredient.of(Items.LEAD), 1, pFinishedRecipeConsumer);
        build(Enchantments.MULTISHOT, CountedIngredient.of(16, ItemTags.ARROWS), 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.PIERCING, CountedIngredient.of(8, Tags.Items.GEMS_PRISMARINE), 1, pFinishedRecipeConsumer);
        build(Enchantments.QUICK_CHARGE, CountedIngredient.of(16, Items.SUGAR), 1, pFinishedRecipeConsumer);
        build(Enchantments.RIPTIDE, CountedIngredient.of(8, Items.FIREWORK_ROCKET), 1, pFinishedRecipeConsumer);
        build(Enchantments.SOUL_SPEED, CountedIngredient.of(16, Items.SOUL_SOIL), 1, pFinishedRecipeConsumer);


        //enderio
        build(EIOEnchantments.SOULBOUND.get(), CountedIngredient.of(EIOTags.Items.GEMS_ENDER_CRYSTAL), 1, pFinishedRecipeConsumer);
        build(EIOEnchantments.WITHERING.get(), CountedIngredient.of(4, EIOItems.WITHERING_POWDER.get()), 1, pFinishedRecipeConsumer);
        build(EIOEnchantments.REPELLENT.get(), CountedIngredient.of(4, Items.ENDER_PEARL), 2, pFinishedRecipeConsumer);
        build(EIOEnchantments.AUTO_SMELT.get(), CountedIngredient.of(16, Items.BLAZE_POWDER), 1, pFinishedRecipeConsumer); //TODO
        build(EIOEnchantments.XP_BOOST.get(), CountedIngredient.of(16, Items.EXPERIENCE_BOTTLE), 1, pFinishedRecipeConsumer); //TODO
    }

    protected void build(Enchantment enchantment, CountedIngredient input, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new FinishedEnchantingRecipe(EnderIO.loc("enchanting/" + ForgeRegistries.ENCHANTMENTS.getKey(enchantment).getPath()), enchantment, input, levelModifier));
    }

    protected static class FinishedEnchantingRecipe extends EnderFinishedRecipe {

        private final Enchantment enchantment;
        private final CountedIngredient input;
        private final int costMultiplier;

        public FinishedEnchantingRecipe(ResourceLocation id, Enchantment enchantment, CountedIngredient input, int costMultiplier) {
            super(id);
            this.enchantment = enchantment;
            this.input = input;
            this.costMultiplier = costMultiplier;
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getCountedIngredientModIds(input));
            mods.add(ForgeRegistries.ENCHANTMENTS.getKey(enchantment).getNamespace());
            return mods;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("enchantment", ForgeRegistries.ENCHANTMENTS.getKey(enchantment).toString());
            json.add("input", input.toJson());
            json.addProperty("cost_multiplier", costMultiplier);
            super.serializeRecipeData(json);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.ENCHANTING.serializer().get();
        }

    }
}
