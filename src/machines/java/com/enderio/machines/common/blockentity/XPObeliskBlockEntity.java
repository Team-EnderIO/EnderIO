package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.menu.XPObeliskMenu;
import com.enderio.machines.common.network.PlayerExperiencePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class XPObeliskBlockEntity extends MachineBlockEntity {

    IntegerNetworkDataSlot xpTankDataSlot;
    public XPObeliskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        this.xpTankDataSlot = new IntegerNetworkDataSlot(
            () -> getFluidTankNN().getFluidAmount(), this::updateTankContents
        );
        addDataSlot(xpTankDataSlot);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new XPObeliskMenu(this, playerInventory, containerId, player);
    }

    @Override
    protected @Nullable FluidTank createFluidTank() {
        // What happens when some other fluid with same tag is inserted ?
        return new FluidTank(Integer.MAX_VALUE, fluidStack -> fluidStack.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    public void addLevelToPlayer(int levelDiff, Player player){
        int requestedLevel = player.experienceLevel + levelDiff;
        requestedLevel = Math.max(requestedLevel, 0);
        int playerXP = ExperienceUtil.getPlayerTotalXp(player);
        int requestedXP = ExperienceUtil.getExpFromLevel(requestedLevel) - playerXP;
        int storedXP = getFluidTankNN().getFluidAmount() / ExperienceUtil.EXP_TO_FLUID;

        int awardXP = levelDiff > 0 ? Math.min(storedXP, requestedXP) : requestedXP;
        // send update player xp packet
        CoreNetwork.sendToServer(new PlayerExperiencePacket(player.getUUID(), awardXP));

        int fluidUpdate = -awardXP * ExperienceUtil.EXP_TO_FLUID; // negative to perform reverse action on tank
        if(level.isClientSide){
            clientUpdateSlot(xpTankDataSlot, fluidUpdate);
        }
    }

    private void updateTankContents(int amount) {
        if (amount > 0){
            getFluidTankNN().fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), amount), IFluidHandler.FluidAction.EXECUTE);
        } else {
            getFluidTankNN().drain(new FluidStack(EIOFluids.XP_JUICE.getSource(), -amount), IFluidHandler.FluidAction.EXECUTE);
        }
    }


}
