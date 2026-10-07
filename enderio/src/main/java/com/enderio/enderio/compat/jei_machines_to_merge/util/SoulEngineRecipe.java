package com.enderio.enderio.compat.jei_machines_to_merge.util;

import com.enderio.enderio.foundation.datamap.EngineSoul;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public record SoulEngineRecipe(ResourceKey<EntityType<?>> entityType, EngineSoul soulData) {}
