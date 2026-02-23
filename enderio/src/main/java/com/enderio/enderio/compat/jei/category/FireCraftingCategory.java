package com.enderio.enderio.compat.jei.category;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.compat.jei.JEILang;
import com.enderio.enderio.compat.jei.JEIUtils;
import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe;
import com.enderio.enderio.init.EIOFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TagnumElite
 */
public class FireCraftingCategory extends AbstractRecipeCategory<RecipeHolder<FireCraftingRecipe>> {

    public static final IRecipeType<RecipeHolder<FireCraftingRecipe>> TYPE = JEIUtils.createRecipeType(EnderIO.MOD_ID,
            "fire_crafting", FireCraftingRecipe.class);

    private static final Identifier BG_LOCATION = EnderIO.id("textures/gui/jei_infinity.png");

    private final ITickTimer timer;
    private final Map<Identifier, Integer> blockIdx = new HashMap<>();
    private int changed = 0;
    private boolean alternateFire = false;

    public FireCraftingCategory(IGuiHelper guiHelper) {
        super(TYPE, JEILang.FIRE_CRAFTING_TITLE, guiHelper.createDrawable(BG_LOCATION, 109, 0, 16, 16), 109, 62);
        this.timer = guiHelper.createTickTimer(40, 1, false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltipBuilder, RecipeHolder<FireCraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // Middle Right, above the tooltip icon
        if (mouseX >= 87 && mouseX <= 105 && mouseY >= 25 && mouseY <= 38) {
            List<ResourceKey<Level>> validDimensions = recipe.value().dimensions();
            List<Component> tooltip = new ArrayList<>(validDimensions.size() + 1);
            tooltip.add(JEILang.FIRE_CRAFTING_VALID_DIMENSIONS);

            for (ResourceKey<Level> dim : validDimensions) {
                tooltip.add(Component.literal("- " + dim.identifier()));
            }

            tooltipBuilder.addAll(tooltip);
            return;
        }
        // Block tool tip
        if (mouseX >= 17 && mouseX <= 47 && mouseY >= 31 && mouseY <= 57) {
            List<Block> bases = recipe.value().getAllBaseBlocks();
            List<Component> tooltip = new ArrayList<>(bases.size() + 1);
            tooltip.add(JEILang.FIRE_CRAFTING_VALID_BLOCKS);

            for (Block block : bases) {
                tooltip.add(Component.literal("- ").append(block.getName()));
            }

            tooltipBuilder.addAll(tooltip);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FireCraftingRecipe> recipe, IFocusGroup focuses) {
        IIngredientAcceptor<?> block = builder.addInvisibleIngredients(RecipeIngredientRole.CRAFTING_STATION);
        block.add(Ingredient.of(recipe.value().getAllBaseBlocks().toArray(Block[]::new)));

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 39)
                .addRichTooltipCallback((slotView, tooltip) -> {
                    slotView.getDisplayedItemStack().ifPresent(stack -> {
                        // Gross way of getting the index.
                        int index = slotView.getItemStacks().toList().indexOf(stack);
                        var result = recipe.value().results().get(index);

                        tooltip.add(TooltipUtil.withArgs(JEILang.FIRE_CRAFTING_CHANCE,
                                Math.round(result.chance() * 100)));

                        if (result.minCount() != result.maxCount()) {
                            tooltip.add(TooltipUtil.withArgs(JEILang.FIRE_CRAFTING_DROPS,
                                    result.minCount() + "-" + result.maxCount()));
                        } else {
                            tooltip.add(TooltipUtil.withArgs(JEILang.FIRE_CRAFTING_DROPS, result.minCount()));
                        }
                    });
                });

        for (var result : recipe.value().results()) {
            output.add(result.result().create().copyWithCount(1));
        }

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 88, 8).setSlotName("catalyst");
        catalyst.add(Ingredient.of(Items.FLINT_AND_STEEL, EIOFluids.FIRE_WATER.bucket()));
    }

    @Override
    public void draw(RecipeHolder<FireCraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        if (timer.getValue() != changed) { //!Screen.hasShiftDown() &&
//            EnderIO.LOGGER.debug("Block {} IDX: {}, ({} - {}) {}", recipe.getId(), blockIdx.get(recipe.getId()), timer.getValue(), changed, blockIdx);
//            blockIdx.put(recipe.getId(), blockIdx.get(recipe.getId()) + 1);
            alternateFire = !alternateFire;
            changed = timer.getValue();
        }

        List<Block> blocks = recipe.value().getAllBaseBlocks();
        Block block = blocks.get(0);

        // Borrowed a bunch of rendering code from Patchouli$PageMultiblock

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(31, 31);
        guiGraphics.pose().scale(20F, 20F);

        // Initial eye pos somewhere off in the distance in the -Z direction
        Vector4f eye = new Vector4f(0, 0, -100, 1);
        Matrix4f rotMat = new Matrix4f();
        rotMat.identity();

        // For each GL rotation done, track the opposite to keep the eye pos accurate
//        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-30F));
//        rotMat.rotation(Axis.XP.rotationDegrees(30F));
//        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-45F));
//        rotMat.rotation(Axis.YP.rotationDegrees(45F));

        // Finally apply the rotations
        eye.mul(rotMat);
        eye.div(eye.w);

        // Block Render
        renderBlock(guiGraphics, block);

        guiGraphics.pose().popMatrix();
    }

    private void renderBlock(GuiGraphics guiGraphics, Block block) {
        guiGraphics.pose().pushMatrix();

        guiGraphics.pose().translate(0, 0);

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        BlockState state = block.defaultBlockState();
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0f, 0.5f);
        guiGraphics.pose().scale(1f, -1f);

        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(state, new PoseStack(), buffers, LightCoordsUtil.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY);

        guiGraphics.pose().popMatrix();
        // TODO: Fire Water has no block. I think this is a registrate bug?
        BlockState fireState = !alternateFire ? Blocks.FIRE.defaultBlockState()
                : EIOFluids.FIRE_WATER.block().get().defaultBlockState();
//        BlockState fireState = Blocks.FIRE.defaultBlockState();
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0f, -0.5f);
        guiGraphics.pose().scale(1f, -1f);

//        if (alternateFire) {
//            VertexConsumer vertex = buffers.getBuffer(RenderType.cutout());
//            // TODO: Fixy this
//            Minecraft.getInstance().getBlockRenderer().renderLiquid(BlockPos.ZERO, Minecraft.getInstance().level, vertex, fireState, EIOFluids.FIRE_WATER.get().defaultFluidState());
//        } else {
        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(fireState, new PoseStack(), buffers, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
//        }

        guiGraphics.pose().popMatrix();

        buffers.endBatch();

        guiGraphics.pose().popMatrix();
    }
}
