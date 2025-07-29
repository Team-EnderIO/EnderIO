package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.menu.XPObeliskMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class XPObeliskBlockEntity extends MachineBlockEntity {

    IntegerNetworkDataSlot xpTankDataSlot;
    private static final TankAccess TANK = new TankAccess();

    private static final Logger LOGGER = LogUtils.getLogger();

    public XPObeliskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        this.xpTankDataSlot = new IntegerNetworkDataSlot(() -> TANK.getFluidAmount(this),
            amount -> TANK.setFluid(this, new FluidStack(EIOFluids.XP_JUICE.getSource(), amount)));
        addDataSlot(xpTankDataSlot);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new XPObeliskMenu(this, playerInventory, containerId);
    }

    @Override
    public @Nullable MachineTankLayout getTankLayout() {
        return new MachineTankLayout.Builder().tank(TANK, Integer.MAX_VALUE, fluidStack -> fluidStack.getFluid().is(EIOTags.Fluids.EXPERIENCE)).build();
    }

    @Override
    protected @Nullable MachineFluidHandler createFluidHandler(MachineTankLayout layout) {
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
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

    public void addLevelsToPlayer(Player player, int levelsToAdd) {
        long playerExperience = ExperienceUtil.getPlayerTotalXp(player);
        long targetExperience = ExperienceUtil.getTotalXpFromLevel(player.experienceLevel + levelsToAdd);
        addPlayerXp(player, targetExperience - playerExperience);
    }

    public void removeLevelsFromPlayer(Player player, int levelsToRemove) {
        long playerExperience = ExperienceUtil.getPlayerTotalXp(player);
        long targetExperience = ExperienceUtil.getTotalXpFromLevel(Math.max(0, player.experienceLevel - levelsToRemove));
        removePlayerXp(player, playerExperience - targetExperience);
    }

    public void addAllXpToPlayer(Player player) {
        long experienceToGive = TANK.getFluidAmount(this) / ExperienceUtil.EXP_TO_FLUID;
        addPlayerXp(player, experienceToGive);
    }

    public void removeAllXpFromPlayer(Player player) {
        long playerExperience = ExperienceUtil.getPlayerTotalXp(player);
        removePlayerXp(player, playerExperience);
    }

    private void addPlayerXp(Player player, long experience) {
        if (experience < 0) {
            throw new IllegalArgumentException("experience cannot be negative");
        }

        // Convert to volume
        long volume = experience * ExperienceUtil.EXP_TO_FLUID;

        // Reduce to int safely, and remove any fluid that will not make the conversion
        int cappedVolume = (int) Math.min(Integer.MAX_VALUE, volume);
        cappedVolume = cappedVolume - cappedVolume % ExperienceUtil.EXP_TO_FLUID;

        // Drain the fluid
        FluidStack drained = TANK.drain(this, cappedVolume, IFluidHandler.FluidAction.EXECUTE);

        // Add the XP to the player
        // Workaround some floating point problems when adding all the exp at once.
        // If we add it all at once, the experienceProgress gets messed up and then the next extract is wonky.
        int xpToAdd = drained.getAmount() / ExperienceUtil.EXP_TO_FLUID;
        while (xpToAdd > 0) {
            int xp = Mth.clamp((int)Math.floor((1 - player.experienceProgress) * ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel)), 0, xpToAdd);

            // If we can't add the rest of this level's progress, move on.
            if (xp <= 0) {
                xp = Mth.clamp(ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel + 1), 0, xpToAdd);
            }

            if (xp <= 0) {
                LOGGER.error("xp <= 0 in addPlayerXp. experienceLevel: {}, experienceProgress: {}, xpToAdd: {}, xp: {}",
                    player.experienceLevel, player.experienceProgress, xpToAdd, xp);
                throw new IllegalStateException("xp <= 0 in addPlayerXp.");
            }

            player.giveExperiencePoints(xp);
            xpToAdd -= xp;
        }
    }

    private void removePlayerXp(Player player, long experience) {
        if (experience < 0) {
            throw new IllegalArgumentException("experience cannot be negative");
        }

        // Convert to volume
        long volume = experience * ExperienceUtil.EXP_TO_FLUID;

        // Reduce to int safely, and remove any fluid that will not make the conversion
        int cappedVolume = (int) Math.min(Integer.MAX_VALUE, volume);
        cappedVolume = cappedVolume - cappedVolume % ExperienceUtil.EXP_TO_FLUID;

        // Determine the fluid to fill with
        Fluid fillFluid = EIOFluids.XP_JUICE.getSource();
        var currentFluid = TANK.getFluid(this);

        if (!currentFluid.isEmpty() && !currentFluid.getFluid().isSame(fillFluid)) {
            fillFluid = currentFluid.getFluid();
        }

        // Add the fluid
        int filled = TANK.fill(this, new FluidStack(fillFluid, cappedVolume), IFluidHandler.FluidAction.EXECUTE);

        // Remove the XP from the player
        // Workaround some floating point problems when adding all the exp at once.
        // If we add it all at once, the experienceProgress gets messed up and then the next extract is wonky.
        int xpToRemove = filled / ExperienceUtil.EXP_TO_FLUID;
        while (xpToRemove > 0) {
            int xp = Mth.clamp((int)Math.floor(player.experienceProgress * ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel)), 0, xpToRemove);

            // If we can't remove the rest of this level's progress, move on.
            if (xp <= 0) {
                xp = Mth.clamp(ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel - 1), 0, xpToRemove);
            }

            if (xp <= 0) {
                LOGGER.error("xp <= 0 in removePlayerXp. experienceLevel: {}, experienceProgress: {}, xpToRemove: {}, xp: {}",
                    player.experienceLevel, player.experienceProgress, xpToRemove, xp);
                throw new IllegalStateException("xp <= 0 in removePlayerXp.");
            }

            player.giveExperiencePoints(-xp);
            xpToRemove -= xp;
        }
    }

}

