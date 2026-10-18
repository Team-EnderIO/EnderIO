package com.enderio.enderio.foundation.util;

import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EntityCaptureUtils {
    @Nullable
    private static List<EntityType<?>> capturableEntityTypes = null;

    public static List<EntityType<?>> getCapturableEntityTypes() {
        if (capturableEntityTypes == null) {
            capturableEntityTypes = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(entityType -> getCapturableStatus(entityType) == CapturableStatus.CAPTURABLE)
                .toList();
        }

        return capturableEntityTypes;
    }

    public enum CapturableStatus {
        CAPTURABLE(Component.empty()),
        DENIED(ToolsLang.SOUL_VIAL_ERROR_DENIED),
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
    public static CapturableStatus getCapturableStatus(EntityType<?> type) {
        // Living entity types register default attributes; display entities, projectiles, etc. do not.
        if (!type.canSerialize() || !DefaultAttributes.hasSupplier(type)) {
            return CapturableStatus.INCOMPATIBLE;
        }

        // Don't allow capturing of the ender dragon
        if (type.equals(EntityTypes.ENDER_DRAGON)) {
            return CapturableStatus.DENIED;
        }

        // Whitelist takes precedence over all
        // This allows easier allowing of restricted mobs than removing from tags.
        if (type.builtInRegistryHolder().is(EIOTags.EntityTypes.SOUL_VIAL_ALLOY_LIST)) {
            return CapturableStatus.CAPTURABLE;
        }

        if (type.builtInRegistryHolder().is(EIOTags.EntityTypes.SOUL_VIAL_DENY_LIST)) {
            return CapturableStatus.DENIED;
        }

        return CapturableStatus.CAPTURABLE;
    }
}
