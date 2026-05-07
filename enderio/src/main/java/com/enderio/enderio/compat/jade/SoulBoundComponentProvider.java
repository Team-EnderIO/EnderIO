package com.enderio.enderio.compat.jade;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

import java.util.Optional;

public enum SoulBoundComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
        ITooltip tooltip,
        BlockAccessor accessor,
        IPluginConfig config
    ) {
        Optional<EntityType<?>> entityType = SoulBoundServerDataProvider.INSTANCE.decodeFromData(accessor);
        if (entityType.isEmpty()) {
            return;
        }

        Element icon = JadeUI.smallItem(SoulVialItem.forSoul(Soul.of(entityType.get())));
        tooltip.add(icon);
        tooltip.append(TooltipUtil.withArgs(EIOCommonLang.BOUND_SOUL, entityType.get().getDescription()));
    }

    @Override
    public Identifier getUid() {
        return EIOJadePlugin.SOUL_BOUND_COMPONENT;
    }
}
