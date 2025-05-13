package com.enderio.armory.data.recipe;

import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgradeTier;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

public class ItemRecipeProvider extends RecipeProvider {

    public ItemRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addDarkSteelArmor(recipeOutput);
        addDarkSteelTools(recipeOutput);
        addDarkSteelUpgrades(recipeOutput);
    }

    private void addDarkSteelArmor(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ArmoryItems.DARK_STEEL_HELMET.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("III")
                .pattern("I I")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ArmoryItems.DARK_STEEL_CHESTPLATE.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("I I")
                .pattern("III")
                .pattern("III")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ArmoryItems.DARK_STEEL_LEGGINGS.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("III")
                .pattern("I I")
                .pattern("I I")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ArmoryItems.DARK_STEEL_BOOTS.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("I I")
                .pattern("I I")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);
    }

    private void addDarkSteelTools(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ArmoryItems.DARK_STEEL_SWORD.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('S', Tags.Items.RODS_WOODEN)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ArmoryItems.DARK_STEEL_PICKAXE.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('S', Tags.Items.RODS_WOODEN)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ArmoryItems.DARK_STEEL_AXE.get())
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('S', Tags.Items.RODS_WOODEN)
                .pattern("II")
                .pattern("IS")
                .pattern(" S")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
                .save(recipeOutput);
    }

    private void addDarkSteelUpgrades(RecipeOutput recipeOutput) {

        ItemStack output = ArmoryItems.DARK_STEEL_UPGRADE_BLANK.toStack();
        ResourceLocation id = EnderIO
                .loc("alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath());
        List<@NotNull SizedIngredient> inputs = List.of(SizedIngredient.of(EIOBlocks.DARK_STEEL_BARS, 1),
                SizedIngredient.of(Items.CLAY_BALL, 1), SizedIngredient.of(Items.STRING, 4));
        recipeOutput.accept(id, new AlloySmeltingRecipe(inputs, output, 6400, 0.3f), null);

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_1, Ingredient.of(EIOItems.VIBRANT_CRYSTAL));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_2, Ingredient.of(EIOItems.BASIC_CAPACITOR));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_3,
                Ingredient.of(EIOItems.DOUBLE_LAYER_CAPACITOR));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_4, Ingredient.of(EIOItems.OCTADIC_CAPACITOR));

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_FORK, Ingredient.of(Items.DIAMOND_HOE));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SPOON, Ingredient.of(Items.DIAMOND_SHOVEL));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_TRAVEL, Ingredient.of(EIOItems.TRAVEL_STAFF));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ArmoryItems.DARK_STEEL_UPGRADE_DIRECT.get())
                .define('I', EIOItems.VIBRANT_ALLOY_INGOT.get())
                .define('N', EIOItems.VIBRANT_ALLOY_NUGGET.get())
                .define('E', Tags.Items.ENDER_PEARLS)
                .define('B', ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get())
                .pattern("NIN")
                .pattern("IEI")
                .pattern("NBN")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get()))
                .save(recipeOutput);

        // TODO:
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_1, Ingredient.of(Items.TNT),
//                Ingredient.of(EIOTags.Items.GEARS_WOOD));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_2, Ingredient.of(Items.TNT),
//                Ingredient.of(EIOTags.Items.GEARS_STONE));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1,
                Ingredient.of(Items.GUNPOWDER));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2,
                Ingredient.of(Items.CREEPER_HEAD));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ArmoryItems.DARK_STEEL_UPGRADE_STEP_ASSIST.get())
                .define('I', Items.BRICK)
                .define('B', ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get())
                .pattern("  I")
                .pattern(" II")
                .pattern("IIB")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get()))
                .save(recipeOutput);

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SPEED_I, Ingredient.of(EIOItems.GEAR_IRON),
                Ingredient.of(Items.SUGAR));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SPEED_II, Ingredient.of(EIOItems.GEAR_ENERGIZED),
                Ingredient.of(Items.SUGAR));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SPEED_III, Ingredient.of(EIOItems.GEAR_VIBRANT),
                Ingredient.of(Items.SUGAR));

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_JUMP_I, Ingredient.of(EIOItems.GEAR_IRON),
                Ingredient.of(Items.PISTON));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_JUMP_II, Ingredient.of(EIOItems.GEAR_ENERGIZED),
                Ingredient.of(Items.PISTON));

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_GLIDER, Ingredient.of(EIOItems.GLIDER));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_ELYTRA, Ingredient.of(Items.ELYTRA));

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_NIGHT_VISION, Ingredient.of(Items.GOLDEN_CARROT));

        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SOLAR_I,
                Ingredient.of(MachineBlocks.SOLAR_PANELS.get(SolarUpgradeTier.ONE.getPanelTier())));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SOLAR_II,
                Ingredient.of(MachineBlocks.SOLAR_PANELS.get(SolarUpgradeTier.TWO.getPanelTier())));
        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SOLAR_III,
                Ingredient.of(MachineBlocks.SOLAR_PANELS.get(SolarUpgradeTier.THREE.getPanelTier())));

    }

    private void addUpgrade(RecipeOutput recipeOutput, ItemLike result, Ingredient... upgradeItems) {
        var builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
                .requires(ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get());

        for (Ingredient i : upgradeItems) {
            builder.requires(i);
        }

        builder.unlockedBy("has_ingredient",
                InventoryChangeTrigger.TriggerInstance.hasItems(ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get()))
                .save(recipeOutput);
    }
}
