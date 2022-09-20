package com.enderio.core.client.render;

import com.enderio.core.common.util.vec.EnderVector3d;

@FunctionalInterface
public interface VertexTransform {

    EnderVector3d apply(EnderVector3d vec);
}
