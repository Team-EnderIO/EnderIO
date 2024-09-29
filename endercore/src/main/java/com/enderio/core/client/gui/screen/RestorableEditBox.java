package com.enderio.core.client.gui.screen;

import net.minecraft.client.gui.components.EditBox;

/**
 * Simple wrapper for an {@link EditBox} that is compatible wit {@link EnderContainerScreen}'s state peristence system.
 * @param editBox The edit box to be persisted.
 */
public record RestorableEditBox(EditBox editBox) implements StateRestoringWidget {
    @Override
    public Object getValueForRestore() {
        return editBox.getValue();
    }

    @Override
    public void restoreValue(Object value) {
        if (value instanceof String stringValue) {
            editBox.setValue(stringValue);
        }
    }
}
