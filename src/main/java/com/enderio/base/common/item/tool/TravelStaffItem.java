package com.enderio.base.common.item.tool;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.EnergyStorageItemStack;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.item.ITabVariants;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import com.tterrag.registrate.util.CreativeModeTabModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TravelStaffItem extends Item implements IMultiCapabilityItem, IAdvancedTooltipProvider, ITabVariants {
    public TravelStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (getActivationStatus(stack).isAir()) {
            if (tryPerformAction(level, player, stack)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (getActivationStatus(context.getItemInHand()).isBlock()) {
            if (context.getPlayer() != null && tryPerformAction(context.getLevel(), context.getPlayer(), context.getItemInHand())) {
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    private boolean tryPerformAction(Level level, Player player, ItemStack stack) {
        boolean isCreative = player.isCreative();
        if (hasResources(stack) || isCreative) {
            if (performAction(level, player,stack)) {
                if (!level.isClientSide() && !isCreative) {
                    consumeResources(stack);
                }

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
            if (TravelHandler.shortTeleport(level, player)) {
                player.getCooldowns().addCooldown(this, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        } else {
            if (TravelHandler.blockTeleport(level, player)) {
                player.getCooldowns().addCooldown(this, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        }
        return false;
    }

    public int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_STAFF_MAX_ENERGY.get();
    }

    public boolean hasResources(ItemStack stack) {
        return EnergyUtil.hasEnergy(stack, BaseConfig.COMMON.ITEMS.TRAVELLING_STAFF_ENERGY_USE.get());
    }

    public void consumeResources(ItemStack stack) {
        EnergyUtil.extractEnergy(stack, BaseConfig.COMMON.ITEMS.TRAVELLING_STAFF_ENERGY_USE.get(), false);
    }

    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(Capabilities.ENERGY, LazyOptional.of(() -> new EnergyStorageItemStack(stack, getMaxEnergy())));
        return provider;
    }

    protected ActivationStatus getActivationStatus(ItemStack stack) {
        return ActivationStatus.ALL;
    }

    @Override
    public void addAllVariants(CreativeModeTabModifier modifier) {
        modifier.accept(this);
        ItemStack is = new ItemStack(this);
        EnergyUtil.setFull(is);
        modifier.accept(is);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack
            .getCapability(Capabilities.ENERGY)
            .map(energyStorage -> Math.round(energyStorage.getEnergyStored() * 13.0F / energyStorage.getMaxEnergyStored()))
            .orElse(0);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return EnergyBarDecorator.BAR_COLOR;
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @org.jetbrains.annotations.Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d", EnergyUtil.getEnergyStored(itemStack)) + "/" + String.format("%,d", EnergyUtil.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOLang.ENERGY_AMOUNT, energy));
    }

    protected enum ActivationStatus {
        BLOCK(true, false), AIR(false, true), ALL(true, true);

        private final boolean isBlock;
        private final boolean isAir;

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
