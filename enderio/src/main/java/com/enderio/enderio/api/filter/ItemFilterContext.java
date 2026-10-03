package com.enderio.enderio.api.filter;

import net.neoforged.neoforge.items.IItemHandler;

public record ItemFilterContext(IItemHandler target, ItemFilterDirection direction) {}
