package com.enderio.armory.common.item.darksteel.upgrades.travel;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.init.EIODataComponents;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public class TravelUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "travel";

    public TravelUpgrade() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_TRAVEL;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_TRAVEL_DESCRIPTION);
    }

    public static void handleTravelEnabledPacket(TravelEndabledUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            // Just check there is no desync
            ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!DarkSteelHelper.hasUpgrade(equipped, TravelUpgrade.NAME)) {
                return;
            }
            equipped.set(EIODataComponents.TRAVEL_ITEM, packet.enabled());
        });
    }

    public static @Nullable InteractionResultHolder<ItemStack> onUse(Level level, Player player,
            InteractionHand usedHand, Item item) {
        ItemStack stack = player.getItemInHand(usedHand);
        @Nullable
        Boolean isTravelActive = stack.get(EIODataComponents.TRAVEL_ITEM);
        if (isTravelActive != null && isTravelActive) {
            if (TravelUpgrade.tryPerformAction(item, level, player, stack)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            return InteractionResultHolder.fail(stack);
        } else {
            return null;
        }
    }

    public static @Nullable InteractionResult onUse(UseOnContext context, Item item) {
        ItemStack stack = context.getItemInHand();
        @Nullable
        Boolean isTravelActive = stack.get(EIODataComponents.TRAVEL_ITEM);
        if (isTravelActive != null && isTravelActive) {
            if (context.getPlayer() != null && TravelUpgrade.tryPerformAction(item, context.getLevel(),
                    context.getPlayer(), context.getItemInHand())) {
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
            return InteractionResult.FAIL;
        }
        return null;
    }

    private static boolean tryPerformAction(Item item, Level level, Player player, ItemStack stack) {
        boolean isCreative = player.isCreative();
        if (TravelHandler.hasResources(stack) || isCreative) {
            if (performAction(item, level, player)) {
                if (!level.isClientSide() && !isCreative) {
                    TravelHandler.consumeResources(stack);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean performAction(Item item, Level level, Player player) {
        Optional<TravelTarget> target = TravelHandler.getTeleportAnchorTarget(player);
        if (target.isEmpty()) {
            if (TravelHandler.shortTeleport(level, player)) {
                player.getCooldowns().addCooldown(item, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        } else {
            if (TravelHandler.blockTeleport(level, player)) {
                player.getCooldowns().addCooldown(item, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        }
        return false;
    }

}
