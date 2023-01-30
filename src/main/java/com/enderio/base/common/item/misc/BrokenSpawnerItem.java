package com.enderio.base.common.item.misc;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.capability.EntityStorage;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BrokenSpawnerItem extends Item implements IMultiCapabilityItem {
    public BrokenSpawnerItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack forType(ResourceLocation type) {
        ItemStack brokenSpawner = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        setEntityType(brokenSpawner, type);
        return brokenSpawner;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (pCategory == getItemCategory()) {
            pItems.add(new ItemStack(this));
        } else if (pCategory == EIOCreativeTabs.SOULS) {
            // Register for every mob that can be captured.
            for (ResourceLocation entity : EntityCaptureUtils.getCapturableEntities()) {
                pItems.add(forType(entity));
            }
        }
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {
        return Arrays.asList(getItemCategory(), EIOCreativeTabs.SOULS);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        getEntityType(pStack).ifPresent(type -> pTooltipComponents.add(TooltipUtil.style(Component.translatable(EntityUtil.getEntityDescriptionId(type)))));
    }

    // region Entity Storage

    public static Optional<ResourceLocation> getEntityType(ItemStack stack) {
        return stack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(storage -> storage.getStoredEntityData().getEntityType()).orElse(Optional.empty());
    }

    private static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        stack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> storage.setStoredEntityData(StoredEntityData.of(entityType)));
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.ENTITY_STORAGE, LazyOptional.of(EntityStorage::new));
        return provider;
    }

    // endregion
}
