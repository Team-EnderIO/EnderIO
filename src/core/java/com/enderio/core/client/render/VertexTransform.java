package com.enderio.core.client.render;

import com.enderio.core.common.util.vec.ImmutableVector3d;

@FunctionalInterface
public interface VertexTransform {

    ImmutableVector3d apply(ImmutableVector3d vec);
}
