package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.armory.common.capability.DarkSteelUpgradeable;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgradeHandler;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.core.common.item.ITabVariants;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ToolActions;

import java.util.List;

public class DarkSteelPickaxeItem extends PickaxeItem implements IDarkSteelItem, ITabVariants {

    private final ModConfigSpec.ConfigValue<Integer> obsidianBreakPowerUse = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST;

    private final ModConfigSpec.ConfigValue<Integer> speedBoostWhenObsidian = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_OBSIDIAN_SPEED;

    private final ModConfigSpec.ConfigValue<Integer> useObsidianBreakSpeedAtHardness = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS;

    public DarkSteelPickaxeItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER, pProperties
            .attributes(createAttributes(ArmoryItems.DARK_STEEL_TIER, 1, -2.8F)));
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        int finalDamage = getEmpoweredUpgrade(stack).map(empoweredUpgrade -> empoweredUpgrade.adjustDamage(getDamage(stack), newDamage)).orElse(newDamage);
        super.setDamage(stack, finalDamage);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        final float baseSpeed = super.getDestroySpeed(pStack, pState);
        float adjustedSpeed = getEmpoweredUpgrade(pStack).map(empoweredUpgrade -> empoweredUpgrade.adjustDestroySpeed(baseSpeed)).orElse(baseSpeed);
        adjustedSpeed = ExplosiveUpgradeHandler.adjustDestroySpeed(adjustedSpeed, pStack);
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
        ExplosiveUpgradeHandler.onMineBlock(pStack, pLevel, pPos, pEntityLiving);
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // TODO: 20.6: I think tools need reworking properly. Just making this compile for now.
        return canHarvest(stack, state)/* && TierSortingRegistry.isCorrectTierForDrops(getTier(), state)*/;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (hasSpoon(pContext.getItemInHand())) {
            return Items.DIAMOND_SHOVEL.useOn(pContext);
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ToolAction toolAction) {
        return super.canPerformAction(stack, toolAction) || (hasSpoon(stack) && ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction));
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);
        modifier.accept(createFullyUpgradedStack(this));

        //Include a fully upgraded version without explosive upgrades
        ItemStack itemStack = createFullyUpgradedStack(this);
        DarkSteelUpgradeable.removeUpgrade(itemStack, ExplosiveUpgrade.NAME);
        DarkSteelUpgradeable.removeUpgrade(itemStack, ExplosivePenetrationUpgrade.NAME);
        modifier.accept(itemStack);

    }

    private boolean canHarvest(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || (state.is(BlockTags.MINEABLE_WITH_SHOVEL) && hasSpoon(stack));
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
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_EFFICIENCY, ArmoryConfig.COMMON.EMPOWERED_EFFICIENCY_BOOST.get()));
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY, speedBoostWhenObsidian.get()));
        }
        IDarkSteelItem.super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
    }

    // region Common for all tools

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelUpgradeable.hasUpgrade(pStack, EmpoweredUpgrade.NAME);
    }

    // endregion

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return isDurabilityBarVisible(pStack);
    }
}
