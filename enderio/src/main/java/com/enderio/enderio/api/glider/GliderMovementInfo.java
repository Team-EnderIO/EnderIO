package com.enderio.enderio.api.glider;

import com.enderio.enderio.api.integration.Integration;

// Deprecated until we work out what to do with gliders.
@Deprecated
public record GliderMovementInfo(double acceleration, double maxSpeed, double fallSpeed, Integration cause) {
}
