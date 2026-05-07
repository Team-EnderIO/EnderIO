package com.enderio.enderio.content.tools.vials;

import com.enderio.core.common.capability.StrictItemAccessFluidHandler;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.google.common.primitives.Ints;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Consumer;

@EventBusSubscriber
public class VoidVialItem extends Item implements ICustomCreativeTabEntries {
    public static final ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER =
        (stack, itemAccess) -> new StrictItemAccessFluidHandler(itemAccess, EIODataComponents.ITEM_FLUID_CONTENT,
            Ints.saturatedCast(ExperienceUtil.getFluidFromLevel(10)),
            Tags.Fluids.EXPERIENCE);

    public VoidVialItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        var filledStack = new ItemStack(this);
        var fluidHandler = ItemAccess.forStack(filledStack).getCapability(Capabilities.Fluid.ITEM);

        if (fluidHandler != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                fluidHandler.insert(FluidResource.of(EIOFluids.XP_JUICE.source()), Integer.MAX_VALUE, transaction);
                transaction.commit();
            }
        }

        output.accept(filledStack);
    }

    public static boolean isFilled(ItemStack stack) {
        var fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        return fluidHandler != null && fluidHandler.getAmountAsInt(0) > 0;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var capability = ItemAccess.forPlayerInteraction(player, hand).getCapability(Capabilities.Fluid.ITEM);
        if (capability == null || capability.getAmountAsInt(0) < ExperienceUtil.EXP_TO_FLUID) {
            return InteractionResult.FAIL;
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = stack.copy();
        var capability = ItemAccess.forStack(result).getCapability(Capabilities.Fluid.ITEM);
        if (!(livingEntity instanceof Player player) || capability == null) {
            return stack;
        }

        int amount = capability.getAmountAsInt(0);
        if (amount < ExperienceUtil.EXP_TO_FLUID) {
            return stack;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = capability.extract(capability.getResource(0), (amount / ExperienceUtil.EXP_TO_FLUID) * ExperienceUtil.EXP_TO_FLUID, transaction);
            player.giveExperiencePoints(extracted / ExperienceUtil.EXP_TO_FLUID);
            transaction.commit();
        }

        return result;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isFilled(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Color from XP Juice texture
        return 0xFF66FF03;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler != null && isFilled(stack)) {
            return Math.round(fluidHandler.getAmountAsInt(0) * 13f / fluidHandler.getCapacityAsInt(0, fluidHandler.getResource(0)));
        }

        return 0;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isFilled(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        builder.accept(ToolsLang.VOID_VIAL_HINT);

        var tankCap = ItemAccess.forStack(itemStack).getCapability(Capabilities.Fluid.ITEM);
        if (tankCap != null) {
            FluidResource storedFluid = tankCap.getResource(0);
            if (storedFluid.isEmpty()) {
                return;
            }

            int amount = tankCap.getAmountAsInt(0);
            int capacity = tankCap.getCapacityAsInt(0, storedFluid);

            if (amount > 0) {
                builder.accept(TooltipUtil.styledWithArgs(EIOCommonLang.FLUID_TANK_TOOLTIP, amount, capacity, storedFluid.getHoverName()));

                var xp = ExperienceUtil.getLevelFromFluidWithLeftover(amount);
                builder.accept(TooltipUtil.styledWithArgs(ToolsLang.VOID_VIAL_STORED_EXPERIENCE, xp.level(), xp.experience()));
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return (int) (1.6F * 20.0F); // default food time
    }

    @SubscribeEvent
    public static void collectEXP(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();

        // Using forPlayerSlot directly to avoid spamming creative players with new items.
        ItemAccess itemAccess = ItemAccess.forPlayerSlot(player, player.getInventory().getSelectedSlot());
        if (!(itemAccess.getResource().getItem() instanceof VoidVialItem)) {
            itemAccess = ItemAccess.forPlayerSlot(player, Inventory.SLOT_OFFHAND);
        }

        if (!(itemAccess.getResource().getItem() instanceof VoidVialItem)) {
            return;
        }

        var cap = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        if (cap == null) {
            return;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int orbFluidVolume = event.getOrb().getValue() * ExperienceUtil.EXP_TO_FLUID;

            int maxFill;
            try (Transaction simulateTransaction = Transaction.open(transaction)) {
                maxFill = cap.insert(FluidResource.of(EIOFluids.XP_JUICE.source()), orbFluidVolume, simulateTransaction);
            }

            // Ensure we're aligning to the EXP_TO_FLUID boundary
            int amountToFill = maxFill - (maxFill % ExperienceUtil.EXP_TO_FLUID);
            if (amountToFill <= 0) {
                return;
            }

            int filled = cap.insert(FluidResource.of(EIOFluids.XP_JUICE.source()), amountToFill, transaction);
            if (filled != amountToFill) {
                return;
            }

            int newOrbVolume = (orbFluidVolume - amountToFill);
            event.getOrb().setValue(newOrbVolume / ExperienceUtil.EXP_TO_FLUID);
            transaction.commit();
        }
    }
}
