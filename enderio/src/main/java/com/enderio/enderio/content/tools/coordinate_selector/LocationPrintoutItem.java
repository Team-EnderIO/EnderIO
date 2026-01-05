package com.enderio.enderio.content.tools.coordinate_selector;

import com.enderio.enderio.api.attachment.CoordinateSelection;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class LocationPrintoutItem extends Item {

    public LocationPrintoutItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Optional<CoordinateSelection> optionalSelection = getSelection(pContext.getItemInHand());
        if (optionalSelection.isPresent() && pContext.getPlayer() != null && pContext.getPlayer().isCrouching()) {
            if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
                handleRightClick(serverPlayer, optionalSelection.get(), pContext.getItemInHand());
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        Optional<CoordinateSelection> optionalSelection = getSelection(itemInHand);
        if (optionalSelection.isPresent() && pPlayer.isCrouching()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                CoordinateSelection selection = optionalSelection.get();
                handleRightClick(serverPlayer, selection, itemInHand);
            }

            return InteractionResult.SUCCESS;
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static void handleRightClick(ServerPlayer serverPlayer, @Nullable CoordinateSelection selection, ItemStack printout) {
        if (selection != null) {
            openMenu(serverPlayer, selection, printout.getHoverName().getString());
        }
    }

    private static void openMenu(ServerPlayer player, CoordinateSelection selection, String name) {

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return new CoordinateMenu(pContainerId, selection, name);
            }
        }, buf -> CoordinateMenu.writeAdditionalData(buf, selection, name));
    }

    public static Optional<CoordinateSelection> getSelection(ItemStack stack) {
        return Optional.ofNullable(stack.get(EIODataComponents.COORDINATE_SELECTION));
    }

    public static void setSelection(ItemStack stack, CoordinateSelection selection) {
        stack.set(EIODataComponents.COORDINATE_SELECTION, selection);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        getSelection(stack).ifPresent(selection -> {
            tooltipAdder.accept(writeCoordinate('x', selection.pos().getX())
                    .append(writeCoordinate('y', selection.pos().getY()))
                    .append(writeCoordinate('z', selection.pos().getZ())));
            tooltipAdder.accept(Component.literal(selection.getLevelName()));
        });
    }

    private static MutableComponent writeCoordinate(char character, int number) {
        return Component.literal("" + character).withStyle(ChatFormatting.GRAY).append(Component.literal("" + number).withStyle(ChatFormatting.GREEN));
    }
}
