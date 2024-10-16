package com.enderio.modconduits.mods.mekanism;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ChemicalFilterSlot extends Slot {

    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final Consumer<ChemicalStack> consumer;

    public ChemicalFilterSlot(Consumer<ChemicalStack> consumer, int pSlot, int pX, int pY) {
        super(EMPTY_INVENTORY, pSlot, pX, pY);
        this.consumer = consumer;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack pStack) {
        setChanged();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public ItemStack remove(int pAmount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return getItem().getMaxStackSize();
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        // If this stack is valid, set the inventory slot value.
        IChemicalHandler capability = stack.getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
        if (!stack.isEmpty() && mayPlace(stack) && capability != null) {
            var ghost = capability.getChemicalInTank(0).copy();
            consumer.accept(ghost);
        }

        return stack;
    }
}
