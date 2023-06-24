package com.enderio.machines.common.io.fluid;

import com.enderio.EnderIO;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class MachineFluidTank extends FluidTank {
    public boolean allowInput = true, allowOutput = true;
    private @Nullable BlockEntity parent = null;

    public MachineFluidTank(int capacity, BlockEntity parent) {
        super(capacity, (FluidStack stack) -> true);
        this.parent = parent;
    }

    public MachineFluidTank(BlockEntity parent) {
        super(8 * FluidType.BUCKET_VOLUME, (FluidStack stack) -> true);
        this.parent = parent;
    }

    /**
     * @param capacity  The capacity of the tank
     * @param validator The filter, a lambda expression that should return whether a fluid is allowed or not.
     * @param parent    The container to be updated in case that the amount or type of fluid changes.
     *                  (Usually the block entity that "contains" the tank)
     */
    public MachineFluidTank(int capacity, Predicate<FluidStack> validator, BlockEntity parent) {
        super(capacity, validator);
        this.parent = parent;
    }

    /**
     * Transfers fluid from a fluid handler to this tank.
     *
     * @param from          Fluid handler to transfer from
     * @param desiredAmount The max amount to transfer if possible.
     * @param force         Force transfer even if 'allowInput' is false or validator prohibits fluid to enter. It respects the fluid types though
     * @return The amount that was actually transferred.
     */
    public int transferFrom(IFluidHandler from, int desiredAmount, boolean force) {
        FluidStack incomingStack = from.drain(desiredAmount, FluidAction.SIMULATE);
        int transferredAmount = fill(incomingStack, FluidAction.EXECUTE, force);
        from.drain(transferredAmount, FluidAction.EXECUTE);
        return transferredAmount;
    }

    /**
     * Transfers fluid from a fluid handler to this tank.
     *
     * @param to            The handler to transfer into.
     * @param desiredAmount The max amount to transfer if possible.
     * @param force         Force transfer even if 'allowOutput' is false.
     * @return The amount that was actually transferred.
     */
    public int transferTo(IFluidHandler to, int desiredAmount, boolean force) {
        FluidStack outStack = fluid.copy();
        int transferAmount = 0;
        if (!outStack.isEmpty()) {
            outStack.setAmount(drain(desiredAmount, FluidAction.SIMULATE, force));//caps at the tank's capacity to transfer out of and at the desired amount
            transferAmount = to.fill(outStack, FluidAction.EXECUTE);
        }
        drain(transferAmount, FluidAction.EXECUTE);//should always succeed to transfer what we have simulated
        return transferAmount;
    }

    /**
     * Works like the FluidTank class of forge with some additions described below.
     *
     * @param source The source fluid stack. Tries to increase tank amount with entire source amount.
     *               WARNING! This method does not drain the source stack according to the forge FluidTank implementation.
     *               For that use case, use the method "transferFrom"
     * @param action Execute makes the transfer happen, simulate checks what would happen if executed.
     * @param force  Force your way around the allow input flag and the validator (filter). Usually used to
     *               fill a machine output tank from within the machine.
     * @return The amount that could be filled in case of simulation or was filled in case of execution.
     * <p>
     * Credit: the Forge team, since the logic is their work.
     */
    public int fill(FluidStack source, FluidAction action, boolean force) {
        if (source.isEmpty())
            return 0;
        if (!fluid.isEmpty() && !fluid.isFluidEqual(source))
            return 0;
        if (!allowInput && !force)
            return 0;//Continue filling if either allow input or force input
        if (!isFluidValid(source) && !force)
            return 0;
        else if (action.simulate()) {
            return Math.min(capacity - fluid.getAmount(), source.getAmount());
        } else {
            if (fluid.isEmpty()) {
                fluid = new FluidStack(source, Math.min(capacity, source.getAmount()));
                onContentsChanged();//case source is empty is checked in beginning.
                return fluid.getAmount();
            } else {
                int availableSpace = capacity - fluid.getAmount();
                int sourceAmount = source.getAmount();
                int transferAmount = Math.min(availableSpace, sourceAmount);
                fluid.grow(transferAmount);
                //according to the parent implementation the source shall not be drained
                if (transferAmount > 0)
                    onContentsChanged();
                return transferAmount;
            }
        }
    }

    //Same as parent behavior with the addition of this class capabilities, see method 'fill' above for details.
    public int fill(int desiredAmount, FluidAction action, boolean force) {
        if (fluid.isEmpty()) {
            EnderIO.LOGGER.error("No fluid in tank, can't contain an amount of unspecified fluid other than 0");
            return 0;
        }
        return fill(new FluidStack(fluid.getFluid(), desiredAmount), action, force);
    }

    //Same as parent behavior with the addition of this class capabilities, see method 'fill' above for details.
    @Override
    public int fill(FluidStack source, FluidAction action) {
        return fill(source, action, false);
    }

    /**
     * Drains fluid out of the tank.
     *
     * @param maxDrain The maximal amount that you wish to draw in mb. In case of using a bucket it would be FluidType.BUCKET_VOLUME (1000)mb.
     * @param action   Execute if the tank amount should be changed, simulate if not.
     * @param force    Force your way around the allow output flag. Usually used to drain a machine "input" tank from within the machine.
     * @return The amount that was drained in case of action is execute and how much could be drawn in case of
     */
    public int drain(int maxDrain, FluidAction action, boolean force) {
        if (maxDrain <= 0)
            return 0;
        if (!allowOutput && !force)
            return 0; // Continue draining only if either allow drain or force
        int transferAmount = Math.min(maxDrain, fluid.getAmount());
        if (action == FluidAction.EXECUTE && transferAmount > 0) {
            fluid.shrink(transferAmount);
            onContentsChanged();
        }
        return transferAmount;
    }

    //Same as parent behavior with the addition of this class capabilities, see method 'drain' above for details.
    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid))
            return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }
    //Same as parent behavior with the addition of this class capabilities, see method 'drain' above for details

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        // Get this before the drain, otherwise it will forget what we had stored.
        var storedFluid = fluid.getFluid();
        int amount = drain(maxDrain, action, false);
        if (amount == 0)
            return FluidStack.EMPTY;
        return new FluidStack(storedFluid, amount);
    }

    /**
     * Checks whether the item in the players hand can deposit or take up fluids from this tank, and if so does the transfer
     *
     * @param player The player.
     * @param hand   The interaction hand.
     * @return Consumed if interacted, success otherwise.
     */
    public InteractionResult onClickedWithPotentialFluidItem(Player player, InteractionHand hand) {
        if (!player.level().isClientSide() && player.hasItemInSlot(EquipmentSlot.MAINHAND) && hand == InteractionHand.MAIN_HAND) {
            ItemStack heldStack = player.getMainHandItem();
            Optional<IFluidHandlerItem> heldItemFH = heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
            if (heldStack.getItem() == Items.BUCKET && allowOutput) {
                //Clicked with empty bucket and tank allowed to output fluids
                if (getFluidAmount() >= FluidType.BUCKET_VOLUME) {
                    FluidStack outStack = drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    ItemStack filledBucket = new ItemStack(outStack.getFluid().getBucket(), 1);
                    if (heldStack.getCount() == 1) {
                        player.setItemInHand(hand, filledBucket);
                    } else {
                        heldStack.shrink(1);
                        if (!player.addItem(filledBucket)) {//has an empty slot in inventory
                            player.drop(filledBucket, true);
                        }
                    }
                    return InteractionResult.CONSUME;
                }
            } else if (heldStack.getItem() instanceof BucketItem filledBucket && allowInput) {
                //Clicked with bucket of something and tank allowed to input said thing
                FluidStack bucketContent = new FluidStack(filledBucket.getFluid(), FluidType.BUCKET_VOLUME);
                if (fill(bucketContent, IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {//can fit entire bucket in tank?
                    fill(bucketContent, IFluidHandler.FluidAction.EXECUTE);
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
                    return InteractionResult.CONSUME;
                }
            } else if (heldItemFH.isPresent()) {//Player holding item capable of holding fluids that isn't a bucket
                IFluidHandlerItem itemFluid = heldItemFH.get();
                boolean hasTransferred = false;

                if (allowOutput) {//clicked with tank that can take my fluid, extracting fluid
                    hasTransferred = transferTo(itemFluid, Integer.MAX_VALUE, false) > 0;
                }
                if (allowInput && !hasTransferred) {//clicked with tank containing fluids, push fluid into tank
                    hasTransferred = transferFrom(itemFluid, Integer.MAX_VALUE, false) > 0;//op-operator in edge case
                    //where interacted item has more than one fluid and I have
                }

                return hasTransferred ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;//Leave vanilla behaviour.
    }

    @Override
    public void setFluid(FluidStack stack) {
        setFluid(stack, true);
    }

    public void setFluid(FluidStack stack, boolean force) {
        if (force || (allowInput && isFluidValid(stack))) {
            this.fluid = stack;
            onContentsChanged();
        }
    }

    //updates the entity if one is marked
    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        if (parent != null)
            parent.setChanged();
    }
}
