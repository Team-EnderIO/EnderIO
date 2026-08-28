package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static mezz.jei.api.recipe.RecipeIngredientRole.*;

public class SagMillCategory extends MachineRecipeCategory<RecipeHolder<SagMillingRecipe>> {
    public static final IRecipeType<RecipeHolder<SagMillingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "sagmilling", SagMillingRecipe.class);

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/viewer/sag_mill.png");
    private static final int WIDTH = 123;
    private static final int HEIGHT = 65;

    private final IDrawable background;
    private final IDrawable icon;

    public SagMillCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.SAG_MILL.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<SagMillingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.SAG_MILL_TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SagMillingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(INPUT, 32, 1).add(recipe.value().input());

        IRecipeSlotBuilder gridingBallSlot = builder.addSlot(CRAFTING_STATION, 74, 12).add(new ItemStack(Items.AIR));
        if (recipe.value().bonusType().useGrindingBall()) {
            List<ItemStack> grindingBalls = BuiltInRegistries.ITEM
                .getDataMap(GrindingBallData.DATA_MAP_TYPE)
                .keySet()
                .stream()
                .map(BuiltInRegistries.ITEM::getValue)
                .filter(Objects::nonNull)
                .map(ItemStack::new)
                .collect(Collectors.toList());

            gridingBallSlot.addItemStacks(grindingBalls);
        }

        List<SagMillingRecipe.OutputItem> results = recipe.value().outputs();
        if (!results.isEmpty()) {
            builder.addSlot(OUTPUT, 1, 48)
                    .add(results.get(0).getItemStackTemplate().map(ItemStackTemplate::create).orElse(ItemStack.EMPTY))
                    .addRichTooltipCallback(chanceTooltip(recipe, results.get(0)));
        }

        if (results.size() > 1) {
            builder.addSlot(OUTPUT, 22, 48)
                    .add(results.get(1).getItemStackTemplate().map(ItemStackTemplate::create).orElse(ItemStack.EMPTY))
                    .addRichTooltipCallback(chanceTooltip(recipe, results.get(1)));
        }

        if (results.size() > 2) {
            builder.addSlot(OUTPUT, 43, 48)
                    .add(results.get(2).getItemStackTemplate().map(ItemStackTemplate::create).orElse(ItemStack.EMPTY))
                    .addRichTooltipCallback(chanceTooltip(recipe, results.get(2)));
        }

        if (results.size() > 3) {
            builder.addSlot(OUTPUT, 64, 48)
                    .add(results.get(3).getItemStackTemplate().map(ItemStackTemplate::create).orElse(ItemStack.EMPTY))
                    .addRichTooltipCallback(chanceTooltip(recipe, results.get(3)));
        }
    }

    private IRecipeSlotRichTooltipCallback chanceTooltip(RecipeHolder<SagMillingRecipe> recipe,
            SagMillingRecipe.OutputItem item) {
        return (recipeSlotView, tooltip) -> {
            if (item.chance() < 1.0f) {
                String chance = item.chance() > 0.01f
                        ? NumberFormat.getIntegerInstance(Locale.ENGLISH).format(item.chance() * 100)
                        : "<1";
                if (recipe.value().bonusType().useGrindingBall()) {
                    tooltip.add(TooltipUtil.styledWithArgs(MachinesLang.SAG_MILL_CHANCE_GRINDING_BALL, chance));
                } else {
                    tooltip.add(TooltipUtil.styledWithArgs(MachinesLang.SAG_MILL_CHANCE, chance));
                }
            }
        };
    }

    @Override
    public void draw(RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics,
            double mouseX, double mouseY) {
        background.draw(graphics);
        graphics.text(Minecraft.getInstance().font, getEnergyString(recipe, recipeSlotsView), 83, 47,
                0xff808080, false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 83 && mouseY > 47 && mouseX < 83 + mc.font.width(getEnergyString(recipe, recipeSlotsView))
            && mouseY < 47 + mc.font.lineHeight) {
            tooltip.add(EIOCommonLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }
    }


    private Component getEnergyString(RecipeHolder<SagMillingRecipe> recipe, IRecipeSlotsView recipeSlotsView) {
        @Nullable
        GrindingBallData data = recipeSlotsView.getSlotViews()
                .get(1)
                .getDisplayedItemStack()
                .map(i -> i.typeHolder().getData(GrindingBallData.DATA_MAP_TYPE))
                .orElse(GrindingBallData.IDENTITY);

        if (data == null) {
            data = GrindingBallData.IDENTITY;
        }

//        return TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, NumberFormat.getIntegerInstance(Locale.ENGLISH)
//                .format(recipe.value().getEnergyCost(data)));
        return Component.literal("TODO");
    }
}
