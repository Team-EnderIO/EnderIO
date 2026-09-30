package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.core.common.menu.FilterSlot;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ChemicalFilterSlot extends FilterSlot<BoxedChemicalStack> {

    private static final List<Capability<? extends IChemicalHandler<?, ?>>> CHEMICAL_HANDLERS = List.of(
        CapabilityManager.get(new CapabilityToken<IGasHandler>() {}),
        CapabilityManager.get(new CapabilityToken<ISlurryHandler>() {}),
        CapabilityManager.get(new CapabilityToken<IInfusionHandler>() {}),
        CapabilityManager.get(new CapabilityToken<IPigmentHandler>() {}));

    public ChemicalFilterSlot(Consumer<BoxedChemicalStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    public Optional<BoxedChemicalStack> getResourceFrom(ItemStack itemStack) {
        for (Capability<? extends IChemicalHandler<?, ?>> capability : CHEMICAL_HANDLERS) {
            Optional<? extends IChemicalHandler<?, ?>> handlerOptional = itemStack.getCapability(capability).resolve();
            if (handlerOptional.isPresent()) {
                ChemicalStack<?> chemical = handlerOptional.get().getChemicalInTank(0).copy();
                if (!chemical.isEmpty()) {
                    return Optional.of(BoxedChemicalStack.box(chemical));
                }
            }
        }

        return Optional.empty();
    }
}