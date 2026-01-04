package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.content.machines.vat.VatBlockEntity;
import com.enderio.enderio.foundation.datamap.VatReagent;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class VATCategory extends MachineRecipeCategory<RecipeHolder<FermentingRecipe>> {
    public static final IRecipeType<RecipeHolder<FermentingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "vat_fermenting", FermentingRecipe.class);

    private final IDrawable icon;

    public VATCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.VAT.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<FermentingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.VAT_TITLE;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 53;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FermentingRecipe> recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 28, 2)
                .add(Ingredient.of(BuiltInRegistries.ITEM.get(recipe.value().leftReagent()).orElseThrow()))
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("x"
                        + getModifier(recipeSlotView.getDisplayedItemStack().get(), recipe.value().leftReagent()))));

        builder.addSlot(RecipeIngredientRole.INPUT, 77, 2)
                .add(Ingredient.of(BuiltInRegistries.ITEM.get(recipe.value().rightReagent()).orElseThrow()))
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("x"
                        + getModifier(recipeSlotView.getDisplayedItemStack().get(), recipe.value().rightReagent()))));

        for (var fluid : recipe.value().input().ingredient().fluids()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
                    .add(fluid.value(), recipe.value().input().amount())
                    .setFluidRenderer(VatBlockEntity.TANK_CAPACITY, false, 15, 47);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 2)
                .add(recipe.value().output().getFluid(), (long) (recipe.value().output().getAmount()))
                .setFluidRenderer(VatBlockEntity.TANK_CAPACITY, false, 15, 47);
    }

    public static double getModifier(ItemStack stack, TagKey<Item> reagent) {
        var map = stack.getItemHolder().getData(VatReagent.DATA_MAP);
        if (map != null) {
            return map.getOrDefault(reagent, 1D);
        }
        return 1;
    }

    @Override
    public void draw(RecipeHolder<FermentingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        // left modifier
        ItemStack item = recipeSlotsView.getSlotViews().get(0).getDisplayedItemStack().get();
        double modifier = FermentingRecipe.getModifier(item, recipe.value().leftReagent());
        String text = "x" + modifier;
        Font font = Minecraft.getInstance().font;
        int x = 28 + 8 - font.width(text) / 2;
        guiGraphics.drawString(font, text, x, 22, 4210752, false);

        // right modifier
        item = recipeSlotsView.getSlotViews().get(1).getDisplayedItemStack().get();
        modifier = FermentingRecipe.getModifier(item, recipe.value().rightReagent());
        text = "x" + modifier;
        x = 77 + 8 - font.width(text) / 2;
        guiGraphics.drawString(font, text, x, 22, 4210752, false);
    }
}
