package com.enderio.base.common.item.tool;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.AcceptingFluidItemHandler;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ColdFireIgniter extends Item implements IMultiCapabilityItem {

    public ColdFireIgniter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Optional<IFluidHandlerItem> fluidCap = getTankCap(itemstack);
        if (fluidCap.isPresent()) {
            IFluidHandlerItem cap = fluidCap.get();
            if (cap.drain(10, IFluidHandler.FluidAction.SIMULATE).getAmount() == 10) {
                cap.drain(10, IFluidHandler.FluidAction.EXECUTE);
                Player player = context.getPlayer();
                Level level = context.getLevel();
                BlockPos blockpos = context.getClickedPos().relative(context.getClickedFace());
                BlockState coldFireState = EIOBlocks.COLD_FIRE.get().getStateForPlacement(new BlockPlaceContext(context));
                if (coldFireState.canSurvive(context.getLevel(), blockpos)) {
                    level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                    level.setBlock(blockpos, coldFireState, Block.UPDATE_ALL_IMMEDIATE);
                    level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return InteractionResult.FAIL;
        }
        LogManager.getLogger().warn("could not find FluidCapability on ColdFireIgniter");
        return super.useOn(context);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getTankCap(stack).isPresent();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x99BD42;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        Optional<IFluidHandlerItem> tankCap = getTankCap(stack);
        if (tankCap.isPresent()) {
            IFluidHandlerItem fluidHandler = tankCap.get();
            return Math.round(fluidHandler.getFluidInTank(0).getAmount() * 13f / fluidHandler.getTankCapacity(0));
        }
        return super.getBarWidth(stack);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        getTankCap(stack).ifPresent(handler -> {
            boolean isOneTank = handler.getTanks() == 1;
            if (!isOneTank) {
                components.add(new TextComponent("Fluids:"));
            }
            for (int i = 0; i < handler.getTanks(); i++) {
                String prefix = isOneTank ? "" : i + ": ";
                Component postFix = handler.getFluidInTank(i).isEmpty() ? new TextComponent("") : handler.getFluidInTank(i).getDisplayName();
                components.add(new TextComponent(prefix + handler.getFluidInTank(i).getAmount() + " / " + handler.getTankCapacity(i) + " ").append(postFix));
            }
        });
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (allowdedIn(pCategory)) {
            ItemStack is = new ItemStack(this);
            pItems.add(is.copy());

            getTankCap(is).ifPresent(handler -> {
                if (handler instanceof AcceptingFluidItemHandler fluidHandler) {
                    fluidHandler.setFluid(new FluidStack(EIOFluids.VAPOR_OF_LEVITY.get(), handler.getTankCapacity(0)));
                    pItems.add(is);
                }
            });
        }
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSimple(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
            new AcceptingFluidItemHandler(stack, 1000, EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL)
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
        return provider;
    }

    private Optional<IFluidHandlerItem> getTankCap(ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
    }
}
