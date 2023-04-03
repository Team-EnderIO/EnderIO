package com.enderio.machines.common.item;

import com.enderio.EnderIO;
import com.enderio.core.client.item.ItemTooltip;
import com.enderio.machines.client.rendering.item.FluidTankBEWLR;
import com.enderio.machines.common.block.MachineBlock;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FluidTankItem extends BlockItem {

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
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pFlag) {
        ItemTooltip.addShiftKeyMessage(tooltip);
        Optional<IFluidHandlerItem> fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (fluidHandler.isPresent()){
            ItemTooltip.addFluidTankMessage(tooltip, fluidHandler.get().getFluidInTank(0), capacity);//always present
        } else {
            EnderIO.LOGGER.warn("No fluid handler capability on tank item, unable to complete tooltip");
        }

        super.appendHoverText(stack, pLevel, tooltip, pFlag);
    }
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidItemStack(stack, capacity);
    }

    public class FluidItemStack extends FluidHandlerItemStack{
        private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
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
            return tagCompoundOptional.map(tagCompound -> tagCompound.getCompound(BLOCK_ENTITY_TAG))
                .map(beTag -> beTag.getCompound("fluid"))
                .map(FluidStack::loadFluidStackFromNBT)
                .orElse(FluidStack.EMPTY);
        }

        protected void setFluid(FluidStack fluid) {
            CompoundTag mainTag = container.getOrCreateTag();
            CompoundTag beTag = new CompoundTag();
            mainTag.put(BLOCK_ENTITY_TAG, beTag);

            CompoundTag fluidTag = new CompoundTag();
            fluid.writeToNBT(fluidTag);//rewrites the old value
            //TODO: externalize "fluid" into one global parameter.
            beTag.put("fluid", fluidTag);
        }

        @Override
        protected void setContainerToEmpty() {
            CompoundTag tagCompound = container.getTag();
            if(tagCompound != null){
                CompoundTag BETag = tagCompound.getCompound(BLOCK_ENTITY_TAG);
                if (BETag.contains("fluid"))BETag.remove("fluid");
            }
        }
    }
}
