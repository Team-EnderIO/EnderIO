package com.enderio.base.common.item.misc;

import com.enderio.api.capability.CoordinateSelection;
import com.enderio.api.capability.ICoordinateSelectionHolder;
import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.CoordinateSelectionHolder;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.menu.CoordinateMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class LocationPrintoutItem extends Item implements IMultiCapabilityItem {

    public LocationPrintoutItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Optional<CoordinateSelection> optionalSelection = getSelection(pContext.getItemInHand());
        if (optionalSelection.isPresent() && pContext.getPlayer() != null && pContext.getPlayer().isCrouching()) {
            if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
                handleRightClick(serverPlayer, optionalSelection.get(), pContext.getItemInHand());
            }
            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        Optional<CoordinateSelection> optionalSelection = getSelection(itemInHand);
        if (optionalSelection.isPresent() && pPlayer.isCrouching()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                CoordinateSelection selection = optionalSelection.get();
                handleRightClick(serverPlayer, selection, itemInHand);
            }
            return InteractionResultHolder.sidedSuccess(itemInHand, pLevel.isClientSide);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static void handleRightClick(ServerPlayer serverPlayer, @Nullable CoordinateSelection selection, ItemStack printout) {
        if (selection != null)
            openMenu(serverPlayer, selection, printout.getHoverName().getString());
    }

    private static void openMenu(ServerPlayer player, CoordinateSelection selection, String name) {

        NetworkHooks.openScreen(player,new MenuProvider() {
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
        return stack.getCapability(EIOCapabilities.COORDINATE_SELECTION_HOLDER)
            .filter(ICoordinateSelectionHolder::hasSelection)
            .map(ICoordinateSelectionHolder::getSelection);
    }

    public static void setSelection(ItemStack stack, CoordinateSelection selection) {
        stack.getCapability(EIOCapabilities.COORDINATE_SELECTION_HOLDER).ifPresent(selectionHolder -> selectionHolder.setSelection(selection));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> toolTip, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, toolTip, pIsAdvanced);
        getSelection(pStack).ifPresent(selection -> {
                toolTip.add(writeCoordinate('x', selection.pos().getX())
                    .append(writeCoordinate('y', selection.pos().getY()))
                    .append(writeCoordinate('z', selection.pos().getZ())));
                toolTip.add(Component.literal(selection.getLevelName()));
        });
    }

    private static MutableComponent writeCoordinate(char character, int number) {
        return Component.literal("" + character).withStyle(ChatFormatting.GRAY).append(Component.literal("" + number).withStyle(ChatFormatting.GREEN));
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.COORDINATE_SELECTION_HOLDER, LazyOptional.of(CoordinateSelectionHolder::new));
        return provider;
    }
}
