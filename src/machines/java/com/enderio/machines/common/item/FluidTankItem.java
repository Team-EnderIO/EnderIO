package com.enderio.machines.common.item;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.rendering.item.FluidTankBEWLR;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.MachineBlock;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.util.NonNullLazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FluidTankItem extends BlockItem implements IAdvancedTooltipProvider {

    public static final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER =
        (stack, v) -> new FluidItemStack(stack, ((FluidTankItem)stack.getItem()).capacity);

    protected final int capacity;

    public FluidTankItem(MachineBlock block, Properties properties, int capacity) {
        super(block, properties);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
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
        var fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            if (fluidHandler instanceof FluidItemStack itemFluidHandler) {
                if (itemFluidHandler.getFluid().isEmpty()) {
                    tooltips.add(TooltipUtil.style(EIOLang.TANK_EMPTY_STRING));
                } else {
                    tooltips.add(TooltipUtil.styledWithArgs(EIOLang.FLUID_TANK_TOOLTIP, itemFluidHandler.getFluid().getAmount(), capacity,
                        itemFluidHandler.getFluid().getFluid().getFluidType().getDescription().getString()));
                }
            }
        }
    }

    public static class FluidItemStack extends FluidHandlerItemStack {

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
                .map(blockEntityTag -> blockEntityTag.getCompound(MachineNBTKeys.FLUIDS))
                .map(fluidTag -> fluidTag.getList("Tanks", Tag.TAG_COMPOUND))
                .map(tank -> tank.getCompound(0))
                .map(FluidStack::loadFluidStackFromNBT)
                .orElse(FluidStack.EMPTY);
        }

        protected void setFluid(FluidStack fluid) {
            CompoundTag mainTag = container.getOrCreateTag();
            CompoundTag blockEntityTag = new CompoundTag();
            mainTag.put(BLOCK_ENTITY_TAG, blockEntityTag);

            CompoundTag fluidTag = new CompoundTag();
            fluid.writeToNBT(fluidTag);//rewrites the old value
            ListTag listTag = new ListTag();
            listTag.add(0, fluidTag);
            CompoundTag tanks = new CompoundTag();
            tanks.put("Tanks", listTag);
            blockEntityTag.put(MachineNBTKeys.FLUIDS, tanks);
        }

        @Override
        protected void setContainerToEmpty() {
            CompoundTag tagCompound = container.getTag();
            if (tagCompound != null) {
                CompoundTag blockEntityTag = tagCompound.getCompound(BLOCK_ENTITY_TAG);
                if (blockEntityTag.contains("Tanks")) {
                    blockEntityTag.remove("Tanks");
                }
            }
        }
    }
}
