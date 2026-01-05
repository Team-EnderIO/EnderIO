package com.enderio.enderio.content.tools.coordinate_selector;

import com.enderio.enderio.api.attachment.CoordinateSelection;
import com.enderio.enderio.content.tools.ToolsLang;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CoordinateSelectorItem extends Item {

    public CoordinateSelectorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!checkPaper(player)) {
            return super.use(level, player, hand);
        }

        BlockHitResult hitResult = level.clip(
            new ClipContext(
                player.getEyePosition(),
                player.getLookAngle().scale(64).add(player.getEyePosition()), //Make range configurable(?)
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                player)
        );

        if (hitResult.getType() == HitResult.Type.MISS) {
            if (player instanceof LocalPlayer) {
                player.displayClientMessage(ToolsLang.COORDINATE_SELECTOR_NO_BLOCK, true);
            }

            return super.use(level, player, hand);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, level, hitResult.getBlockPos());
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && checkPaper(context.getPlayer())) {
            if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                openMenu(serverPlayer, context.getLevel(), context.getClickedPos());
            }

            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }

        return super.useOn(context);
    }

    private static void openMenu(ServerPlayer player, Level level, BlockPos pos) {
        CoordinateSelection selection = new CoordinateSelection(level, pos);

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new CoordinateMenu(containerId, selection, null);
            }
        }, buf -> CoordinateMenu.writeAdditionalData(buf, selection, ""));
    }

    private static boolean checkPaper(Player player) {
        if (player.getInventory().contains(Items.PAPER.getDefaultInstance())) {
            return true;
        }

        if (player instanceof LocalPlayer) {
            player.displayClientMessage(ToolsLang.COORDINATE_SELECTOR_NO_PAPER, true);
        }

        return false;
    }
}
