package com.enderio.base.common.util;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.util.EntityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityCaptureUtils {
    // The id of the ender dragon for manual filtering.
    private static final ResourceLocation DRAGON = new ResourceLocation("minecraft", "ender_dragon");

    public static List<ResourceLocation> getCapturableEntities() {
        List<ResourceLocation> entities = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (getCapturableStatus(type, null) == CapturableStatus.CAPTURABLE) {
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type);
                if (key != null && !key.equals(DRAGON)) {
                    entities.add(key);
                }
            }
        }
        return entities;
    }

    public enum CapturableStatus {
        CAPTURABLE(Component.empty()),
        BOSS(EIOLang.SOUL_VIAL_ERROR_BOSS),
        BLACKLISTED(EIOLang.SOUL_VIAL_ERROR_BLACKLISTED),
        INCOMPATIBLE(EIOLang.SOUL_VIAL_ERROR_FAILED);

        CapturableStatus(Component errorMessage) {
            this.errorMessage = errorMessage;
        }

        private final Component errorMessage;

        public Component errorMessage() {
            return errorMessage;
        }
    }

    /**
     * @param type EntityType to be checked
     * @param entity The specific entity to be used or null if general information is wanted
     * @return the status on how this entity should be handled for capture
     */
    public static CapturableStatus getCapturableStatus(EntityType<?> type, @Nullable Entity entity) {

        if (entity != null && isBlacklistedBoss(entity)) //Do we keep this special case?
            return CapturableStatus.BOSS;

        if (!type.canSerialize() || type.getCategory() == MobCategory.MISC)
            return CapturableStatus.INCOMPATIBLE;

        if (type.is(EIOTags.Entitytypes.SOUL_VIAL_BLACKLIST))
            return CapturableStatus.BLACKLISTED;

        return CapturableStatus.CAPTURABLE;
    }

    public static boolean isBlacklistedBoss(Entity entity) {
        return EntityUtil.getEntityTypeRL(entity)
            .map(resourceLocation ->
                EntityUtil.isBoss(entity)
                && !"minecraft".equals(resourceLocation.getNamespace()))
            .orElse(false);
    }
}
