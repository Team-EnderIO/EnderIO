package com.enderio.machines.common.item;

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
        return 1;//TODO: fix madness with tanks that stack.
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

        ItemTooltip.addFluidTankMessage(tooltip, fluidHandler.get().getFluidInTank(0));//always present
        super.appendHoverText(stack, pLevel, tooltip, pFlag);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
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
            CompoundTag tagCompound = container.getTag();
            if(tagCompound == null) return FluidStack.EMPTY;
            CompoundTag BETag = tagCompound.getCompound(BLOCK_ENTITY_TAG);
            if (!BETag.contains("fluid")) return FluidStack.EMPTY;
            else return FluidStack.loadFluidStackFromNBT(BETag.getCompound("fluid"));
        }
        protected void setFluid(FluidStack fluid)
        {
            CompoundTag mainTag = container.getTag();
            if (mainTag == null){
                mainTag = new CompoundTag();
                container.setTag(mainTag);//main tag
            }
            CompoundTag BETag = new CompoundTag();
            mainTag.put(BLOCK_ENTITY_TAG, BETag);

            CompoundTag fluidTag = new CompoundTag();
            fluid.writeToNBT(fluidTag);//rewrites the old value
            //TODO: externalize "fluid" into one global parameter.
            BETag.put("fluid", fluidTag);
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
