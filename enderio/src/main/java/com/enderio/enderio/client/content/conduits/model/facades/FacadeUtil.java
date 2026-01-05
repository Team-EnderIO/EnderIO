package com.enderio.enderio.client.content.conduits.model.facades;

import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class FacadeUtil {
    public static boolean areFacadesVisible(@Nullable Player player) {
        if (player == null) {
            return true;
        }

        return areFacadesVisible(player.getMainHandItem()) && areFacadesVisible(player.getOffhandItem());
    }

    public static boolean areFacadesVisible(ItemStack itemStack) {
        return !itemStack.is(EIOTags.Items.HIDE_FACADES);
    }
}
