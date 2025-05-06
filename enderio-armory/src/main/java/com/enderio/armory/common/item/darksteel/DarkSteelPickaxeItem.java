package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgradeHandler;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.util.TooltipUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class DarkSteelPickaxeItem extends PickaxeItem implements IDarkSteelItem, CreativeTabVariants {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_PICKAXE, EmpoweredUpgrade.NAME,
                        SpoonUpgrade.NAME, DirectUpgrade.NAME, ExplosiveUpgrade.NAME, ExplosivePenetrationUpgrade.NAME,
                        TravelUpgrade.NAME);
    }

    private final ModConfigSpec.ConfigValue<Integer> obsidianBreakPowerUse = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST;

    private final ModConfigSpec.ConfigValue<Integer> speedBoostWhenObsidian = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_OBSIDIAN_SPEED;

    private final ModConfigSpec.ConfigValue<Integer> useObsidianBreakSpeedAtHardness = ArmoryConfig.COMMON.DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS;

    public DarkSteelPickaxeItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER,
                pProperties.attributes(createAttributes(ArmoryItems.DARK_STEEL_TIER, 1, -2.8F)));
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        super.setDamage(stack, EmpoweredUpgrade.getAdjustedDamage(stack, newDamage));
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        final float baseSpeed = super.getDestroySpeed(pStack, pState);
        float adjustedSpeed = getEmpoweredUpgrade(pStack)
                .map(empoweredUpgrade -> empoweredUpgrade.adjustDestroySpeed(baseSpeed, pStack))
                .orElse(baseSpeed);
        adjustedSpeed = ExplosiveUpgradeHandler.adjustDestroySpeed(adjustedSpeed, pStack);
        if (useObsidianMining(pState, pStack)) {
            adjustedSpeed += speedBoostWhenObsidian.get();
        }
        return adjustedSpeed;
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos,
            LivingEntity pEntityLiving) {
        if (useObsidianMining(pState, pStack)) {
            ItemStackEnergy.extractEnergy(pStack, obsidianBreakPowerUse.get(), false);
        }
        ExplosiveUpgradeHandler.onMineBlock(pStack, pLevel, pPos, pEntityLiving);
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        @Nullable
        InteractionResult res = TravelUpgrade.onUse(pContext, this);
        if (res != null) {
            return res;
        }
        if (hasSpoon(pContext.getItemInHand())) {
            return Items.DIAMOND_SHOVEL.useOn(pContext);
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        @Nullable
        InteractionResultHolder<ItemStack> res = TravelUpgrade.onUse(level, player, usedHand, this);
        if (res != null) {
            return res;
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return super.canPerformAction(stack, itemAbility)
                || (hasSpoon(stack) && ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility));
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);

        // Include a fully upgraded version without explosive upgrades
        ItemStack itemStack = createFullyUpgradedStack(this);
        ItemStackEnergy.setFull(itemStack);
        DarkSteelHelper.removeUpgrade(itemStack, ExplosiveUpgrade.NAME);
        DarkSteelHelper.removeUpgrade(itemStack, ExplosivePenetrationUpgrade.NAME);
        modifier.accept(itemStack);

        ItemStack fullyUpgraded = createFullyUpgradedStack(this);
        ItemStackEnergy.setFull(fullyUpgraded);
        modifier.accept(fullyUpgraded);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    private boolean hasSpoon(ItemStack stack) {
        return DarkSteelHelper.hasUpgrade(stack, SpoonUpgrade.NAME);
    }

    private boolean useObsidianMining(BlockState pState, ItemStack stack) {
        return ItemStackEnergy.getEnergyStored(stack) >= obsidianBreakPowerUse.get() && treatBlockAsObsidian(pState);
    }

    private boolean treatBlockAsObsidian(BlockState pState) {
        return pState.getBlock() == Blocks.OBSIDIAN || (useObsidianBreakSpeedAtHardness.get() > 0
                && pState.getBlock().defaultDestroyTime() >= useObsidianBreakSpeedAtHardness.get());
    }

    @Override
    public void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        if (isDetailed && getEmpoweredUpgrade(itemStack).isPresent()) {
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_EFFICIENCY,
                    ArmoryConfig.COMMON.EMPOWERED_EFFICIENCY_BOOST.get()));
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY,
                    speedBoostWhenObsidian.get()));
        }
        IDarkSteelItem.super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
    }

    // region Common for all tools

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelHelper.hasUpgrade(pStack, EmpoweredUpgrade.NAME) || super.isFoil(pStack);
    }

    // endregion
}
