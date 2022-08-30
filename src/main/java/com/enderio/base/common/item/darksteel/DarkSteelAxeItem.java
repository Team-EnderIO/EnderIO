package com.enderio.base.common.item.darksteel;

import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.ForkUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.BlockUtil;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.*;

public class DarkSteelAxeItem extends AxeItem implements IDarkSteelItem {

    public DarkSteelAxeItem(Properties pProperties) {
        super(EIOItems.DARK_STEEL_TIER, 5, -3, pProperties);
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        int finalDamage = getEmpoweredUpgrade(stack).map(empoweredUpgrade -> empoweredUpgrade.adjustDamage(getDamage(stack), newDamage)).orElse(newDamage);
        super.setDamage(stack, finalDamage);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        final float baseSpeed = canHarvest(pStack, pState) ? speed : 1.0f;
        return getEmpoweredUpgrade(pStack).map(empoweredUpgrade -> empoweredUpgrade.adjustDestroySpeed(baseSpeed)).orElse(baseSpeed);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player) {
            if (pEntityLiving.isCrouching() && pState.is(BlockTags.LOGS) && EnergyUtil.getEnergyStored(pStack) > 0) {

                int maxSearchSize = 400; //put an upper limit on search size
                Set<BlockPos> chopCandidates = new HashSet<>();
                collectTreeBlocks(pLevel, pPos, new HashSet<>(), chopCandidates, maxSearchSize, pState.getBlock());
                chopCandidates.remove(pPos); // don't double harvest this guy

                int energyPerBlock = BaseConfig.COMMON.DARK_STEEL.DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG.get();
                int maxBlocks = EnergyUtil.getEnergyStored(pStack)/energyPerBlock;

                Collection<BlockPos> toChop = chopCandidates;
                if(maxBlocks < chopCandidates.size()) {
                    //If not enough power to get them all cut top to bottom to avoid floating logs
                    List<BlockPos> orderedChopList = new ArrayList<>(chopCandidates);
                    orderedChopList.sort((o1, o2) -> Integer.compare(o2.getY(), o1.getY()));
                    toChop = orderedChopList;
                }

                int chopCount = 0;
                int energyUse = 0;
                for (BlockPos chopPos : toChop) {
                    if (BlockUtil.removeBlock(pLevel, player, pStack, chopPos)) {
                        energyUse += energyPerBlock;
                        chopCount++;
                        if(chopCount >= maxBlocks) {
                            break;
                        }
                    }
                }
                if (energyUse  > 0) {
                    EnergyUtil.extractEnergy(pStack, energyUse, false);
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return canHarvest(stack, state) && TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (hasFork(pContext.getItemInHand())) {
            return Items.DIAMOND_HOE.useOn(pContext);
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return super.canPerformAction(stack,toolAction) || (hasFork(stack) && ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction));
    }

    private boolean canHarvest(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE) || (state.is(BlockTags.MINEABLE_WITH_HOE) && hasFork(stack));
    }

    private boolean hasFork(ItemStack stack) {
        return DarkSteelUpgradeable.hasUpgrade(stack, ForkUpgrade.NAME);
    }

    /**
     * Recursive method to collect all blocks that form part of a tree.
     * @param level the level containing the tree
     * @param pos the position to be checked to see if it is part of the tree
     * @param checkedPos all positions already checked
     * @param toChop the list of positions for blocks that make up part of the tree
     * @param maxBlocks the maximum number of blocks that can be checked before the recursion ill end
     * @param targetBock the type of block the tree is made of, e.g. oak log
     */
    private void collectTreeBlocks(Level level, BlockPos pos, Set<BlockPos> checkedPos, Set<BlockPos> toChop, int maxBlocks, Block targetBock) {
        if (toChop.size() >= maxBlocks || checkedPos.contains(pos)) {
            return;
        }
        checkedPos.add(pos);
        BlockState checkState = level.getBlockState(pos);
        if (checkState.is(targetBock)) {
            toChop.add(pos);

            Set<BlockPos> toCheck = new HashSet<>();
            surrounding(toCheck, pos);
            surrounding(toCheck, pos.above());
            toCheck.add(pos.above());
            for (BlockPos newPos : toCheck) {
                collectTreeBlocks(level, newPos, checkedPos, toChop, maxBlocks, targetBock);
            }
        }
    }

    private void surrounding(Set<BlockPos> res, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                res.add(pos.offset(x, 0, z));
            }
        }
        res.remove(pos);
    }

    @Override
    public void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        if(isDetailed && getEmpoweredUpgrade(itemStack).isPresent()) {
            tooltips.add(TooltipUtil.withArgs(EIOLang.DS_UPGRADE_EMPOWERED_EFFICIENCY, BaseConfig.COMMON.DARK_STEEL.EMPOWERED_EFFICIENCY_BOOST.get()));
        }
        IDarkSteelItem.super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
    }

    // region Common for all tools

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelUpgradeable.hasUpgrade(pStack, EmpoweredUpgrade.NAME);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (allowedIn(pCategory)) {
            addCreativeItems(pItems, this);
        }
    }

    // endregion

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return isDurabilityBarVisible(pStack);
    }
}
