package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.ForkUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.util.BlockUtil;
import com.enderio.core.common.util.TooltipUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class DarkSteelAxeItem extends AxeItem implements IDarkSteelItem, CreativeTabVariants {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_AXE, EmpoweredUpgrade.NAME,
                        ForkUpgrade.NAME, DirectUpgrade.NAME);
    }

    public DarkSteelAxeItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER,
                pProperties.attributes(createAttributes(ArmoryItems.DARK_STEEL_TIER, 5, -3)));
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        super.setDamage(stack, EmpoweredUpgrade.getAdjustedDamage(stack, newDamage));
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        final float baseSpeed = super.getDestroySpeed(pStack, pState);
        return getEmpoweredUpgrade(pStack)
                .map(empoweredUpgrade -> empoweredUpgrade.adjustDestroySpeed(baseSpeed, pStack))
                .orElse(baseSpeed);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos,
            LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player) {
            if (pEntityLiving.isCrouching() && pState.is(BlockTags.LOGS)
                    && ItemStackEnergy.getEnergyStored(pStack) > 0) {

                int maxSearchSize = 400; // put an upper limit on search size
                Set<BlockPos> chopCandidates = new HashSet<>();
                collectTreeBlocks(pLevel, pPos, new HashSet<>(), chopCandidates, maxSearchSize, pState.getBlock());
                chopCandidates.remove(pPos); // don't double harvest this guy

                int energyPerBlock = ArmoryConfig.COMMON.DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG.get();
                int maxBlocks = ItemStackEnergy.getEnergyStored(pStack) / energyPerBlock;

                Collection<BlockPos> toChop = chopCandidates;
                if (maxBlocks < chopCandidates.size()) {
                    // If not enough power to get them all cut top to bottom to avoid floating logs
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
                        if (chopCount >= maxBlocks) {
                            break;
                        }
                    }
                }
                if (energyUse > 0) {
                    ItemStackEnergy.extractEnergy(pStack, energyUse, false);
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (hasFork(pContext.getItemInHand())) {
            return Items.DIAMOND_HOE.useOn(pContext);
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return super.canPerformAction(stack, itemAbility)
                || (hasFork(stack) && ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    private boolean hasFork(ItemStack stack) {
        return DarkSteelHelper.hasUpgrade(stack, ForkUpgrade.NAME);
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
    private void collectTreeBlocks(Level level, BlockPos pos, Set<BlockPos> checkedPos, Set<BlockPos> toChop,
            int maxBlocks, Block targetBock) {
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
        if (isDetailed && getEmpoweredUpgrade(itemStack).isPresent()) {
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_EFFICIENCY,
                    ArmoryConfig.COMMON.EMPOWERED_EFFICIENCY_BOOST.get()));
        }
        IDarkSteelItem.super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);
        modifier.accept(createFullyUpgradedStack(this));
    }

    // region Common for all tools

    public boolean isFoil(ItemStack pStack) {
        return DarkSteelHelper.hasUpgrade(pStack, EmpoweredUpgrade.NAME) || super.isFoil(pStack);
    }

    // endregion

}
