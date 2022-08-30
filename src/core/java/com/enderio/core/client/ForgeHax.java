package com.enderio.core.client;

import com.enderio.core.EnderCore;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ForgeHooksClient;

import java.lang.reflect.Field;
import java.util.Stack;

// TODO: 1.19: Remove the need for this.
public class ForgeHax {

    private static Field guiLayersField;

    static {
        try {
            guiLayersField = ForgeHooksClient.class.getDeclaredField("guiLayers");
            guiLayersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    public static int getGuiLayer(Screen screen) {
        Stack<Screen> guiLayers = getGuiLayers();
        for (int i = 0; i < guiLayers.size(); i++) {
            if (guiLayers.get(i) == screen) {
                return i + 1;
            }
        }
        return 0;
    }

    private static Stack<Screen> getGuiLayers() {
        try {
            return (Stack<Screen>)guiLayersField.get(null);
        } catch (IllegalAccessException e) {
            EnderCore.LOGGER.warn("Couldn't access guiLayers, report to enderio if you are using the latest Version");
            e.printStackTrace();
        }
        return new Stack<>();
    }
}