package com.enderio.base.common.integrations.jei.category;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.integrations.jei.JEIUtils;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * @author TagnumElite
 */
public class FireCraftingCategory implements IRecipeCategory<RecipeHolder<FireCraftingRecipe>> {

    public static final RecipeType<RecipeHolder<FireCraftingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.NAMESPACE,
            "fire_crafting", FireCraftingRecipe.class);

    private static final ResourceLocation BG_LOCATION = EnderIO.loc("textures/gui/jei_infinity.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final ITickTimer timer;
    private final Map<ResourceLocation, Integer> blockIdx = new HashMap<>();
    private int changed = 0;
    private boolean alternateFire = false;

    public FireCraftingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG_LOCATION, 0, 0, 109, 62);
        this.icon = guiHelper.createDrawable(BG_LOCATION, 109, 0, 16, 16);

        this.timer = guiHelper.createTickTimer(40, 1, false);
    }

    @Override
    public RecipeType<RecipeHolder<FireCraftingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return EIOLang.JEI_FIRE_CRAFTING_TITLE;
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
    public List<Component> getTooltipStrings(RecipeHolder<FireCraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView,
            double mouseX, double mouseY) {
        // Middle Right, above the tooltip icon
        if (mouseX >= 87 && mouseX <= 105 && mouseY >= 25 && mouseY <= 38) {
            List<ResourceKey<Level>> validDimensions = recipe.value().dimensions();
            List<Component> tooltip = new ArrayList<>(validDimensions.size() + 1);
            tooltip.add(EIOLang.JEI_FIRE_CRAFTING_VALID_DIMENSIONS);

            for (ResourceKey<Level> dim : validDimensions) {
                tooltip.add(Component.literal("- " + dim.location()));
            }

            return tooltip;
        }
        // Block tool tip
        if (mouseX >= 17 && mouseX <= 47 && mouseY >= 31 && mouseY <= 57) {
            List<Block> bases = recipe.value().getAllBaseBlocks();
            List<Component> tooltip = new ArrayList<>(bases.size() + 1);
            tooltip.add(EIOLang.JEI_FIRE_CRAFTING_VALID_BLOCKS);

            for (Block block : bases) {
                tooltip.add(Component.literal("- ").append(block.getName()));
            }

            return tooltip;
        }

        return List.of();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FireCraftingRecipe> recipe, IFocusGroup focuses) {
        IIngredientAcceptor<?> block = builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST);
        block.addIngredients(Ingredient.of(recipe.value().getAllBaseBlocks().toArray(Block[]::new)));

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 39)
                .addRichTooltipCallback((slotView, tooltip) -> {
                    slotView.getDisplayedItemStack().ifPresent(stack -> {
                        // Gross way of getting the index.
                        int index = slotView.getItemStacks().toList().indexOf(stack);
                        var result = recipe.value().results().get(index);

                        tooltip.add(TooltipUtil.withArgs(EIOLang.JEI_FIRE_CRAFTING_CHANCE,
                                Math.round(result.chance() * 100)));

                        if (result.minCount() != result.maxCount()) {
                            tooltip.add(TooltipUtil.withArgs(EIOLang.JEI_FIRE_CRAFTING_DROPS,
                                    result.minCount() + "-" + result.maxCount()));
                        } else {
                            tooltip.add(TooltipUtil.withArgs(EIOLang.JEI_FIRE_CRAFTING_DROPS, result.minCount()));
                        }
                    });
                });

        for (var result : recipe.value().results()) {
            output.addItemStack(result.result().copyWithCount(1));
        }

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.CATALYST, 88, 8).setSlotName("catalyst");
        catalyst.addIngredients(Ingredient.of(Items.FLINT_AND_STEEL, EIOFluids.FIRE_WATER.getBucket()));
    }

    @Override
    public void draw(RecipeHolder<FireCraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        if (!Screen.hasShiftDown() && timer.getValue() != changed) {
//            EnderIO.LOGGER.debug("Block {} IDX: {}, ({} - {}) {}", recipe.getId(), blockIdx.get(recipe.getId()), timer.getValue(), changed, blockIdx);
//            blockIdx.put(recipe.getId(), blockIdx.get(recipe.getId()) + 1);
            alternateFire = !alternateFire;
            changed = timer.getValue();
        }

        List<Block> blocks = recipe.value().getAllBaseBlocks();
        Block block = blocks.get(0);

        // Borrowed a bunch of rendering code from Patchouli$PageMultiblock
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(31, 31, 100);
        guiGraphics.pose().scale(20F, 20F, 20F);

        // Initial eye pos somewhere off in the distance in the -Z direction
        Vector4f eye = new Vector4f(0, 0, -100, 1);
        Matrix4f rotMat = new Matrix4f();
        rotMat.identity();

        // For each GL rotation done, track the opposite to keep the eye pos accurate
        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-30F));
        rotMat.rotation(Axis.XP.rotationDegrees(30F));
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-45F));
        rotMat.rotation(Axis.YP.rotationDegrees(45F));

        // Finally apply the rotations
        eye.mul(rotMat);
        eye.div(eye.w);

        // Block Render
        renderBlock(guiGraphics, block);

        guiGraphics.pose().popPose();
    }

    private void renderBlock(GuiGraphics guiGraphics, Block block) {
        guiGraphics.pose().pushPose();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        guiGraphics.pose().translate(0, 0, 0);

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        BlockState state = block.defaultBlockState();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0.5, 0);
        guiGraphics.pose().scale(1f, -1f, 1f);

        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(state, guiGraphics.pose(), buffers, LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY);

        guiGraphics.pose().popPose();
        // TODO: Fire Water has no block. I think this is a registrate bug?
        BlockState fireState = !alternateFire ? Blocks.FIRE.defaultBlockState()
                : EIOFluids.FIRE_WATER.getBlock().defaultBlockState();
//        BlockState fireState = Blocks.FIRE.defaultBlockState();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, -0.5, 0);
        guiGraphics.pose().scale(1f, -1f, 1f);

//        if (alternateFire) {
//            VertexConsumer vertex = buffers.getBuffer(RenderType.cutout());
//            // TODO: Fixy this
//            Minecraft.getInstance().getBlockRenderer().renderLiquid(BlockPos.ZERO, Minecraft.getInstance().level, vertex, fireState, EIOFluids.FIRE_WATER.get().defaultFluidState());
//        } else {
        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(fireState, guiGraphics.pose(), buffers, LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
//        }

        guiGraphics.pose().popPose();

        buffers.endBatch();

        guiGraphics.pose().popPose();
    }
}
