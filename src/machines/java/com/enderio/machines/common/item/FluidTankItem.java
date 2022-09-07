package com.enderio.machines.common.item;

import com.enderio.base.common.capability.FluidHandlerBlockItemStack;
import com.enderio.machines.client.rendering.item.FluidTankBEWLR;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FluidTankItem extends BlockItem {
    public static class Standard extends FluidTankItem {
        public Standard(Block block, Properties properties) {
            super(block, properties, FluidTankBlockEntity.Standard.CAPACITY);
        }
    }

    public static class Enhanced extends FluidTankItem {
        public Enhanced(Block block, Properties properties) {
            super(block, properties, FluidTankBlockEntity.Enhanced.CAPACITY);
        }
    }

    private int capacity;

    protected FluidTankItem(Block block, Properties properties, int capacity) {
        super(block, properties);

        this.capacity = capacity;
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        getTankCap(stack).ifPresent(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack fluidStack = handler.getFluidInTank(i);
                Component postFix = fluidStack.isEmpty() ? Component.literal("")
                        : Component.literal(" ").append(fluidStack.getDisplayName());
                components.add(
                        Component.literal(fluidStack.getAmount() + " / " + handler.getTankCapacity(i)).append(postFix));
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerBlockItemStack(stack, capacity);
    }

    private Optional<IFluidHandlerItem> getTankCap(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
    }
}
