package com.enderio.modconduits.mods.mekanism;

import com.enderio.base.common.menu.FilterSlot;
import java.util.Optional;
import java.util.function.Consumer;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;

public class ChemicalFilterSlot extends FilterSlot<ChemicalStack> {

    public ChemicalFilterSlot(Consumer<ChemicalStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    public Optional<ChemicalStack> getResourceFrom(ItemStack itemStack) {
        IChemicalHandler capability = itemStack.getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
        if (capability != null) {
            var ghost = capability.getChemicalInTank(0).copy();
            if (!ghost.isEmpty()) {
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
