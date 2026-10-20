package com.enderio.enderio.compat.jei_machines_to_merge.category;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.client.content.machines.gui.screen.SoulBinderScreen;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.compat.jei_machines_to_merge.util.MachineRecipeCategory;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class SoulBindingCategory extends MachineRecipeCategory<RecipeHolder<SoulBindingRecipe>> {
    public static final IRecipeType<RecipeHolder<SoulBindingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "soul_binding", SoulBindingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IIngredientManager ingredientManager;

    public SoulBindingCategory(IGuiHelper guiHelper, IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
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
        var soulInputs = resolveSoulInputs(recipe, focuses);

        builder.addSlot(INPUT, 24, 4).setSlotName("item_input").add(recipe.value().placementInfo().ingredients().getFirst());

        var results = new ArrayList<ItemStack>();
        var remainders = new ArrayList<ItemStack>();
        var validSoulInputs = new ArrayList<ItemStack>();
        boolean bindsSoul = recipe.value().outputMode().bindsSoul();
        for (ItemStack soulInput : soulInputs) {
            var extracted = SoulBindingRecipe.resolveSoulAndRemainder(soulInput);
            if (extracted.isEmpty()) {
                continue;
            }

            if (bindsSoul) {
                for (var item : recipe.value().input().items().toList()) {
                    recipe.value().resolveOutputStack(item.value().getDefaultInstance(), extracted.get().soul()).ifPresent(result -> {
                        addDistinctStack(validSoulInputs, soulInput);
                        addDistinctStack(remainders, extracted.get().remainder());
                        addDistinctStack(results, result);
                    });
                }
            } else {
                addDistinctStack(validSoulInputs, soulInput);
                addDistinctStack(remainders, extracted.get().remainder());
            }
        }

        var specifiedOutput = recipe.value().output();
        if (!bindsSoul && specifiedOutput != null) {
            results.add(specifiedOutput.create());
        }

        builder.addSlot(INPUT, 3, 4).setSlotName("soul_source").addItemStacks(validSoulInputs);
        List<Optional<ITypedIngredient<?>>> remainderIngredients = new ArrayList<>();
        for (var remainder : remainders) {
            if (!remainder.isEmpty()) {
                ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, remainder)
                    .ifPresent(ingredient -> remainderIngredients.add(Optional.of(ingredient)));
            }
        }
        // Register distinct remainders for lookup; the display override follows the current source.
        if (remainderIngredients.isEmpty()) {
            remainderIngredients.add(Optional.empty());
        }
        builder.addSlot(OUTPUT, 77, 4).setSlotName("remainder").addOptionalTypedIngredients(remainderIngredients);
        builder.addSlot(OUTPUT, 99, 4).setSlotName("bound_result").addItemStacks(results);
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<SoulBindingRecipe> recipe, List<IRecipeSlotDrawable> slots, IFocusGroup focuses) {
        var extracted = findSlot(slots, "soul_source")
            .flatMap(IRecipeSlotDrawable::getDisplayedItemStack)
            .flatMap(SoulBindingRecipe::resolveSoulAndRemainder);
        var remainder = extracted.map(SoulBindingRecipe.ExtractedSoulSource::remainder).orElse(ItemStack.EMPTY);
        findSlot(slots, "remainder").ifPresent(slot -> setDisplayedStack(slot, remainder));

        if (!recipe.value().outputMode().bindsSoul()) {
            return;
        }
        var item = findSlot(slots, "item_input")
            .flatMap(IRecipeSlotDrawable::getDisplayedItemStack)
            .orElse(ItemStack.EMPTY);
        var result = extracted.flatMap(source -> recipe.value().resolveOutputStack(item, source.soul())).orElse(ItemStack.EMPTY);
        findSlot(slots, "bound_result").ifPresent(slot -> setDisplayedStack(slot, result));
    }

    private static void setDisplayedStack(IRecipeSlotDrawable slot, ItemStack stack) {
        var display = slot.createDisplayOverrides();
        if (stack.isEmpty()) {
            display.addOptionalTypedIngredients(List.of(Optional.empty()));
        } else {
            display.add(stack);
        }
    }

    private static Optional<IRecipeSlotDrawable> findSlot(List<IRecipeSlotDrawable> slots, String name) {
        return slots.stream().filter(slot -> slot.getSlotName().filter(name::equals).isPresent()).findFirst();
    }

    private static void addDistinctStack(List<ItemStack> stacks, ItemStack stack) {
        if (stacks.stream().noneMatch(existing -> existing.getCount() == stack.getCount()
            && ItemStack.isSameItemSameComponents(existing, stack))) {
            stacks.add(stack);
        }
    }

    private List<ItemStack> resolveSoulInputs(RecipeHolder<SoulBindingRecipe> recipe, IFocusGroup focuses) {
        var focusedInput = focuses.getItemStackFocuses(INPUT)
            .map(focus -> focus.getTypedValue().getIngredient())
            .filter(stack -> isSoulAllowed(recipe.value().inputSoul(), getAvailableSoul(stack)))
            .findFirst();
        if (focusedInput.isPresent()) {
            return List.of(focusedInput.get());
        }

        var focusedSoul = focuses.getItemStackFocuses(OUTPUT)
            .map(focus -> SoulBoundUtils.getBoundSoul(focus.getTypedValue().getIngredient()))
            .filter(soul -> isSoulAllowed(recipe.value().inputSoul(), soul))
            .findFirst();

        return ingredientManager.getAllItemStacks().stream()
            .filter(stack -> recipe.value().soulSource().test(stack))
            .filter(stack -> {
                var soul = getAvailableSoul(stack);
                return isSoulAllowed(recipe.value().inputSoul(), soul)
                    && (focusedSoul.isEmpty() || soul.entityType() == focusedSoul.get().entityType());
            })
            // Stable sorting preserves JEI's order for non-vials and vials with equal mob names.
            .sorted(Comparator.comparingInt((ItemStack stack) -> stack.is(EIOItems.SOUL_VIAL) ? 0 : 1))
            .toList();
    }

    private static boolean isSoulAllowed(SoulIngredient ingredient, Soul soul) {
        return !soul.isEmpty() && ingredient.test(soul)
            && EntityCaptureUtils.getCapturableStatus(soul.entityType()) == EntityCaptureUtils.CapturableStatus.CAPTURABLE;
    }

    private static Soul getAvailableSoul(ItemStack stack) {
        if (stack.isEmpty()) {
            return Soul.EMPTY;
        }
        var source = ItemAccess.forStack(stack).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        return source != null && source.isSoulAvailable() ? source.availableSoul() : Soul.EMPTY;
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
