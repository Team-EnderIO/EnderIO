package com.enderio.base.client.particle;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRanged {
    @OnlyIn(Dist.CLIENT)
    boolean isShowingRange();

    int getRange();

}
