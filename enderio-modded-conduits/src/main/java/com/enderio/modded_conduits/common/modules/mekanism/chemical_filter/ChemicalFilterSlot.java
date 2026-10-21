package com.enderio.modded_conduits.common.modules.mekanism.chemical_filter;

import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChemicalFilterSlot extends FilterSlot<ChemicalStack> {

    public ChemicalFilterSlot(Supplier<ChemicalStack> getter, Consumer<ChemicalStack> setter, int slot, int x, int y) {
        super(getter, setter, slot, x, y);
    }

    @Override
    protected ChemicalStack emptyResource() {
        return ChemicalStack.EMPTY;
    }

    @Override
    public Optional<ChemicalStack> getResourceFrom(ItemStack itemStack) {
        ResourceHandler<ChemicalResource> capability = ItemAccess.forStack(itemStack).getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
        if (capability != null) {
            for (int i = 0; i < capability.size(); i++) {
                var ghost = capability.getResource(i);
                if (!ghost.isEmpty()) {
                    return Optional.of(ghost.toStack(1));
                }
            }
        }

        return Optional.empty();
    }
}
