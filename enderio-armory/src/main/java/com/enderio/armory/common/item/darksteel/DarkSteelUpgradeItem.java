package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.util.TooltipUtil;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class DarkSteelUpgradeItem extends Item implements AdvancedTooltipProvider, CreativeTabVariants {

    public static void writeUpgradeToItemStack(ItemStack stack, IDarkSteelUpgrade upgrade) {
        CustomData rootNBT = stack.getOrDefault(ArmoryDataComponents.DARK_STEEL_UPGRADE,
                CustomData.of(new CompoundTag()));
        CompoundTag rootTag = rootNBT.copyTag();
        rootTag.putString("name", upgrade.getName());
        rootTag.put("data", upgrade.serializeNBT());
        stack.set(ArmoryDataComponents.DARK_STEEL_UPGRADE, CustomData.of(rootTag));
    }

    public static Optional<IDarkSteelUpgrade> readUpgradeFromStack(ItemStack stack) {
        if (stack.isEmpty() || !stack.has(ArmoryDataComponents.DARK_STEEL_UPGRADE)) {
            return Optional.empty();
        }
        @Nullable
        CustomData upgradeData = stack.get(ArmoryDataComponents.DARK_STEEL_UPGRADE);
        if (upgradeData == null) {
            return Optional.empty();
        }
        CompoundTag rootTag = upgradeData.copyTag();
        String serName = rootTag.getString("name");
        final Optional<IDarkSteelUpgrade> upgrade = DarkSteelUpgradeRegistry.instance().createUpgrade(serName);
        return upgrade.map(up -> {
            up.deserializeNBT(Objects.requireNonNull(rootTag.get("data")));
            return upgrade;
        }).orElse(Optional.empty());
    }

    private final ModConfigSpec.ConfigValue<Integer> levelsRequired;

    public Supplier<? extends IDarkSteelUpgrade> getUpgrade() {
        return upgrade;
    }

    public ModConfigSpec.ConfigValue<Integer> getLevelsRequired() {
        return levelsRequired;
    }

    private final Supplier<? extends IDarkSteelUpgrade> upgrade;

    public DarkSteelUpgradeItem(Properties pProperties, ModConfigSpec.ConfigValue<Integer> levelsRequired,
            Supplier<? extends IDarkSteelUpgrade> upgrade) {
        super(pProperties.stacksTo(1));
        this.levelsRequired = levelsRequired;
        this.upgrade = upgrade;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelUpgradeRegistry.instance().hasUpgrade(pStack);
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);

        ItemStack is = new ItemStack(this);
        writeUpgradeToItemStack(is, upgrade.get());
        modifier.accept(is);
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
//        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
//        @Nullable
//        DarkSteelCapability cap = pPlayer.getItemInHand(InteractionHand.OFF_HAND)
//                .getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
//        if (cap != null) {
//            if (isAlreadyInstalled(cap)) {
//                // TODO: Just for testing
//                cap.removeUpgrade(upgrade.get().getName());
//            } else if (cap.canApplyUpgrade(upgrade.get())) {
//                if (pPlayer.experienceLevel >= levelsRequired.get() || pPlayer.isCreative()) {
//                    if (!pPlayer.isCreative()) {
//                        pPlayer.giveExperienceLevels(-levelsRequired.get());
//                    }
//                    cap.addUpgrade(upgrade.get());
//                    stack.setCount(0);
//                    pLevel.playSound(pPlayer, pPlayer.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS,
//                            1.0F, new Random().nextFloat() * 0.1F + 0.9F);
//                    return InteractionResultHolder.consume(stack);
//                } else if (pLevel.isClientSide) {
//                    pPlayer.sendSystemMessage(ArmoryLang.DS_UPGRADE_ITEM_NO_XP);
//                }
//            } else if (pLevel.isClientSide) {
//                pPlayer.sendSystemMessage(ArmoryLang.DS_UPGRADE_ITEM_INVALID_UPGRADE);
//            }
//        } else if (pLevel.isClientSide) {
//            pPlayer.sendSystemMessage(ArmoryLang.DS_UPGRADE_ITEM_NO_TARGET);
//        }
//        return super.use(pLevel, pPlayer, pUsedHand);
//    }
//
//    private boolean isAlreadyInstalled(@NotNull DarkSteelCapability cap) {
//        Optional<IDarkSteelUpgrade> opt = cap.getUpgrade(upgrade.get().getName());
//        if (opt.isEmpty()) {
//            return false;
//        }
//        int installedTier = opt.map(up -> up.getTier().map(IUpgradeTier::getLevel).orElse(0)).orElse(0);
//        int myTier = upgrade.get().getTier().map(IUpgradeTier::getLevel).orElse(0);
//        return myTier == installedTier;
//    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        Collection<Component> desc = upgrade.get().getDescription();
        for (Component component : desc) {
            tooltips.add(component.copy().withStyle(ChatFormatting.GRAY));
        }
        if (!DarkSteelUpgradeRegistry.instance().hasUpgrade(itemStack)) {
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_XP_COST, levelsRequired.get())
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltips.add(ArmoryLang.DS_UPGRADE_ACTIVATE.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
