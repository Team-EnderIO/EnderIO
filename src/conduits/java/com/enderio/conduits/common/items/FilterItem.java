package com.enderio.conduits.common.items;
import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.ConduitFilter;
import com.enderio.conduits.common.init.ConduitMenus;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilterItem extends Item implements IMultiCapabilityItem {
    private final int size;

    public static FilterItem basic() {
        return new FilterItem(9*2);
    }
    public static FilterItem large() {
        return new FilterItem(9*4);
    }

    public FilterItem(int size) {
        super(new Properties().stacksTo(64));
        this.size = size;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltip, tooltipFlag);

        if (Screen.hasShiftDown()) {
            stack.getCapability(EIOCapabilities.ITEM_FILTER)
                .ifPresent(filterCap -> {
                    // world's worst nesting
                    tooltip.add(Component.literal("Ignore Mode: ")
                        .append(filterCap.getIgnoreMode() ?
                            Component.translatable("options.on").withStyle(ChatFormatting.GREEN)
                            : Component.translatable("options.off").withStyle(ChatFormatting.RED)));
                    filterCap.getItems().forEach(stack1 -> tooltip.add(stack1.getHoverName()));
                });
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide()) {
            ConduitMenus.CONDUIT_FILTER_MENU.open((ServerPlayer) player,
                stack.getDisplayName(),
                buf -> buf.writeBoolean(usedHand == InteractionHand.MAIN_HAND));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public @Nullable MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(EIOCapabilities.ITEM_FILTER, LazyOptional.of(() -> new ConduitFilter(stack, size)));
        return provider;
    }
}
