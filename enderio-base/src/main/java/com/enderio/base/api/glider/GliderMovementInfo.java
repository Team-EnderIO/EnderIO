package com.enderio.base.api.glider;

import com.enderio.base.api.integration.Integration;

public record GliderMovementInfo(double acceleration, double maxSpeed, double fallSpeed, Integration cause) {
}
