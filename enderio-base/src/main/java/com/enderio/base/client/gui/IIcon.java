package com.enderio.base.client.gui;

import com.enderio.api.UseOnly;
import com.enderio.base.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

public interface IIcon {
    Vector2i DEFAULT_TEXTURE_SIZE = new Vector2i(256, 256);

    /**
     * @return The texture that needs to be bound to the texturemanager to be rendered
     */
    @UseOnly(LogicalSide.CLIENT)
    ResourceLocation getTextureLocation();

    /**
     * @return the size of the area on the texture you want to render
     */
    @UseOnly(LogicalSide.CLIENT)
    Vector2i getIconSize();

    /**
     * @return the size you want to render the texturearea at
     */
    @UseOnly(LogicalSide.CLIENT)
    default Vector2i getRenderSize() {
        return getIconSize();
    }

    /**
     * @return the position your icon is on the texture
     */
    @UseOnly(LogicalSide.CLIENT)
    Vector2i getTexturePosition();

    /**
     * @return a Component that is rendered on hover, if this icon is rendered on a gui
     */
    @UseOnly(LogicalSide.CLIENT)
    default Component getTooltip() {
        return TextComponent.EMPTY;
    }

    /**
     * @return the texture size
     */
    @UseOnly(LogicalSide.CLIENT)
    default Vector2i getTextureSize() {
        return DEFAULT_TEXTURE_SIZE;
    }

    /**
     * @return if this icon should render
     */
    @UseOnly(LogicalSide.CLIENT)
    default boolean shouldRender() {
        return true;
    }
}
