package com.enderio.conduits.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.DoubleChannelPacket;
import com.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RedstoneDoubleChannelFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneInsertFilter filter;
    private final DoubleRedstoneChannel channels;

    protected RedstoneDoubleChannelFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;

        // TODO: Gross workaround, I think these menus should be rewritten sometime.
        Object resourceFilter = pStack.getCapability(ConduitCapabilities.REDSTONE_INSERT_FILTER);
        if (resourceFilter == null) {
            resourceFilter = pStack.getCapability(ConduitCapabilities.REDSTONE_EXTRACT_FILTER);
        }
        if (!(resourceFilter instanceof RedstoneInsertFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
        if (!(resourceFilter instanceof DoubleRedstoneChannel channels)) {
            throw new IllegalArgumentException();
        }
        this.channels = channels;
        addInventorySlots(14,119, inventory);
    }

    protected RedstoneDoubleChannelFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER.get(), pContainerId, inventory, pStack);
    }


    public static RedstoneDoubleChannelFilterMenu factory(int i, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new RedstoneDoubleChannelFilterMenu(i, inventory, inventory.player.getMainHandItem());
    }

    public RedstoneInsertFilter getFilter() {
        return filter;
    }

    public DoubleRedstoneChannel getChannels() {
        return channels;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getItemInHand(InteractionHand.MAIN_HAND).equals(stack);
    }

    public void setFirstChannel(DyeColor colorControl) {
        channels.setFirstChannel(colorControl);
        PacketDistributor.sendToServer(new DoubleChannelPacket(channels.getFirstChannel(), channels.getSecondChannel()));
    }

    public void setSecondChannel(DyeColor colorControl) {
        channels.setSecondChannel(colorControl);
        PacketDistributor.sendToServer(new DoubleChannelPacket(channels.getFirstChannel(), channels.getSecondChannel()));
    }

    public void addInventorySlots(int xPos, int yPos, Inventory inventory) {

        // Hotbar
        for (int x = 0; x < 9; x++) {
            Slot ref = new Slot(inventory, x, xPos + x * 18, yPos + 58);
            this.addSlot(ref);
        }

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot ref = new Slot(inventory, x + y * 9 + 9, xPos + x * 18, yPos + y * 18);
                this.addSlot(ref);
            }
        }

    }
}
