package com.enderio.base.common.item.misc;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.util.EntityCaptureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrokenSpawnerItem extends Item {
    public BrokenSpawnerItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack forType(ResourceLocation type) {
        ItemStack brokenSpawner = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        setEntityType(brokenSpawner, type);
        return brokenSpawner;
    }

    public static List<ItemStack> getPossibleStacks() {
        // Register for every mob that can be captured.
        List<ItemStack> items = new ArrayList<>();
        for (ResourceLocation entity : EntityCaptureUtils.getCapturableEntities()) {
            items.add(forType(entity));
        }
        return items;
    }

    // region Entity Storage

    public static Optional<ResourceLocation> getEntityType(ItemStack stack) {
        return stack.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY).entityType();
    }

    private static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        stack.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entityType));
    }

    // endregion
}
