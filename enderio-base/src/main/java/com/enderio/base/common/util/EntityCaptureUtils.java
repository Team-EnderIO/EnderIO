package com.enderio.base.common.util;

import com.enderio.base.config.base.BaseConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EntityCaptureUtils {
    // The id of the ender dragon for manual filtering.
    private static final ResourceLocation DRAGON = new ResourceLocation("minecraft", "ender_dragon");

    public static List<ResourceLocation> getCapturableEntities() {
        List<ResourceLocation> entities = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITIES.getValues()) {
            if (type.getCategory() != MobCategory.MISC) {
                ResourceLocation key = ForgeRegistries.ENTITIES.getKey(type);
                if (key != null && !key.equals(DRAGON)) {
                    entities.add(key);
                }
            }
        }
        return entities;
    }

    public enum CapturableStatus {
        CAPTURABLE,
        BOSS,
        BLACKLISTED,
        INCOMPATIBLE,
    }

    public static CapturableStatus getCapturableStatus(Entity entity) {
        EntityType<?> type = entity.getType();

        if (isBlacklistedBoss(entity))
            return CapturableStatus.BOSS;

        if (!type.canSerialize())
            return CapturableStatus.INCOMPATIBLE;

        if (BaseConfig.COMMON.ITEMS.SOUL_VIAL_BLACKLIST.get().contains(type.getRegistryName().toString()))
            return CapturableStatus.BLACKLISTED;

        return CapturableStatus.CAPTURABLE;
    }

    public static boolean isBlacklistedBoss(Entity entity) {
        return EntityUtil.getEntityType(entity).map(entityType -> EntityUtil.isBoss(entity) && !"minecraft".equals(entityType.getNamespace())).orElse(false);
    }
}
