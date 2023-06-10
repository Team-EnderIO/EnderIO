package com.enderio.base.common.item.tool;

import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.handler.TeleportHandler;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.item.darksteel.IDarkSteelItem;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgradeTier;
import com.enderio.core.common.util.EnergyUtil;
import com.tterrag.registrate.util.CreativeModeTabModifier;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TravelStaffItem extends Item implements IDarkSteelItem {
    public TravelStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (getActivationStatus(stack).isAir()) {if (tryPerformAction(level, player, stack)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {if (getActivationStatus(context.getItemInHand()).isBlock()) {
        if (context.getPlayer() != null && tryPerformAction(context.getLevel(), context.getPlayer(), context.getItemInHand())) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return InteractionResult.FAIL;
    }
        return super.useOn(context);
    }

    private boolean tryPerformAction(Level level, Player player, ItemStack stack) {
        if (hasResources(stack)) {
            if (performAction(level, player,stack)) {
                if (!level.isClientSide())
                    consumeResources(stack);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Perform your action
     * @return true if it was a success and you want to consume the resources
     */
    public boolean performAction(Level level, Player player, ItemStack stack) {
        if (player.isShiftKeyDown()) {
            if (TeleportHandler.shortTeleport(level, player)) {
                player.getCooldowns().addCooldown(this, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        } else {
            if (TeleportHandler.blockTeleport(level, player)) {
                player.getCooldowns().addCooldown(this, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        }
        return false;
    }

    public boolean hasResources(ItemStack stack) {
        return EnergyUtil.hasEnergy(stack, 1000);
    }

    public void consumeResources(ItemStack stack) {
        EnergyUtil.extractEnergy(stack, 1000, false);
    }

    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {

        IDarkSteelItem.super.initCapabilities(stack, nbt, provider);
        //Staff of Travelling always has Empowered I
        provider.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).ifPresent(
            cap -> cap.addUpgrade(EmpoweredUpgradeTier.ONE.getFactory().get()));
        return provider;
    }

    protected ActivationStatus getActivationStatus(ItemStack stack) {
        return ActivationStatus.ALL;
    }

    @Override
    public void addAllVariants(CreativeModeTabModifier modifier) {
        modifier.accept(this);
        modifier.accept(createFullyUpgradedStack(this));
    }

    protected enum ActivationStatus {
        BLOCK(true, false),
        AIR(false, true),
        ALL(true, true);

        private final boolean isBlock, isAir;

        ActivationStatus(boolean isBlock, boolean isAir) {
            this.isBlock = isBlock;
            this.isAir = isAir;
        }

        public boolean isBlock() {
            return isBlock;
        }

        public boolean isAir() {
            return isAir;
        }
    }

}
