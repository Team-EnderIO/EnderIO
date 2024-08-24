package com.enderio.base.common.menu;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.network.FilterUpdatePacket;
import com.enderio.core.common.capability.EntityFilterCapability;
import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class EntityFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final EntityFilterCapability capability;

    public EntityFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;

        LazyOptional<ResourceFilter> resourceFilter = stack.getCapability(EIOCapabilities.FILTER);

        capability = resourceFilter.map(filter -> {
            if (filter instanceof EntityFilterCapability cap) {
                return cap;
            }
            throw new IllegalArgumentException();
        }).orElseThrow(IllegalArgumentException::new);

        List<StoredEntityData> items = capability.getEntries();
        for (int i = 0; i < items.size(); i++) {
            int pSlot = i;
            addSlot(new EntityFilterSlot(data -> capability.setEntry(pSlot, data) ,i ,14 + ( i % 5) * 18, 35 + 20 * ( i / 5)));
        }
        addInventorySlots(14,119, inventory);
    }

    public EntityFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(EIOMenus.ENTITY_FILTER.get(), pContainerId, inventory, stack);
    }

    public static EntityFilterMenu factory(@Nullable MenuType<EntityFilterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new EntityFilterMenu(EIOMenus.ENTITY_FILTER.get(), pContainerId, inventory, inventory.player.getMainHandItem());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getMainHandItem().equals(stack);
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

    public EntityFilterCapability getFilter() {
        return capability;
    }

    public void setNbt(Boolean nbt) {
        CoreNetwork.sendToServer(new FilterUpdatePacket(nbt, capability.isInvert()));
        capability.setNbt(nbt);
    }

    public void setInverted(Boolean inverted) {
        CoreNetwork.sendToServer(new FilterUpdatePacket(capability.isNbt(), inverted));
        capability.setInverted(inverted);
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pSlotId < capability.getEntries().size() && pSlotId >= 0) {
            if (capability.getEntries().get(pSlotId).getEntityType().isPresent()) {
                capability.setEntry(pSlotId, StoredEntityData.empty());
            }
        }
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }
}
