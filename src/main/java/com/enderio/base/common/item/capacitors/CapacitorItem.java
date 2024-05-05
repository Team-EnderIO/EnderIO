package com.enderio.base.common.item.capacitors;

import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.blockentity.IMachineInstall;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

/**
 * Base class for all capacitor items for common functionality
 */
public class CapacitorItem extends Item {
    public CapacitorItem(Properties properties) {
        super(properties);
    }

    // TODO: Once the old FixedLootCapacitor stuff is thrown, just use the data component.
    public static final ICapabilityProvider<ItemStack, Void, ICapacitorData> CAPACITOR_DATA_PROVIDER
        = (stack, ctx) -> stack.get(EIODataComponents.CAPACITOR_DATA);

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockEntity(pos) instanceof IMachineInstall equippable) {
            return equippable.tryItemInstall(stack, context);
        }

        return super.onItemUseFirst(stack, context);
    }
}
