package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.armory.client.renderer.GliderIntegrationClient;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.api.glider.GliderMovementInfo;
import com.enderio.base.api.integration.ClientIntegration;
import com.enderio.base.api.integration.Integration;
import com.enderio.base.common.lang.EIOLang;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GliderIntegration implements Integration {

    private final GliderMovementInfo info = new GliderMovementInfo(0.003d, 1d, -0.05d, this);

    public static final GliderIntegration INSTANCE = new GliderIntegration();

    @Override
    public Optional<Component> hangGliderDisabledReason(Player player) {
        return player.isFallFlying() ? Optional.of(EIOLang.GLIDER_DISABLED_FALL_FLYING) : Optional.empty();
    }

    @Override
    public Optional<GliderMovementInfo> getGliderMovementInfo(Player player) {
        ItemStack equipped = player.getItemBySlot(EquipmentSlot.CHEST);
        if (equipped.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE)
                && DarkSteelHelper.hasUpgrade(equipped, GliderUpgrade.NAME)
                && equipped.getOrDefault(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, false)) {
            return Optional.of(info);
        }
        return Optional.empty();
    }

    @Override
    public ClientIntegration getClientIntegration() {
        return GliderIntegrationClient.INSTANCE;
    }

}
