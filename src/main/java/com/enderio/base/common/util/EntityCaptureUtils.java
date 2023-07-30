package com.enderio.base.common.util;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.util.EntityUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityCaptureUtils {
    // The id of the ender dragon for manual filtering.
    private static final ResourceLocation DRAGON = new ResourceLocation("minecraft", "ender_dragon");

    @Nullable
    private static List<ResourceLocation> capturableEntities = null;

    public static List<ResourceLocation> getCapturableEntities() {
        if (capturableEntities == null) {
            //noinspection unchecked
            var livingEntities = ImmutableList.copyOf(
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                    .filter(DefaultAttributes::hasSupplier)
                    .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                    .collect(Collectors.toList()));

            List<ResourceLocation> entities = new ArrayList<>();
            for (EntityType<? extends LivingEntity> type : livingEntities) {
                if (getCapturableStatus(type, null) == CapturableStatus.CAPTURABLE) {
                    ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type);
                    if (key != null && !key.equals(DRAGON)) {
                        entities.add(key);
                    }
                }
            }

            capturableEntities = ImmutableList.copyOf(entities);
        }

        return capturableEntities;
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
    public static CapturableStatus getCapturableStatus(EntityType<? extends LivingEntity> type, @Nullable Entity entity) {

        if (entity != null && isBlacklistedBoss(entity)) //Do we keep this special case?
            return CapturableStatus.BOSS;

        if (!type.canSerialize())
            return CapturableStatus.INCOMPATIBLE;

        if (type.is(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST)) {
            return CapturableStatus.BLACKLISTED;
        }

        return CapturableStatus.CAPTURABLE;
    }

    public static boolean isBlacklistedBoss(Entity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }
}
