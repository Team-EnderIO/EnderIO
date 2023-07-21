package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.blockentity.IAutoEquippable;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * A capacitor item that has fixed capacitor data attached.
 */
public class FixedCapacitorItem extends Item implements IMultiCapabilityItem {
    private final ICapacitorData data;

    public FixedCapacitorItem(ICapacitorData data, Properties properties) {
        super(properties);
        this.data = data;
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(EIOCapabilities.CAPACITOR, LazyOptional.of(() -> data));
        return provider;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockEntity(pos) instanceof IAutoEquippable equippable)
            return equippable.tryItemAutoEquip(stack, context);
        return super.onItemUseFirst(stack, context);
    }
}
