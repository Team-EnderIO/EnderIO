package com.enderio.enderio.content.tools.coordinate_selector;

import com.enderio.enderio.api.attachment.CoordinateSelection;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LocationPrintoutItem extends Item {

    public LocationPrintoutItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Optional<CoordinateSelection> optionalSelection = getSelection(context.getItemInHand());
        if (optionalSelection.isPresent() && context.getPlayer() != null && context.getPlayer().isCrouching()) {
            if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                handleRightClick(serverPlayer, optionalSelection.get(), context.getItemInHand());
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);
        Optional<CoordinateSelection> optionalSelection = getSelection(itemInHand);
        if (optionalSelection.isPresent() && player.isCrouching()) {
            if (player instanceof ServerPlayer serverPlayer) {
                CoordinateSelection selection = optionalSelection.get();
                handleRightClick(serverPlayer, selection, itemInHand);
            }
            return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
        }
        return super.use(level, player, usedHand);
    }

    private static void handleRightClick(ServerPlayer serverPlayer, @Nullable CoordinateSelection selection, ItemStack printout) {
        if (selection != null) {
            openMenu(serverPlayer, selection, printout.getHoverName().getString());
        }
    }

    private static void openMenu(ServerPlayer player, CoordinateSelection selection, String name) {

        NetworkHooks.openScreen(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new CoordinateMenu(containerId, selection, name);
            }
        }, buf -> CoordinateMenu.writeAdditionalData(buf, selection, name));
    }

    public static Optional<CoordinateSelection> getSelection(ItemStack stack) {
        return Optional.ofNullable(EIODataComponents.COORDINATE_SELECTION.get(stack));
    }

    public static void setSelection(ItemStack stack, CoordinateSelection selection) {
        EIODataComponents.COORDINATE_SELECTION.set(stack, selection);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        getSelection(stack).ifPresent(selection -> {
                tooltipComponents.add(writeCoordinate('x', selection.pos().getX())
                    .append(writeCoordinate('y', selection.pos().getY()))
                    .append(writeCoordinate('z', selection.pos().getZ())));
                tooltipComponents.add(Component.literal(selection.getLevelName()));
        });
    }

    private static MutableComponent writeCoordinate(char character, int number) {
        return Component.literal("" + character).withStyle(ChatFormatting.GRAY).append(Component.literal("" + number).withStyle(ChatFormatting.GREEN));
    }
}
