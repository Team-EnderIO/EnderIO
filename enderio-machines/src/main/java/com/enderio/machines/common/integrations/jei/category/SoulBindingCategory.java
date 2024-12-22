package com.enderio.machines.common.integrations.jei.category;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

import com.enderio.EnderIOBase;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.client.gui.screen.SoulBinderScreen;
import com.enderio.machines.common.blocks.soul_binder.SoulBindingRecipe;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.util.MachineRecipeCategory;
import com.enderio.machines.common.integrations.jei.util.RecipeUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.souldata.SoulData;
import com.enderio.machines.common.souldata.SoulDataReloadListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SoulBindingCategory extends MachineRecipeCategory<RecipeHolder<SoulBindingRecipe>> {
    public static final RecipeType<RecipeHolder<SoulBindingRecipe>> TYPE = JEIUtils
            .createRecipeType(EnderIOBase.REGISTRY_NAMESPACE, "soul_binding", SoulBindingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SoulBindingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SoulBinderScreen.BG_TEXTURE, 35, 30, 118, 44);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MachineBlocks.SOUL_BINDER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SoulBindingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return MachineLang.CATEGORY_SOUL_BINDING;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SoulBindingRecipe> recipe, IFocusGroup focuses) {
        List<ItemStack> vials = new ArrayList<>();
        Optional<IFocus<ItemStack>> output = focuses.getItemStackFocuses(OUTPUT).findFirst();
        Optional<IFocus<ItemStack>> input = focuses.getItemStackFocuses(INPUT)
                .filter(f -> f.getTypedValue().getItemStack().get().is(EIOItems.FILLED_SOUL_VIAL.asItem()))
                .findFirst();

        if (input.isPresent()) {
            vials.add(input.get().getTypedValue().getIngredient());
        } else if (recipe.value().entityType().isPresent()) {
            var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
            SoulVialItem.setEntityType(item, recipe.value().entityType().get());

            vials.add(item);
        } else if (recipe.value().mobCategory().isPresent()) {

            var allEntitiesOfCategory = BuiltInRegistries.ENTITY_TYPE.stream()
                    .filter(e -> e.getCategory().equals(recipe.value().mobCategory().get()))
                    .map(BuiltInRegistries.ENTITY_TYPE::getKey)
                    .toList();

            for (ResourceLocation entity : allEntitiesOfCategory) {
                var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                SoulVialItem.setEntityType(item, entity);
                vials.add(item);
            }

        } else if (recipe.value().soulData().isPresent()) {
            if (output.isPresent()) {
                var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                if (output.get().getTypedValue().getItemStack().get().is(EIOTags.Items.ENTITY_STORAGE)) {
                    StoredEntityData data = output.get()
                            .getTypedValue()
                            .getItemStack()
                            .get()
                            .get(EIODataComponents.STORED_ENTITY);
                    SoulVialItem.setEntityType(item, data.entityType().get());
                    vials.add(item);
                }
            } else {
                SoulDataReloadListener<? extends SoulData> soulDataReloadListener = SoulDataReloadListener
                        .fromString(recipe.value().soulData().get());

                var allEntitiesOfSoulData = BuiltInRegistries.ENTITY_TYPE.keySet()
                        .stream()
                        .filter(r -> soulDataReloadListener.map.containsKey(r))
                        .toList();

                for (ResourceLocation entity : allEntitiesOfSoulData) {
                    var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                    SoulVialItem.setEntityType(item, entity);
                    vials.add(item);
                }
            }
        } else {
            if (output.isPresent()) {
                var item = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get());
                if (output.get().getTypedValue().getItemStack().get().is(EIOTags.Items.ENTITY_STORAGE)) {
                    StoredEntityData data = output.get()
                            .getTypedValue()
                            .getItemStack()
                            .get()
                            .get(EIODataComponents.STORED_ENTITY);
                    SoulVialItem.setEntityType(item, data.entityType().get());
                    vials.add(item);
                }
            } else {
                vials.addAll(SoulVialItem.getAllFilled());
            }
        }

        builder.addSlot(INPUT, 3, 4).addItemStacks(vials);

        builder.addSlot(INPUT, 24, 4).addIngredients(recipe.value().getInput());

        var resultStack = RecipeUtil.getResultStacks(recipe).get(0).getItem();
        var results = new ArrayList<ItemStack>();

        // If the output can take an entity type, then we add it
        if (resultStack.is(EIOTags.Items.ENTITY_STORAGE)) {
            for (ItemStack vial : vials) {
                SoulVialItem.getEntityData(vial).flatMap(StoredEntityData::entityType).ifPresent(entityType -> {
                    var result = resultStack.copy();
                    if (result.is(EIOTags.Items.ENTITY_STORAGE)) {
                        result.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entityType));
                        results.add(result);
                    }
                });
            }
        }

        // Fallback :(
        if (results.size() == 0) {
            results.add(resultStack);
        }

        builder.addSlot(OUTPUT, 77, 4).addItemStack(new ItemStack(EIOItems.EMPTY_SOUL_VIAL.get()));

        builder.addSlot(OUTPUT, 99, 4).addItemStacks(results);
    }

    @Override
    public void draw(RecipeHolder<SoulBindingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        int cost = recipe.value().experience();
        String costText = cost < 0 ? "err" : Integer.toString(cost);
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        // Show red if the player doesn't have enough levels
        int mainColor = playerHasEnoughLevels(player, cost) ? 0xFF80FF20 : 0xFFFF6060;
        guiGraphics.drawString(minecraft.font, text, 5, 24, mainColor);

        guiGraphics.drawString(Minecraft.getInstance().font, getBasicEnergyString(recipe), 5, 34, 0xff808080, false);
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<SoulBindingRecipe> recipe, IRecipeSlotsView recipeSlotsView,
            double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 5 && mouseY > 34 && mouseX < 5 + mc.font.width(getBasicEnergyString(recipe))
                && mouseY < 34 + mc.font.lineHeight) {
            return List.of(MachineLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }

        return List.of();
    }
}
