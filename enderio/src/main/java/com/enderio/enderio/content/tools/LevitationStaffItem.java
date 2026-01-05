package com.enderio.enderio.content.tools;

import com.enderio.core.common.capability.StrictItemAccessFluidHandler;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class LevitationStaffItem extends PoweredToggledItem {

    public static final ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (stack,
            itemAccess) -> new StrictItemAccessFluidHandler(itemAccess, EIODataComponents.ITEM_FLUID_CONTENT, 1000,
                    EIOTags.Fluids.STAFF_OF_LEVITY_FUEL);

    public LevitationStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    protected int getEnergyUse() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_ENERGY_USE.get();
    }

    @Override
    public int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_MAX_ENERGY.get();
    }

    @Override
    protected boolean hasCharge(ItemStack stack) {
        if (!super.hasCharge(stack)) {
            return false;
        }

        var fluidHandler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
        if (fluidHandler != null) {
            // TODO: Config for consumption amount
            return fluidHandler.getAmountAsInt(0) > 1;
        }

        return false;
    }

    @Override
    protected void consumeCharge(ItemStack stack) {
        super.consumeCharge(stack);

        var fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                // TODO: Config for consumption amount
                fluidHandler.extract(fluidHandler.getResource(0), 1, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    protected void setFullCharge(ItemStack stack) {
        super.setFullCharge(stack);

        var fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler != null) {
            int capacity = fluidHandler.getCapacityAsInt(0, FluidResource.of(EIOFluids.VAPOR_OF_LEVITY.source()));

            // Just set the component itself.
            stack.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(
                new FluidStack(EIOFluids.VAPOR_OF_LEVITY.source(), capacity)));
        }
    }

    @Override
    protected void onTickWhenActive(Player player, ItemStack stack, Level level, @Nullable EquipmentSlot slot) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 1));
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
