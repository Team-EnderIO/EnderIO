package com.enderio.armory.common.item.darksteel.upgrades.jump;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class JumpUpgrade extends TieredUpgrade<JumpUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "jump";

    private final ModConfigSpec.ConfigValue<Integer> energyUse = ArmoryConfig.COMMON.JUMP_ENERGY_USE;

    public JumpUpgrade() {
        this(JumpUpgradeTier.ONE);
    }

    public JumpUpgrade(JumpUpgradeTier tier) {
        super(tier, NAME);
    }

    public int getNumJumps() {
        return tier.getNumJumps().get();
    }

    public int getEnergyUse() {
        return energyUse.get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(tier.getDescription());
    }

    @Override
    protected JumpUpgradeTier getBaseTier() {
        return JumpUpgradeTier.ONE;
    }

    @Override
    protected Optional<JumpUpgradeTier> getTier(int tier) {
        if (tier >= JumpUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(JumpUpgradeTier.values()[tier]);
    }

    private static int jumpCount = 0;
    private static boolean wasJumping = false;

    public static void doExtraJumps(MovementInputUpdateEvent evt) {
        Player player = evt.getEntity();
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_BOOTS)) {
            return;
        }
        @Nullable
        IDarkSteelCapability cap = boots.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (cap == null) {
            return;
        }
        Optional<JumpUpgrade> jumpUp = cap.getUpgradeAs(JumpUpgrade.NAME, JumpUpgrade.class);
        if (jumpUp.isEmpty()) {
            return;
        }
        // disable when elytra upgrade is enabled or the elytra won't work
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (DarkSteelHelper.hasUpgrade(chest, ElytraUpgrade.NAME)
                && chest.getOrDefault(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, false)) {
            return;
        }

        if (evt.getInput().jumping && jumpCount < jumpUp.get().getNumJumps() && !wasJumping
                && ItemStackEnergy.getEnergyStored(boots) > 0) {
            if (!evt.getEntity().onGround()) {
                ItemStackEnergy.extractEnergy(boots, jumpUp.get().getEnergyUse(), false);
            }
            evt.getEntity().setOnGround(true);
            jumpCount++;
        } else if (evt.getEntity().onGround()) {
            jumpCount = 0;
        }
        // Make sure we only get the first input. Holding space will produce multiple
        // calls with jumping = true
        wasJumping = evt.getInput().jumping;
    }
}
