package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class XPObeliskBlockEntity extends MachineBlockEntity {

    IntegerNetworkDataSlot xpTankDataSlot;

    public XPObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.XP_OBELISK.get(), worldPosition, blockState);

        this.xpTankDataSlot = new IntegerNetworkDataSlot(() -> getFluidTankNN().getFluidAmount(),
            amount -> getFluidTankNN().setFluid(new FluidStack(EIOFluids.XP_JUICE.getSource(), amount)));
        addDataSlot(xpTankDataSlot);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new XPObeliskMenu(this, playerInventory, containerId);
    }

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new FluidTank(Integer.MAX_VALUE, fluidStack -> fluidStack.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into XP Juice
                if (this.isFluidValid(resource)) {
                    var currentFluid = this.getFluid().getFluid();
                    if (currentFluid == Fluids.EMPTY || resource.getFluid().isSame(currentFluid)) {
                        return super.fill(resource, action);
                    } else {
                        return super.fill(new FluidStack(currentFluid, resource.getAmount()), action);
                    }
                }

                // Non-XP is not allowed.
                return 0;
            }
        };
    }

    public void addLevelToPlayer(int levelDiff, Player player) {
        int requestedLevel = player.experienceLevel + levelDiff;
        requestedLevel = Math.max(requestedLevel, 0);
        long playerXP = ExperienceUtil.getPlayerTotalXp(player);
        long requestedXP = ExperienceUtil.getTotalXpFromLevel(requestedLevel) - playerXP;
        int storedXP = getFluidTankNN().getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;

        long awardXP = levelDiff > 0 ? Math.min(storedXP, requestedXP) : requestedXP;
        awardXP(awardXP, player);
    }

    public void addAllLevelToPlayer(boolean give, Player player) {
        long awardXP = 0;
        if (give) {
            awardXP = getFluidTankNN().getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;
        } else {
            awardXP = -ExperienceUtil.getPlayerTotalXp(player);
        }
        awardXP(awardXP, player);
    }

    public void awardXP(long exp, Player player) {
        long volumeToRemove = exp * ExperienceUtil.EXP_TO_FLUID;
        // Positive -> Give levels to player ; Negative -> Take levels from player
        if (volumeToRemove > 0) {
            int cappedVolume = (int) Math.min(Integer.MAX_VALUE, volumeToRemove);
            FluidStack drained = getFluidTankNN().drain(new FluidStack(EIOFluids.XP_JUICE.getSource(), cappedVolume), IFluidHandler.FluidAction.EXECUTE);
            player.giveExperiencePoints(drained.getAmount() / ExperienceUtil.EXP_TO_FLUID);

        } else {
            int cappedVolume = (int) Math.min(Integer.MAX_VALUE, -volumeToRemove); // Invert
            int filled = getFluidTankNN().fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), cappedVolume), IFluidHandler.FluidAction.EXECUTE);
            player.giveExperiencePoints(-1 * filled / ExperienceUtil.EXP_TO_FLUID); // Negative -> Take
        }
    }

}
