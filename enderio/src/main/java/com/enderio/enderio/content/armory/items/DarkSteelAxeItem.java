package com.enderio.enderio.content.armory.items;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.GetDestroySpeedHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.IsCorrectToolForDropsHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.MineBlockHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;
import com.enderio.enderio.content.armory.DarkSteelHelper;
import com.enderio.enderio.content.armory.IDarkSteelItem;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class DarkSteelAxeItem extends AxeItem implements AdvancedTooltipProvider, IDarkSteelItem, DarkSteelUpgradableItem {
    private static final Set<DarkSteelUpgradeHookType<?>> AVAILABLE_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.EQUIPMENT_TICK,
        DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION,
        DarkSteelUpgradeHookTypes.USE_ON,
        DarkSteelUpgradeHookTypes.USE,
        DarkSteelUpgradeHookTypes.GET_DESTROY_SPEED,
        DarkSteelUpgradeHookTypes.IS_CORRECT_TOOL_FOR_DROPS,
        DarkSteelUpgradeHookTypes.MINE_BLOCK,
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES
    );

    public DarkSteelAxeItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        return UseHook.use(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), level, player, usedHand,
            super.use(level, player, usedHand));
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
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        boolean result = super.mineBlock(stack, level, state, pos, miningEntity);
        MineBlockHook.mineBlock(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), level, state, pos, miningEntity);
        return result;
    }

    @Override
    public Set<DarkSteelUpgradeHookType<?>> availableHookTypes() {
        return AVAILABLE_HOOKS;
    }
}
