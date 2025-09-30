package com.enderio.enderio.api.glider;

import com.enderio.enderio.api.integration.Integration;

public record GliderMovementInfo(double acceleration, double maxSpeed, double fallSpeed, Integration cause) {
}
