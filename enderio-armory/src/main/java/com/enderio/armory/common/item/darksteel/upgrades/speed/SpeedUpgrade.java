package com.enderio.armory.common.item.darksteel.upgrades.speed;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public class SpeedUpgrade extends TieredUpgrade<SpeedUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "speed";

    private final ModConfigSpec.ConfigValue<Integer> energyUse = ArmoryConfig.COMMON.SPEED_ENERGY_USE;

    public SpeedUpgrade() {
        this(SpeedUpgradeTier.ONE);
    }

    public SpeedUpgrade(SpeedUpgradeTier tier) {
        super(tier, NAME);

    }

    public int getEnergyUse() {
        return energyUse.get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_SPEED_DESCRIPTION);
    }

    @Override
    protected SpeedUpgradeTier getBaseTier() {
        return SpeedUpgradeTier.ONE;
    }

    @Override
    protected Optional<SpeedUpgradeTier> getTier(int tier) {
        if (tier >= SpeedUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(SpeedUpgradeTier.values()[tier]);
    }

    public static void applySpeedModifiers(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        if (!stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS)
                || !DarkSteelHelper.hasUpgrade(stack, SpeedUpgrade.NAME)) {
            return;
        }
        if (ItemStackEnergy.getEnergyStored(stack) > 0) {
            Optional<SpeedUpgrade> upgrade = DarkSteelHelper.getUpgradeAs(stack, SpeedUpgrade.NAME, SpeedUpgrade.class);
            upgrade.ifPresent(speedUpgrade -> e.addModifier(Attributes.MOVEMENT_SPEED,
                    speedUpgrade.tier.getAttributeModifier(), EquipmentSlotGroup.LEGS));
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Pre playerTickEvent) {

        Player player = playerTickEvent.getEntity();
        if (!player.level().isClientSide()) {
            // Movement delta only tracked client side
            return;
        }
        if (!player.onGround()) {
            return;
        }
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!legs.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS)) {
            return;
        }
        @Nullable
        IDarkSteelCapability cap = legs.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (cap == null) {
            return;
        }
        Optional<SpeedUpgrade> speedUpgradeOpt = cap.getUpgradeAs(SpeedUpgrade.NAME, SpeedUpgrade.class);
        if (speedUpgradeOpt.isEmpty()) {
            return;
        }
        SpeedUpgrade speedUpgrade = speedUpgradeOpt.get();
        double costPerUnit = speedUpgrade.getEnergyUse();
        double distanceMoved = player.getDeltaMovement().horizontalDistance();
        int extracted = ItemStackEnergy.extractEnergy(legs, (int) (costPerUnit * distanceMoved), false);
        if (extracted > 0) {
            PacketDistributor.sendToServer(new SpeedUsePowerPacket(extracted));
        }
    }

    public static void handleEnergyUsePacket(SpeedUsePowerPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            // Check in case of desync
            ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
            if (!legs.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS)) {
                return;
            }
            ItemStackEnergy.extractEnergy(legs, packet.energyUse(), false);
        });
    }

}
