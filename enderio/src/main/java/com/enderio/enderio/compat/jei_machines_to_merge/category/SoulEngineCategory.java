package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.client.content.machines.gui.screen.SoulEngineScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei_machines_to_merge.util.SoulEngineRecipe;
import com.enderio.enderio.content.machines.soul_engine.SoulEngineBlockEntity;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.init.EIOBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class SoulEngineCategory implements IRecipeCategory<SoulEngineRecipe> {

    public static final IRecipeType<SoulEngineRecipe> TYPE = IRecipeType.create(EnderIO.MOD_ID, "soul_engine",
        SoulEngineRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SoulEngineCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SoulEngineScreen.BG_TEXTURE, 49, 18, 124, 53);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.SOUL_ENGINE.get()));
    }

    @Override
    public IRecipeType<SoulEngineRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.SOUL_ENGINE_TITLE;
    }

    @Override
    public int getWidth() {
        return 124;
    }

    @Override
    public int getHeight() {
        return 53;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulEngineRecipe recipe, IFocusGroup focuses) {
        List<FluidStack> list = recipe.soulData().fluidIngredient().fluids()
            .stream().map(f -> new FluidStack(f, SoulEngineBlockEntity.FLUID_CAPACITY))
            .toList();

        builder.addSlot(RecipeIngredientRole.INPUT, 31, 3)
                .addIngredients(NeoForgeTypes.FLUID_STACK, list)
                .setFluidRenderer(SoulEngineBlockEntity.FLUID_CAPACITY, false, 16, 47);

        EntityType<?> value = BuiltInRegistries.ENTITY_TYPE.get(recipe.entityType()).orElseThrow().value();
        SpawnEggItem.byId(value)
            .ifPresent(spawnEggHolder -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .add(new ItemStack(spawnEggHolder)));

        ItemStack stack = SoulVialItem.forSoul(Soul.of(recipe.entityType()));
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(stack);
    }

    @Override
    public void draw(SoulEngineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics,
            double mouseX, double mouseY) {
        background.draw(graphics);
        EntityType<?> value = BuiltInRegistries.ENTITY_TYPE.get(recipe.entityType()).orElseThrow().value();
        graphics.text(Minecraft.getInstance().font, value.getDescription().getString(), 50, 5, CommonColors.DARK_GRAY, false);

        graphics.text(Minecraft.getInstance().font, recipe.soulData().tickPerMb() + " t/mb", 50, 30, CommonColors.DARK_GRAY, false);
        graphics.text(Minecraft.getInstance().font, recipe.soulData().powerPerMb() + " µI/mb", 50, 40, CommonColors.DARK_GRAY, false);

    }
}
