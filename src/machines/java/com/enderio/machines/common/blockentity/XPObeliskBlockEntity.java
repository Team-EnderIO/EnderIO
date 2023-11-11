package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class XPObeliskBlockEntity extends MachineBlockEntity {

    IntegerNetworkDataSlot xpTankDataSlot;

    public XPObeliskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

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
        int playerXP = ExperienceUtil.getPlayerTotalXp(player);
        int requestedXP = ExperienceUtil.getExpFromLevel(requestedLevel) - playerXP;
        int storedXP = getFluidTankNN().getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;

        int awardXP = levelDiff > 0 ? Math.min(storedXP, requestedXP) : requestedXP;
        awardXP(awardXP, player);
    }

    public void addAllLevelToPlayer(boolean give, Player player) {
        int awardXP = 0;
        if (give) {
            awardXP = getFluidTankNN().getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;
        } else {
            awardXP = -ExperienceUtil.getPlayerTotalXp(player);
        }
        awardXP(awardXP, player);
    }

    public void awardXP(int exp, Player player) {
        player.giveExperiencePoints(exp);
        int volumeToRemove = exp * ExperienceUtil.EXP_TO_FLUID;
        updateTankContents(-volumeToRemove); // negative to perform remove from tank
    }

    private void updateTankContents(int amount) {
        if (amount > 0) {
            getFluidTankNN().fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), amount), IFluidHandler.FluidAction.EXECUTE);
        } else {
            getFluidTankNN().drain(new FluidStack(EIOFluids.XP_JUICE.getSource(), -amount), IFluidHandler.FluidAction.EXECUTE);
        }
    }

}
