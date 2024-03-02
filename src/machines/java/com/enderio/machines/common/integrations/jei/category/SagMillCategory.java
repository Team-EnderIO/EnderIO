package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.GrindingBallManager;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.gui.screen.SagMillScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.SagMillingRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static mezz.jei.api.recipe.RecipeIngredientRole.CATALYST;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class SagMillCategory extends MachineRecipeCategory<SagMillingRecipe> {
    public static final RecipeType<SagMillingRecipe> TYPE = RecipeType.create(EnderIO.MODID, "sagmilling", SagMillingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SagMillCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SagMillScreen.BG_TEXTURE, 48, 11, 99 + 24, 65);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SAG_MILL.get()));
    }

    @Override
    public RecipeType<SagMillingRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, SagMillingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 32, 1)
            .addItemStacks(List.of(recipe.getInput().getItems()));

        IRecipeSlotBuilder gridingBallSlot = builder.addSlot(CATALYST, 74, 12)
            .addItemStack(new ItemStack(Items.AIR));
        if (recipe.getBonusType().useGrindingBall()) {
            gridingBallSlot.addItemStacks(GrindingBallManager.getGrindingBalls().stream().map(ItemStack::new).toList());
        }

        List<SagMillingRecipe.OutputItem> results = recipe.getOutputs();
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

    private IRecipeSlotTooltipCallback chanceTooltip(SagMillingRecipe recipe, SagMillingRecipe.OutputItem item) {
        return (recipeSlotView, tooltip) -> {
            if (item.getChance() < 1.0f) {
                String chance = item.getChance() > 0.01f ? NumberFormat.getIntegerInstance(Locale.ENGLISH).format(item.getChance() * 100) : "<1";
                if (recipe.getBonusType().useGrindingBall()) {
                    tooltip.add(TooltipUtil.styledWithArgs(MachineLang.TOOLTIP_SAG_MILL_CHANCE_BALL, chance));
                } else {
                    tooltip.add(TooltipUtil.styledWithArgs(MachineLang.TOOLTIP_SAG_MILL_CHANCE, chance));
                }
            }
        };
    }

    @Override
    public void draw(SagMillingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, getEnergyString(recipe, recipeSlotsView), 83, 47, 0xff808080, false);
    }

    @Override
    public List<Component> getTooltipStrings(SagMillingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 83 && mouseY > 47 && mouseX < 83 + mc.font.width(getEnergyString(recipe, recipeSlotsView)) && mouseY < 47 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }

    private Component getEnergyString(SagMillingRecipe recipe, IRecipeSlotsView recipeSlotsView) {
        return TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, NumberFormat
            .getIntegerInstance(Locale.ENGLISH)
            .format(recipe.getEnergyCost(
                recipeSlotsView.getSlotViews().get(1).getDisplayedItemStack().map(GrindingBallManager::getData).orElse(IGrindingBallData.IDENTITY))));
    }
}
