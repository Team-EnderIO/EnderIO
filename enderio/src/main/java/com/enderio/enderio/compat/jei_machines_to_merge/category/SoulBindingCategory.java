package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.client.content.machines.gui.screen.SoulBinderScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.compat.jei_machines_to_merge.util.RecipeUtil;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class SoulBindingCategory extends MachineRecipeCategory<RecipeHolder<SoulBindingRecipe>> {
    public static final IRecipeType<RecipeHolder<SoulBindingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "soul_binding", SoulBindingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SoulBindingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(SoulBinderScreen.BG_TEXTURE, 35, 30, 118, 44);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(EIOBlocks.SOUL_BINDER.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<SoulBindingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return JEILang.SOUL_BINDING_TITLE;
    }

    @Override
    public int getWidth() {
        return 118;
    }

    @Override
    public int getHeight() {
        return 44;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SoulBindingRecipe> recipe, IFocusGroup focuses) {
        List<ItemStack> vials = new ArrayList<>();
        var soulIngredient = recipe.value().inputSoul();
        var input = focuses.getItemStackFocuses(INPUT)
            .map(f -> f.getTypedValue().getIngredient())
            .filter(stack -> stack.is(EIOItems.SOUL_VIAL.asItem()))
            .filter(stack -> {
                var soul = SoulBoundUtils.getBoundSoul(stack);
                return !soul.isEmpty() && soulIngredient.test(soul);
            })
            .findFirst();
        var outputSoul = focuses.getItemStackFocuses(OUTPUT)
            .map(f -> SoulBoundUtils.getBoundSoul(f.getTypedValue().getIngredient()))
            .filter(soul -> !soul.isEmpty() && soulIngredient.test(soul))
            .findFirst();

        if (input.isPresent()) {
            vials.add(input.get());
        } else if (outputSoul.isPresent()) {
            vials.add(SoulVialItem.forSoul(outputSoul.get().copyOnlyType()));
        } else {
            for (var entityType : soulIngredient.getEntityTypes()) {
                vials.add(SoulVialItem.forSoul(Soul.of(entityType.value())));
            }
        }

        builder.addSlot(INPUT, 24, 4).add(recipe.value().placementInfo().ingredients().getFirst());

        var resultStack = RecipeUtil.getResultStacks(recipe).getFirst().getItem();
        var results = new ArrayList<ItemStack>();
        var boundVials = new ArrayList<ItemStack>();

        // If the output can take an entity type, then we add it
        if (SoulBoundUtils.canBindSoul(resultStack)) {
            for (ItemStack vial : vials) {
                var soul = SoulBoundUtils.getBoundSoul(vial);
                if (!soul.isEmpty()) {
                    var result = resultStack.copy();
                    if (SoulBoundUtils.tryBindSoul(result, Soul.of(soul.entityType()))) {
                        boundVials.add(vial);
                        results.add(result);
                    }
                }
            }
        }

        // Keep each bound output at the same index as its input soul.
        boolean linkSouls = !results.isEmpty();
        if (linkSouls) {
            vials = boundVials;
        }

        if (results.isEmpty()) {
            results.add(resultStack);
        }

        builder.addSlot(OUTPUT, 77, 4).add(new ItemStack(EIOItems.SOUL_VIAL.get()));

        var vialSlot = builder.addSlot(INPUT, 3, 4).addItemStacks(vials);
        var resultSlot = builder.addSlot(OUTPUT, 99, 4).addItemStacks(results);
        if (linkSouls) {
            builder.createFocusLink(vialSlot, resultSlot);
        }
    }

    @Override
    public void draw(RecipeHolder<SoulBindingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        int cost = recipe.value().experience();
        String costText = cost < 0 ? "err" : Integer.toString(cost);
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        // Show red if the player doesn't have enough levels
        int mainColor = playerHasEnoughLevels(player, cost) ? 0xFF80FF20 : 0xFFFF6060;
        guiGraphics.text(minecraft.font, text, 5, 24, mainColor);

        guiGraphics.text(Minecraft.getInstance().font, getBasicEnergyString(recipe), 5, 34, 0xff808080, false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<SoulBindingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mouseX > 5 && mouseY > 34 && mouseX < 5 + mc.font.width(getBasicEnergyString(recipe))
            && mouseY < 34 + mc.font.lineHeight) {
            tooltip.add(EIOCommonLang.TOOLTIP_ENERGY_EQUIVALENCE);
        }
    }
}
