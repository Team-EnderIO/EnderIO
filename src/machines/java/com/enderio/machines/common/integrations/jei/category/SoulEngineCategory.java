package com.enderio.machines.common.integrations.jei.category;

import com.enderio.EnderIO;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.machines.client.gui.screen.SoulEngineScreen;
import com.enderio.machines.common.blockentity.SoulEngineBlockEntity;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.souldata.EngineSoul;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulEngineCategory implements IRecipeCategory<EngineSoul.SoulData> {

    public static final RecipeType<EngineSoul.SoulData> TYPE = RecipeType.create(EnderIO.MODID, "soul_engine", EngineSoul.SoulData.class);
    private final IDrawableStatic background;
    private final IDrawable icon;

    public SoulEngineCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SoulEngineScreen.BG_TEXTURE, 49, 18, 124, 53);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SOUL_ENGINE.get()));
    }
    @Override
    public RecipeType<EngineSoul.SoulData> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_SOUL_ENGINE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, EngineSoul.SoulData recipe, IFocusGroup focuses) {
        List<FluidStack> list = new ArrayList<>();
        String fluid = recipe.fluid();
        if (fluid.startsWith("#")) { //We have a fluid tag instead
            TagKey<Fluid> tag = TagKey.create(Registries.FLUID, new ResourceLocation(fluid.substring(1)));
            ForgeRegistries.FLUIDS.tags().getTag(tag).stream().forEach(f -> list.add(new FluidStack(f, SoulEngineBlockEntity.FLUID_CAPACITY)));
        } else {
            Optional<Holder.Reference<Fluid>> delegate = ForgeRegistries.FLUIDS.getDelegate(new ResourceLocation(fluid));
            delegate.ifPresent(fluidReference -> list.add(new FluidStack(fluidReference.get(), SoulEngineBlockEntity.FLUID_CAPACITY)));
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 31, 3)
            .addIngredients(ForgeTypes.FLUID_STACK, list)
            .setFluidRenderer(SoulEngineBlockEntity.FLUID_CAPACITY, false, 16, 47);

        EntityType<?> value = ForgeRegistries.ENTITY_TYPES.getValue(recipe.entitytype());
        if (recipe.getKey().equals(ForgeRegistries.ENTITY_TYPES.getKey(value))) {
            if (ForgeSpawnEggItem.fromEntityType(value) != null) {
                builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addItemStack(new ItemStack(ForgeSpawnEggItem.fromEntityType(value)));
            }

            ItemStack stack = new ItemStack(EIOItems.FILLED_SOUL_VIAL);
            stack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(c -> c.setStoredEntityData(StoredEntityData.of(recipe.entitytype())));
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addItemStack(stack);
        }

    }

    @Override
    public void draw(EngineSoul.SoulData recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        EntityType<?> value = ForgeRegistries.ENTITY_TYPES.getValue(recipe.entitytype());
        if (recipe.getKey().equals(ForgeRegistries.ENTITY_TYPES.getKey(value))) {
            guiGraphics.drawString(Minecraft.getInstance().font, value.getDescription().getString(), 50, 5, 4210752, false);
        }

        guiGraphics.drawString(Minecraft.getInstance().font, recipe.tickpermb() + " t/mb", 50, 30, 4210752, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.powerpermb() + " µI/mb", 50, 40, 4210752, false);

    }
}
