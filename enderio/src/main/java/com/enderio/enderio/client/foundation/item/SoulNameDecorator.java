package com.enderio.enderio.client.foundation.item;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.client.IItemDecorator;

import java.util.ArrayList;

/** Identifies the stored mob with a Shift-held marquee or a small spawn egg. */
public class SoulNameDecorator implements IItemDecorator {
    public static final SoulNameDecorator INSTANCE = new SoulNameDecorator();

    private static final int MOB_NAME_COLOR = 0xFF0030B0;
    private static final int WINDOW_LENGTH = 3;
    private static final long SCROLL_INTERVAL_MS = 200;

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
        var soul = stack.get(EIODataComponents.SOUL);
        if (soul == null || soul.entityType() == null) {
            return false;
        }

        if (!InputConstants.isKeyDown(InputConstants.KEY_LSHIFT) && !InputConstants.isKeyDown(InputConstants.KEY_RSHIFT)) {
            return false;
        }

        // Display the spawn egg if we're using the modern overlay.
        if (!BaseConfig.CLIENT.NOSTALGIC_SOUL_NAME_OVERLAY.get()) {
            var spawnEggOpt = SpawnEggItem.byId(soul.entityType());
            if (spawnEggOpt.isPresent()) {
                graphics.pose().pushMatrix();
                graphics.pose().translate(x, y + 8);
                graphics.pose().scale(0.5f, 0.5f);
                graphics.fakeItem(new ItemStack(spawnEggOpt.get()), 0, 0);
                graphics.pose().popMatrix();

                return false;
            }

            // If people *really* dislike the scrolling text, allow them to disable it as a fallback option.
            if (!BaseConfig.CLIENT.SOUL_NAME_OVERLAY_FALLBACK.get()) {
                return false;
            }
        }

        String name = soul.entityType().getDescription().getString();
        if (name.isEmpty()) {
            return false;
        }

        // Shape and order the full name before selecting a window, preserving RTL runs and joined letters.
        var characters = new ArrayList<FormattedCharSequence>();
        Component.literal(name).getVisualOrderText().accept((_, style, codePoint) -> {
            characters.add(FormattedCharSequence.codepoint(codePoint, style));
            return true;
        });

        characters.add(FormattedCharSequence.codepoint(' ', Style.EMPTY));

        // Step through the characters to display up to three at a time.
        int step = (int) ((Util.getMillis() / SCROLL_INTERVAL_MS) % characters.size());
        int start = font.isBidirectional() ? characters.size() - 1 - WINDOW_LENGTH - step : step;
        var window = new ArrayList<FormattedCharSequence>(WINDOW_LENGTH);
        for (int i = 0; i < WINDOW_LENGTH; i++) {
            window.add(characters.get(Math.floorMod(start + i, characters.size())));
        }

        var text = FormattedCharSequence.composite(window);
        graphics.text(font, text, x + 8 - font.width(text) / 2, y + 5, MOB_NAME_COLOR, true);
        return false;
    }
}
