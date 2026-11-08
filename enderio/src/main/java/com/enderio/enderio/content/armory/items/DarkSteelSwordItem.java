package com.enderio.enderio.content.armory.items;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.GetDestroySpeedHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.IsCorrectToolForDropsHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;
import com.enderio.enderio.content.armory.DarkSteelHelper;
import com.enderio.enderio.content.armory.IDarkSteelItem;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class DarkSteelSwordItem extends SwordItem implements AdvancedTooltipProvider, IDarkSteelItem, DarkSteelUpgradableItem {
    private static final Set<DarkSteelUpgradeHookType<?>> AVAILABLE_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION,
        DarkSteelUpgradeHookTypes.USE_ON,
        DarkSteelUpgradeHookTypes.GET_DESTROY_SPEED,
        DarkSteelUpgradeHookTypes.IS_CORRECT_TOOL_FOR_DROPS
    );

    public DarkSteelSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier,
                properties.attributes(createAttributes(tier, attackDamage, attackSpeed))
                        .component(EIODataComponents.TRAVEL_ITEM, false));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return super.canPerformAction(stack, itemAbility) || CanPerformActionHook.canPerformAction(DarkSteelHelper.getUpgradeContainer(stack),
            DarkSteelHelper.getHookContext(stack), itemAbility);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return UseOnHook.useOn(DarkSteelHelper.getUpgradeContainer(context.getItemInHand()), DarkSteelHelper.getHookContext(context.getItemInHand()),
            context, super.useOn(context));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GetDestroySpeedHook.getDestroySpeed(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return IsCorrectToolForDropsHook.isCorrectToolForDrops(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), state);
    }

    @Override
    public Set<DarkSteelUpgradeHookType<?>> availableHookTypes() {
        return AVAILABLE_HOOKS;
    }
}
