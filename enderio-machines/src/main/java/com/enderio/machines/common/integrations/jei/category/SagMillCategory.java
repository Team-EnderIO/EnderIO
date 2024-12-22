package com.enderio.machines.common.integrations.jei.category;

import static mezz.jei.api.recipe.RecipeIngredientRole.CATALYST;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

import com.enderio.EnderIOBase;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.blocks.sag_mill.SagMillingRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.lang.MachineLang;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SagMillCategory extends MachineRecipeCategory<RecipeHolder<SagMillingRecipe>> {
    public static final RecipeType<RecipeHolder<SagMillingRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIOBase.REGISTRY_NAMESPACE, "sagmilling", SagMillingRecipe.class);

    private static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/viewer/sag_mill.png");
    private static final int WIDTH = 123;
    private static final int HEIGHT = 65;

    private final IDrawable background;
    private final IDrawable icon;

    public SagMillCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SAG_MILL.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SagMillingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_SAG_MILL;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SagMillingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 32, 1).addItemStacks(List.of(recipe.value().input().getItems()));

        IRecipeSlotBuilder gridingBallSlot = builder.addSlot(CATALYST, 74, 12).addItemStack(new ItemStack(Items.AIR));
        if (recipe.value().bonusType().useGrindingBall()) {
            gridingBallSlot.addIngredients(VanillaTypes.ITEM_STACK,
                    Arrays.asList(Ingredient.of(EIOTags.Items.GRINDING_BALLS).getItems()));
        }

        List<SagMillingRecipe.OutputItem> results = recipe.value().outputs();
        if (!results.isEmpty()) {
            builder.addSlot(OUTPUT, 1, 48)
                    .addItemStack(results.get(0).getItemStack())
                    .addTooltipCallback(chanceTooltip(recipe, results.get(0)));
        }

        if (results.size() > 1) {
            builder.addSlot(OUTPUT, 22, 48)
                    .addItemStack(results.get(1).getItemStack())
                    .addTooltipCallback(chanceTooltip(recipe, results.get(1)));
        }

        if (results.size() > 2) {
            builder.addSlot(OUTPUT, 43, 48)
                    .addItemStack(results.get(2).getItemStack())
                    .addTooltipCallback(chanceTooltip(recipe, results.get(2)));
        }

        if (results.size() > 3) {
            builder.addSlot(OUTPUT, 64, 48)
                    .addItemStack(results.get(3).getItemStack())
                    .addTooltipCallback(chanceTooltip(recipe, results.get(3)));
        }
    }

    private IRecipeSlotTooltipCallback chanceTooltip(RecipeHolder<SagMillingRecipe> recipe,
            SagMillingRecipe.OutputItem item) {
        return (recipeSlotView, tooltip) -> {
            if (item.chance() < 1.0f) {
                String chance = item.chance() > 0.01f
                        ? NumberFormat.getIntegerInstance(Locale.ENGLISH).format(item.chance() * 100)
                        : "<1";
                if (recipe.value().bonusType().useGrindingBall()) {
                    tooltip.add(TooltipUtil.styledWithArgs(MachineLang.TOOLTIP_SAG_MILL_CHANCE_BALL, chance));
                } else {
                    tooltip.add(TooltipUtil.styledWithArgs(MachineLang.TOOLTIP_SAG_MILL_CHANCE, chance));
                }
            }
        };
    }

    @Override
    public void draw(RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, getEnergyString(recipe, recipeSlotsView), 83, 47,
                0xff808080, false);
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView,
            double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 83 && mouseY > 47 && mouseX < 83 + mc.font.width(getEnergyString(recipe, recipeSlotsView))
                && mouseY < 47 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }

    private Component getEnergyString(RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView) {
        return TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, NumberFormat.getIntegerInstance(Locale.ENGLISH)
                .format(recipe.value()
                        .getEnergyCost(recipeSlotsView.getSlotViews()
                                .get(1)
                                .getDisplayedItemStack()
                                .map(i -> i.getOrDefault(EIODataComponents.GRINDING_BALL, GrindingBallData.IDENTITY))
                                .orElse(GrindingBallData.IDENTITY))));
    }
}
