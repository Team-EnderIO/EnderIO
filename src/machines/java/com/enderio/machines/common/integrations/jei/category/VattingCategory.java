package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.VatScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.recipe.VatRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class VattingCategory implements IRecipeCategory<VatRecipe> {

    public static final RecipeType<VatRecipe> TYPE = RecipeType.create(EnderIO.MODID, "vatting", VatRecipe.class);

    private final IDrawable background, icon;
    public VattingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(VatScreen.BG_TEXTURE, 27,5, 123, 58);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.THE_VAT.get()));
    }

    @Override
    public RecipeType<VatRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_VATTING;
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
    public void setRecipe(IRecipeLayoutBuilder builder, VatRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> leftInputs = new ArrayList<>();
        recipe.getLeftInputItems().forEach(ingredient -> leftInputs.addAll(List.of(ingredient.getItems())));
        builder.addSlot(INPUT, 29,7).addIngredients(VanillaTypes.ITEM_STACK, leftInputs)
            .setSlotName("left");

        List<ItemStack> rightInputs = new ArrayList<>();
        recipe.getRightInputItems().forEach(ingredient -> rightInputs.addAll(List.of(ingredient.getItems())));
        builder.addSlot(INPUT, 78,7).addIngredients(VanillaTypes.ITEM_STACK, rightInputs)
            .setSlotName("right");

        //Since the fluids depend on which of the items in the list is active,
        // we need to set the fluid amount dynamically. Therefore we set the output amount to 0.
        builder.addSlot(INPUT, 3, 7).addFluidStack(recipe.getInputFluid(), 0)
            .setFluidRenderer(8000, true, 15,47)
            .setSlotName("inputFluid");
        builder.addSlot(OUTPUT, 105, 7).addFluidStack(recipe.getOutputFluid(), 0)
            .setFluidRenderer(8000, true, 15,47)
            .setSlotName("outputFluid");
    }

    @Override
    public void draw(VatRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);

        //We find the current item combo to determine how much fluid we should put in the stacks.
        ItemStack leftItem = findItem(recipeSlotsView, "left");
        ItemStack rightItem = findItem(recipeSlotsView, "right");
        float leftModifier = recipe.getMultiplierForLeftInputItem(leftItem);
        float rightModifier = recipe.getMultiplierForRightInputItem(rightItem);

        renderString(leftModifier + "x", stack, 27, -3);
        renderString(rightModifier + "x", stack, 76, -3);

        FluidStack inputFluidStack = findFluidStack(recipeSlotsView, "inputFluid");
        FluidStack outputFluidStack = findFluidStack(recipeSlotsView, "outputFluid");

        inputFluidStack.setAmount(recipe.calcFluidConsumption(leftModifier, rightModifier));
        outputFluidStack.setAmount(recipe.calcFluidProduction(leftModifier, rightModifier));
    }

    private FluidStack findFluidStack(IRecipeSlotsView recipeSlotsView, String name){
        Optional<IRecipeSlotView> slot = recipeSlotsView.findSlotByName(name);
        assert slot.isPresent();
        Optional<FluidStack> fluid = slot.get().getDisplayedIngredient(ForgeTypes.FLUID_STACK);
        assert fluid.isPresent();
        return fluid.get();
    }
    private ItemStack findItem(IRecipeSlotsView view, String slotName){
        Optional<IRecipeSlotView> itemSlot = view.findSlotByName(slotName);
        assert itemSlot.isPresent();
        Optional<ItemStack> item = itemSlot.get().getDisplayedItemStack();
        assert item.isPresent();
        return item.get();
    }

    private void renderString(String text, PoseStack stack, int x, int y){
        stack.pushPose();
        GuiComponent.drawString(stack, Minecraft.getInstance().font, text, x,y, ChatFormatting.GRAY.getColor());
        stack.popPose();
    }
}
