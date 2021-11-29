package com.enderio.base.common.item.darksteel;

import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.config.base.BaseConfig;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nonnull;
import java.util.List;

public class DarkSteelPickaxeItem extends PickaxeItem implements IDarkSteelItem {

    private final ForgeConfigSpec.ConfigValue<Integer> obsidianBreakPowerUse = BaseConfig.COMMON.ITEMS.DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST;

    private final ForgeConfigSpec.ConfigValue<Integer> speedBoostWhenObsidian = BaseConfig.COMMON.ITEMS.DARK_STEEL_PICKAXE_OBSIDIAN_SPEED;

    private final ForgeConfigSpec.ConfigValue<Integer> useObsidianBreakSpeedAtHardness = BaseConfig.COMMON.ITEMS.DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS;

    public DarkSteelPickaxeItem(Properties pProperties) {
        super(EIOItems.DARK_STEEL_TIER, 1, -2.8F, pProperties);
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        int finalDamage = getEmpoweredUpgrade(stack).map(empoweredUpgrade -> empoweredUpgrade.adjustDamage(getDamage(stack), newDamage)).orElse(newDamage);
        super.setDamage(stack, finalDamage);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        final float baseSpeed = canHarvest(pStack, pState) ? speed : 1.0f;
        float adjustedSpeed = getEmpoweredUpgrade(pStack).map(empoweredUpgrade -> empoweredUpgrade.adjustDestroySpeed(baseSpeed)).orElse(baseSpeed);
        if (useObsidianMining(pState, pStack)) {
            adjustedSpeed += speedBoostWhenObsidian.get();
        }
        return adjustedSpeed;
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (useObsidianMining(pState, pStack)) {
            EnergyUtil.extractEnergy(pStack, obsidianBreakPowerUse.get(), false);
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return canHarvest(stack, state) && TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (hasSpoon(pContext.getItemInHand())) {
            return Items.DIAMOND_SHOVEL.useOn(pContext);
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return super.canPerformAction(stack, toolAction) || (hasSpoon(stack) && ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction));
    }

    private boolean canHarvest(ItemStack stack, BlockState state) {
        return BlockTags.MINEABLE_WITH_PICKAXE.contains(state.getBlock()) || (state.is(BlockTags.MINEABLE_WITH_SHOVEL) && hasSpoon(stack));
    }

    private boolean hasSpoon(ItemStack stack) {
        return DarkSteelUpgradeable.hasUpgrade(stack, SpoonUpgrade.NAME);
    }

    private boolean useObsidianMining(BlockState pState, ItemStack stack) {
        return EnergyUtil.getEnergyStored(stack) >= obsidianBreakPowerUse.get() && treatBlockAsObsidian(pState);
    }

    private boolean treatBlockAsObsidian(BlockState pState) {
        return pState.getBlock() == Blocks.OBSIDIAN || (useObsidianBreakSpeedAtHardness.get() > 0
            && pState.getBlock().defaultDestroyTime() >= useObsidianBreakSpeedAtHardness.get());
    }

    @Override
    public void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        if(isDetailed && getEmpoweredUpgrade(itemStack).isPresent()) {
            tooltips.add(TooltipUtil.withArgs(EIOLang.DS_UPGRADE_EMPOWERED_EFFICIENCY, BaseConfig.COMMON.ITEMS.EMPOWERED_EFFICIENCY_BOOST.get()));
            tooltips.add(TooltipUtil.withArgs(EIOLang.DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY, speedBoostWhenObsidian.get()));
        }
        IDarkSteelItem.super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
    }

    // region Common for all tools

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelUpgradeable.hasUpgrade(pStack, EmpoweredUpgrade.NAME);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (allowdedIn(pCategory)) {
            addCreativeItems(pItems, this);
        }
    }

    // endregion
}
