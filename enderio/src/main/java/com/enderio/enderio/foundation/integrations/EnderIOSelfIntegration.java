package com.enderio.enderio.foundation.integrations;

import com.enderio.enderio.api.glider.GliderMovementInfo;
import com.enderio.enderio.api.integration.ClientIntegration;
import com.enderio.enderio.api.integration.Integration;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class EnderIOSelfIntegration implements Integration {

    private final GliderMovementInfo info = new GliderMovementInfo(0.01d, 1d, -0.05d, this);

    public static final EnderIOSelfIntegration INSTANCE = new EnderIOSelfIntegration();
    @Override
    public Optional<Component> hangGliderDisabledReason(Player player) {
        return player.isFallFlying() ? Optional.of(ToolsLang.GLIDER_DISABLED_FALL_FLYING) : Optional.empty();
    }

    @Override
    public Optional<GliderMovementInfo> getGliderMovementInfo(Player player) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(EIOTags.Items.GLIDER)) {
            return Optional.of(info);
        }
        return Optional.empty();
    }
    @Override
    public ClientIntegration getClientIntegration() {
        return EnderIOSelfClientIntegration.INSTANCE;
    }

    public Optional<Item> getActiveGliderItem(Player player) {
        return Optional.of(player.getItemBySlot(EquipmentSlot.CHEST).getItem());
    }
}
