package com.enderio.enderio.compat.guideme;

import com.enderio.enderio.EnderIO;
import guideme.Guide;

public class GuideMECompat {
    public static void init() {
        var guide = Guide.builder(EnderIO.rl("guide")).folder("enderioguide").build();
    }
}
