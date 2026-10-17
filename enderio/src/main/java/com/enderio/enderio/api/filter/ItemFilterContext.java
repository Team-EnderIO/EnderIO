package com.enderio.enderio.api.filter;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record ItemFilterContext(ResourceHandler<ItemResource> target, ItemFilterDirection direction) {}
