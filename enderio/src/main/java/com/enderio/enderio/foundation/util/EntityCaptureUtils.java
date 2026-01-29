package com.enderio.enderio.foundation.util;

import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class EntityCaptureUtils {
    // The id of the ender dragon for manual filtering.
    private static final ResourceLocation DRAGON = ResourceLocation.withDefaultNamespace("ender_dragon");

    @Nullable
    private static List<EntityType<?>> capturableEntityTypes = null;

    public static List<EntityType<?>> getCapturableEntityTypes() {
        if (capturableEntityTypes == null) {
            //noinspection unchecked
            capturableEntityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                    .filter(DefaultAttributes::hasSupplier)
                    .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                    .filter(entityType -> getCapturableStatus(entityType) == CapturableStatus.CAPTURABLE)
                    .filter(entityType -> !BuiltInRegistries.ENTITY_TYPE.getKey(entityType).equals(DRAGON))
                    .collect(Collectors.toList()));
        }

        return capturableEntityTypes;
    }

    public enum CapturableStatus {
        CAPTURABLE(Component.empty()),
        BLACKLISTED(ToolsLang.SOUL_VIAL_ERROR_BLACKLISTED),
        INCOMPATIBLE(ToolsLang.SOUL_VIAL_ERROR_FAILED);

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
     * @return the status on how this entity should be handled for capture
     */
    public static CapturableStatus getCapturableStatus(EntityType<? extends LivingEntity> type) {
        if (!type.canSerialize()) {
            return CapturableStatus.INCOMPATIBLE;
        }

        // Don't allow capturing of the ender dragon
        if (type.equals(EntityType.ENDER_DRAGON)) {
            return CapturableStatus.BLACKLISTED;
        }

        // Whitelist takes precedence over all
        // This allows easier allowing of restricted mobs than removing from tags.
        if (type.is(EIOTags.EntityTypes.SOUL_VIAL_WHITELIST)) {
            return CapturableStatus.CAPTURABLE;
        }

        if (type.is(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST)) {
            return CapturableStatus.BLACKLISTED;
        }

        return CapturableStatus.CAPTURABLE;
    }
}
