package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class XPObeliskBlockEntity extends MachineBlockEntity implements FluidTankUser {

    private final NetworkDataSlot<Integer> xpTankDataSlot;
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();

    public XPObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.XP_OBELISK.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        this.xpTankDataSlot = NetworkDataSlot.INT.create(() -> TANK.getFluidAmount(this),
            amount -> TANK.setFluid(this, new FluidStack(EIOFluids.XP_JUICE.getSource(), amount)));
        addDataSlot(xpTankDataSlot);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new XPObeliskMenu(this, playerInventory, containerId);
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return new MachineTankLayout.Builder().tank(TANK, Integer.MAX_VALUE, fluidStack -> fluidStack.is(EIOTags.Fluids.EXPERIENCE)).build();
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into XP Juice
                if (TANK.isFluidValid(this, resource)) {
                    var currentFluid = TANK.getFluid(this).getFluid();
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

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    public void addLevelToPlayer(int levelDiff, Player player) {
        int requestedLevel = player.experienceLevel + levelDiff;
        requestedLevel = Math.max(requestedLevel, 0);
        long playerXP = ExperienceUtil.getPlayerTotalXp(player);
        long requestedXP = ExperienceUtil.getTotalXpFromLevel(requestedLevel) - playerXP;
        int storedXP = TANK.getFluidAmount(this) / ExperienceUtil.EXP_TO_FLUID;

        long awardXP = levelDiff > 0 ? Math.min(storedXP, requestedXP) : requestedXP;
        awardXP(awardXP, player);
    }

    public void addAllLevelToPlayer(boolean give, Player player) {
        long awardXP;
        if (give) {
            awardXP = TANK.getFluidAmount(this) / ExperienceUtil.EXP_TO_FLUID;
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
            FluidStack drained = TANK.drain(this, cappedVolume, IFluidHandler.FluidAction.EXECUTE);
            player.giveExperiencePoints(drained.getAmount() / ExperienceUtil.EXP_TO_FLUID);

        } else {
            int cappedVolume = (int) Math.min(Integer.MAX_VALUE, -volumeToRemove); // Invert
            int filled = TANK.fill(this, new FluidStack(EIOFluids.XP_JUICE.getSource(), cappedVolume), IFluidHandler.FluidAction.EXECUTE);
            player.giveExperiencePoints(-1 * filled / ExperienceUtil.EXP_TO_FLUID); // Negative -> Take
        }
    }

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        loadTank(lookupProvider, pTag);
    }

    // endregion
}
