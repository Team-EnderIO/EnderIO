package com.enderio.enderio.content.machines.obelisks.xp;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class XPObeliskBlockEntity extends MachineBlockEntity {

    public static final ICapabilityProvider<XPObeliskBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout<XPObeliskBlockEntity> FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.<XPObeliskBlockEntity>builder()
            .storageSlot(TANK_SLOT, slot -> slot
                .capacity(Integer.MAX_VALUE)
                .filter((index, resource, obelisk) -> resource.getFluid().is(Tags.Fluids.EXPERIENCE)))
            .build();

    private final FluidStorage<XPObeliskBlockEntity> fluidStorage;

    private static final Logger LOGGER = LogUtils.getLogger();

    public XPObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.XP_OBELISK.get(), worldPosition, blockState, true);

        fluidStorage = new FluidStorage<>(FLUID_STORAGE_LAYOUT, this) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                setChanged();
            }

            @Override
            public int insert(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                // Convert into XP Juice - allow any XP fluid type to be inserted but normalize to the current fluid
                if (isValid(index, resource)) {
                    var currentFluid = getResource(index);
                    if (currentFluid.getFluid() == Fluids.EMPTY || resource.getFluid().isSame(currentFluid.getFluid())) {
                        return super.insert(index, resource, amount, transaction);
                    } else {
                        // Insert the same amount but as the current fluid type
                        return super.insert(index, currentFluid, amount, transaction);
                    }
                }

                // Non-XP is not allowed.
                return 0;
            }
        };
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

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }

    public int getFluidAmount() {
        return fluidStorage.getAmountAsInt(TANK_SLOT);
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
        long experienceToGive = getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;
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

        // Get current fluid type
        FluidStack currentFluid = getStoredFluid();
        if (currentFluid.isEmpty()) {
            return;
        }

        // Drain the fluid
        int drained;
        try (Transaction transaction = Transaction.openRoot()) {
            drained = fluidStorage.internalExtract(TANK_SLOT, FluidResource.of(currentFluid.getFluid()), cappedVolume, transaction);
            transaction.commit();
        }

        // Add the XP to the player
        // Workaround some floating point problems when adding all the exp at once.
        // If we add it all at once, the experienceProgress gets messed up and then the
        // next extract is wonky.
        int xpToAdd = drained / ExperienceUtil.EXP_TO_FLUID;
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

        // Determine the fluid to fill with
        Fluid fillFluid = EIOFluids.XP_JUICE.source().get();
        FluidStack currentFluid = getStoredFluid();

        if (!currentFluid.isEmpty() && !currentFluid.getFluid().isSame(fillFluid)) {
            fillFluid = currentFluid.getFluid();
        }

        // Add the fluid
        int filled;
        try (Transaction transaction = Transaction.openRoot()) {
            filled = fluidStorage.internalInsert(TANK_SLOT, FluidResource.of(fillFluid), cappedVolume, transaction);
            transaction.commit();
        }

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
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK_SLOT, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("Fluid", fluidStorage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid")
            .ifPresent(fluidStorage::deserialize);
    }

    // endregion
}
