package com.enderio.api.glider;

import com.enderio.api.integration.Integration;

public record GliderMovementInfo(double acceleration, double maxSpeed, double fallSpeed, Integration cause) {
}
