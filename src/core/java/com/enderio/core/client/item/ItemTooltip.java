package com.enderio.core.client.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;


import java.util.List;

public class ItemTooltip {

    public static void addShiftKeyMessage(List<Component> source){
        //TODO: add translation
        if(!Screen.hasShiftDown()){
            source.add(Component.literal("<Hold shift>"));
        }

    }

    public static void addFluidTankMessage(List<Component> source, FluidStack stack, int capacity){
        //TODO: add translation
        if(Screen.hasShiftDown()){
            if(stack.isEmpty()) {
                source.add(Component.literal("Tank is empty"));
            }else{
                source.add(Component.literal(stack.getAmount() + " / " + capacity + " mb of " + stack.getFluid().getFluidType().getDescription().getString()));
            }
        }
    }
}
