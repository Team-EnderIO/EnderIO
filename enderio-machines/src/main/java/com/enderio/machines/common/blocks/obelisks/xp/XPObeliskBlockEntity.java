package com.enderio.machines.common.blocks.obelisks.xp;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class XPObeliskBlockEntity extends MachineBlockEntity implements FluidTankUser {

    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();

    private static final Logger LOGGER = LogUtils.getLogger();

    public XPObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.XP_OBELISK.get(), worldPosition, blockState, true);
        fluidHandler = createFluidHandler();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new XPObeliskMenu(containerId, playerInventory, this);
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return new MachineTankLayout.Builder()
                .tank(TANK, Integer.MAX_VALUE, fluidStack -> fluidStack.is(EIOTags.Fluids.EXPERIENCE))
                .build();
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
                // TODO: Avoid filling beyond 2,147,483,640 (INT_MAX - 7) to avoid having 7mb
                // that cannot be extracted?

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

    public void addLevelsToPlayer(Player player, int levelsToAdd) {
        long playerExperience = ExperienceUtil.getPlayerTotalXp(player);
        long targetExperience = ExperienceUtil.getTotalXpFromLevel(player.experienceLevel + levelsToAdd);
        addPlayerXp(player, targetExperience - playerExperience);
    }

    public void removeLevelsFromPlayer(Player player, int levelsToRemove) {
        long playerExperience = ExperienceUtil.getPlayerTotalXp(player);
        long targetExperience = ExperienceUtil
                .getTotalXpFromLevel(Math.max(0, player.experienceLevel - levelsToRemove));
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
        // If we add it all at once, the experienceProgress gets messed up and then the
        // next extract is wonky.
        int xpToAdd = drained.getAmount() / ExperienceUtil.EXP_TO_FLUID;
        while (xpToAdd > 0) {
            int xp = Mth.clamp((int) Math.floor(
                    (1 - player.experienceProgress) * ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel)),
                    0, xpToAdd);

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

        // Add the fluid
        int filled = TANK.fill(this, new FluidStack(EIOFluids.XP_JUICE.getSource(), cappedVolume),
                IFluidHandler.FluidAction.EXECUTE);

        // Remove the XP from the player
        // Workaround some floating point problems when adding all the exp at once.
        // If we add it all at once, the experienceProgress gets messed up and then the
        // next extract is wonky.
        int xpToRemove = filled / ExperienceUtil.EXP_TO_FLUID;
        while (xpToRemove > 0) {
            int xp = Mth.clamp(
                    (int) Math.floor(
                            player.experienceProgress * ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel)),
                    0, xpToRemove);

            // If we can't remove the rest of this level's progress, move on.
            if (xp <= 0) {
                xp = Mth.clamp(ExperienceUtil.getXpNeededForNextLevel(player.experienceLevel - 1), 0, xpToRemove);
            }

            if (xp <= 0) {
                LOGGER.error(
                        "xp <= 0 in removePlayerXp. experienceLevel: {}, experienceProgress: {}, xpToRemove: {}, xp: {}",
                        player.experienceLevel, player.experienceProgress, xpToRemove, xp);
                throw new IllegalStateException("xp <= 0 in removePlayerXp.");
            }

            player.giveExperiencePoints(-xp);
            xpToRemove -= xp;
        }
    }

    // region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            var tank = TANK.getTank(this);
            tank.setFluid(storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var tank = TANK.getTank(this);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank.getFluid()));
        }
    }

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
