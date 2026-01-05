package com.enderio.enderio.content.travel;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TravelStaffItem extends Item implements AdvancedTooltipProvider, ICustomCreativeTabEntries {

    public static final ICapabilityProvider<ItemStack, ItemAccess, EnergyHandler> ENERGY_STORAGE_PROVIDER = (stack,
            itemAccess) -> new ItemAccessEnergyHandler(itemAccess, EIODataComponents.ENERGY, TravelStaffItem.getMaxEnergy());

    public TravelStaffItem(Properties properties) {
        super(properties.component(EIODataComponents.TRAVEL_ITEM, true).stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (getActivationStatus(stack).isAir()) {
            if (tryPerformAction(level, player, stack)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (getActivationStatus(context.getItemInHand()).isBlock()) {
            if (context.getPlayer() != null
                    && tryPerformAction(context.getLevel(), context.getPlayer(), context.getItemInHand())) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    private boolean tryPerformAction(Level level, Player player, ItemStack stack) {
        boolean isCreative = player.isCreative();
        if (TravelHandler.hasResources(stack) || isCreative) {
            if (performAction(stack, level, player)) {
                if (!level.isClientSide() && !isCreative) {
                    TravelHandler.consumeResources(stack);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Perform your action
     * @return true if it was a success and you want to consume the resources
     */
    public boolean performAction(ItemStack itemStack, Level level, Player player) {
        if (player.isShiftKeyDown()) {
            if (TravelHandler.shortTeleport(level, player)) {
                player.getCooldowns().addCooldown(itemStack, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        } else {
            if (TravelHandler.blockTeleport(level, player)) {
                player.getCooldowns().addCooldown(itemStack, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            } else if (TravelHandler.interact(level, player)) {
                player.getCooldowns().addCooldown(itemStack, BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
                return true;
            }
        }

        return false;
    }

    public static int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_STAFF_MAX_ENERGY.get();
    }

    protected ActivationStatus getActivationStatus(ItemStack stack) {
        return ActivationStatus.ALL;
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack is = new ItemStack(this);
        ItemStackEnergy.setFull(is);
        output.accept(is);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        if (energyStorage != null) {
            return Math.round(energyStorage.getAmountAsInt() * 13.0F / energyStorage.getCapacityAsInt());
        }

        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return EnergyBarDecorator.BAR_COLOR;
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player,
            List<Component> tooltips) {
        String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/"
                + String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOCommonLang.ENERGY_AMOUNT, energy));
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

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

}
