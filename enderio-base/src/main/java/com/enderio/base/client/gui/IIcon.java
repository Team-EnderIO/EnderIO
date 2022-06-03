package com.enderio.base.client.gui;

import com.enderio.base.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public interface IIcon {
    Vector2i DEFAULT_TEXTURE_SIZE = new Vector2i(256, 256);

    /**
     * @return The texture that needs to be bound to the texturemanager to be rendered
     */
    ResourceLocation getTextureLocation();

    /**
     * @return the size of the area on the texture you want to render
     */
    Vector2i getIconSize();

    /**
     * @return the size you want to render the texturearea at
     */
    default Vector2i getRenderSize() {
        return getIconSize();
    }

    /**
     * @return the position your icon is on the texture
     */
    Vector2i getTexturePosition();

    /**
     * @return a Component that is rendered on hover, if this icon is rendered on a gui
     */
    default Component getTooltip() {
        return TextComponent.EMPTY;
    }

    /**
     * @return the texture size
     */
    default Vector2i getTextureSize() {
        return DEFAULT_TEXTURE_SIZE;
    }

    /**
     * @return if this icon should render
     */
    default boolean shouldRender() {
        return false;
    }
}
