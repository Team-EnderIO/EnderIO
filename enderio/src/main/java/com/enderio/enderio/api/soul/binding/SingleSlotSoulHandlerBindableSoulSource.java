package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulResource;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class SingleSlotSoulHandlerBindableSoulSource implements BindableSoulSource {

    private final ItemAccess itemAccess;

    public SingleSlotSoulHandlerBindableSoulSource(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public boolean isSoulAvailable() {
        var soulHandler = itemAccess.getCapability(EnderIOCapabilities.Soul.ITEM);

        // Ensure we have a soul handler and it has a single slot.
        if (soulHandler == null || soulHandler.size() != 1) {
            return false;
        }

        return !soulHandler.getResource(0).isEmpty();
    }

    @Override
    public Soul availableSoul() {
        var soulHandler = itemAccess.getCapability(EnderIOCapabilities.Soul.ITEM);

        // Ensure we have a soul handler and it has a single slot.
        if (soulHandler == null || soulHandler.size() != 1) {
            return Soul.EMPTY;
        }

        SoulResource soulToExtract = soulHandler.getResource(0);
        return soulToExtract.toSoul();
    }

    @Override
    public Soul extractSoul(TransactionContext transaction) {
        var soulHandler = itemAccess.getCapability(EnderIOCapabilities.Soul.ITEM);

        // Ensure we have a soul handler and it has a single slot.
        if (soulHandler == null || soulHandler.size() != 1) {
            return Soul.EMPTY;
        }

        SoulResource soulToExtract = soulHandler.getResource(0);
        if (soulHandler.extract(0, soulToExtract, 1, transaction) == 1) {
            return soulToExtract.toSoul();
        }

        return Soul.EMPTY;
    }
}
