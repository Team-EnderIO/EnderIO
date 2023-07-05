package com.enderio.machines.common.item;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.rendering.item.FluidTankBEWLR;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.MachineBlock;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FluidTankItem extends BlockItem implements IAdvancedTooltipProvider {

    protected final int capacity;

    public FluidTankItem(MachineBlock block, Properties properties, int capacity) {
        super(block, properties);
        this.capacity = capacity;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;//TODO: when fluid tank entity accepts item stacks of more than 1 in the internalDrain/fill. Remove this method to allow fluid tank items to stack to 64.
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            // Minecraft can be null during datagen
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> FluidTankBEWLR.INSTANCE);

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(iFluidHandlerItem -> {
            if (iFluidHandlerItem instanceof FluidItemStack fluidHandler) {
                if (fluidHandler.getFluid().isEmpty()) {
                    tooltips.add(TooltipUtil.style(EIOLang.TANK_EMPTY_STRING));
                } else {
                    tooltips.add(TooltipUtil.styledWithArgs(EIOLang.FLUID_TANK_TOOLTIP, fluidHandler.getFluid().getAmount(), capacity,
                        fluidHandler.getFluid().getFluid().getFluidType().getDescription().getString()));
                }
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidItemStack(stack, capacity);
    }

    public class FluidItemStack extends FluidHandlerItemStack {

        /**
         * @param container The container itemStack, data is stored on it directly as NBT.
         * @param capacity  The maximum capacity of this fluid tank.
         */
        public FluidItemStack(@NotNull ItemStack container, int capacity) {
            super(container, capacity);
        }

        @NotNull
        public FluidStack getFluid() {
            Optional<CompoundTag> tagCompoundOptional = Optional.ofNullable(container.getTag());
            return tagCompoundOptional
                .map(tagCompound -> tagCompound.getCompound(BLOCK_ENTITY_TAG))
                .map(blockEntityTag -> blockEntityTag.getCompound(MachineNBTKeys.FLUID))
                .map(FluidStack::loadFluidStackFromNBT)
                .orElse(FluidStack.EMPTY);
        }

        protected void setFluid(FluidStack fluid) {
            CompoundTag mainTag = container.getOrCreateTag();
            CompoundTag blockEntityTag = new CompoundTag();
            mainTag.put(BLOCK_ENTITY_TAG, blockEntityTag);

            CompoundTag fluidTag = new CompoundTag();
            fluid.writeToNBT(fluidTag);//rewrites the old value
            blockEntityTag.put(MachineNBTKeys.FLUID, fluidTag);
        }

        @Override
        protected void setContainerToEmpty() {
            CompoundTag tagCompound = container.getTag();
            if (tagCompound != null) {
                CompoundTag blockEntityTag = tagCompound.getCompound(BLOCK_ENTITY_TAG);
                if (blockEntityTag.contains(MachineNBTKeys.FLUID))
                    blockEntityTag.remove(MachineNBTKeys.FLUID);
            }
        }
    }
}
