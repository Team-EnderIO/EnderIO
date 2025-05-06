package com.enderio.armory.common.item.darksteel.upgrades.nightvision;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NightVisionHandler {

    public static final NightVisionHandler INST = new NightVisionHandler();

    private final Set<UUID> appliedToPlayers = new HashSet<>();

    public void updateEffect(PlayerTickEvent.Pre evt) {
        Player player = evt.getEntity();
        Optional<ItemStack> helOpt = getEquippedHelmet(player);
        if (helOpt.isEmpty()) {
            removeEffect(player);
            return;
        }
        ItemStack helmet = helOpt.get();

        if (!helmet.getOrDefault(ArmoryDataComponents.DARK_STEEL_NIGHT_VISION_ACTIVE, false)) {
            removeEffect(player);
            return;
        }

        int extracted = ItemStackEnergy.extractEnergy(helmet, ArmoryConfig.COMMON.NIGHT_VISION_ENERGY_USE.get(), false);
        if (extracted <= 0) {
            removeEffect(player);
            return;
        }
        MobEffectInstance instance = new MobEffectInstance(MobEffects.NIGHT_VISION, 500, 0, false, false, false);
        player.addEffect(instance);
        appliedToPlayers.add(player.getUUID());
    }

    public void handleActivePacket(NightVisionEnabledPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            Optional<ItemStack> eq = getEquippedHelmet(player);
            if (eq.isEmpty()) {
                return;
            }
            eq.get().set(ArmoryDataComponents.DARK_STEEL_NIGHT_VISION_ACTIVE, packet.enabled());
        });
    }

    private void removeEffect(Player player) {
        if (!appliedToPlayers.contains(player.getUUID())) {
            return;
        }
        player.removeEffect(MobEffects.NIGHT_VISION);
        appliedToPlayers.remove(player.getUUID());
    }

    public static Optional<ItemStack> getEquippedHelmet(Player player) {
        ItemStack equipped = player.getItemBySlot(EquipmentSlot.HEAD);
        if (equipped.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_HELMET)
                && (DarkSteelHelper.hasUpgrade(equipped, NightVisisionUpgrade.NAME))) {
            return Optional.of(equipped);
        }
        return Optional.empty();
    }

}
