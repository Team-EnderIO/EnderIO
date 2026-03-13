package com.enderio.enderio.client.content.conduits;

// TODO: 1.21.2: ItemTintSource
public class ConduitFacadeColor /*implements BlockColor*//*, ItemColor*/ {

    public static final ConduitFacadeColor INSTANCE = new ConduitFacadeColor();

    private ConduitFacadeColor() {
    }

//    @Override
//    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
//        tintIndex = unmoveTintIndex(tintIndex);
//        if (level != null && pos != null) {
//            BlockEntity entity = level.getBlockEntity(pos);
//            if (entity instanceof ConduitBundleBlockEntity conduitBundleBlock) {
//
//                if (conduitBundleBlock.hasFacade()) {
//                    var facade = conduitBundleBlock.getFacadeBlock();
//
//                    if (ClientFacadeVisibility.areFacadesVisible()) {
//                        int color = Minecraft.getInstance()
//                                .getBlockColors()
//                                .getColor(facade.defaultBlockState(), level, pos, tintIndex);
//                        if (color != -1) {
//                            return color;
//                        }
//                    }
//                }
//            }
//        }
//
//        return 0xFFFFFF;
//    }

//    @Override
//    public int getColor(ItemStack stack, int tintIndex) {
//        var facadeData = stack.get(EIODataComponents.BLOCK_PAINT);
//        if (facadeData != null) {
//            var block = facadeData.paint();
//            return Minecraft.getInstance().getItemColors().getColor(block.asItem().getDefaultInstance(), tintIndex);
//        }
//
//        return 0;
//    }

    public static int moveTintIndex(int original) {
        return -original - 2;
    }

    public static int unmoveTintIndex(int original) {
        if (original > 0) {
            return original;
        } else {
            return -original - 2;
        }
    }
}
