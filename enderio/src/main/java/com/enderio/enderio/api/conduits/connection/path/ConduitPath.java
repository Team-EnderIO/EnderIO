package com.enderio.enderio.api.conduits.connection.path;

import java.util.Map;

public record ConduitPath(int length, Map<ConnectionPathProperty<?>, ?> properties) {}
