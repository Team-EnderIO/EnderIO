package com.enderio.enderio.compat.jade;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SoulBoundComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
        ITooltip tooltip,
        BlockAccessor accessor,
        IPluginConfig config
    ) {
        var data = accessor.getServerData();
        if (!data.contains("enderio_soulbound")) {
            return;
        }

        ResourceLocation entityTypeId = ResourceLocation.tryParse(data.getString("enderio_soulbound"));
        if (entityTypeId == null) {
            return;
        }

        var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId);
        if (entityType.isEmpty()) {
            return;
        }

        tooltip.add(TooltipUtil.withArgs(EIOCommonLang.BOUND_SOUL, entityType.get().getDescription()));
    }

    @Override
    public ResourceLocation getUid() {
        return EIOJadePlugin.SOUL_BOUND_COMPONENT;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        var soulBindable = blockAccessor.getLevel().getCapability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, blockAccessor.getPosition());
        if (soulBindable != null && soulBindable.hasSoul()) {
            compoundTag.putString("enderio_soulbound", soulBindable.getBoundSoul().entityTypeId().toString());
        }
    }
}
