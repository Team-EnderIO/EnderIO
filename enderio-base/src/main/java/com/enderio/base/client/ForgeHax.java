package com.enderio.base.client;

import com.enderio.base.EnderIO;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ForgeHooksClient;

import java.lang.reflect.Field;
import java.util.Stack;

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

    public static void setItemRendererDepthForScreen(Screen screen, PoseStack poseStack) {
        poseStack.translate(0, 0, -2000 * ForgeHax.getGuiLayer(screen));
    }

    private static int getGuiLayer(Screen screen) {
        try {
            Stack<Screen> guiLayers = (Stack<Screen>)guiLayersField.get(null);
            for (int i = 0; i < guiLayers.size(); i++) {
                if (guiLayers.get(i) == screen) {
                    return guiLayers.size() - i - 1;
                }
            }
            return guiLayers.size();
        } catch (IllegalAccessException e) {
            EnderIO.LOGGER.warning("Couldn't access guiLayers, report to enderio if you are using the latest Version");
            e.printStackTrace();
        }
        return 0;
    }
}