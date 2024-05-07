//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.EnderIO;
//import com.enderio.api.conduit.IExtendedConduitData;
//import mekanism.api.chemical.merged.BoxedChemical;
//import net.minecraft.nbt.CompoundTag;
//
//public class ChemicalExtendedData implements IExtendedConduitData<ChemicalExtendedData> {
//
//    public final boolean isMultiChemical;
//
//    BoxedChemical lockedChemical = BoxedChemical.EMPTY;
//    boolean shouldReset = false;
//
//    public ChemicalExtendedData(boolean isMultiChemical) {this.isMultiChemical = isMultiChemical;}
//
//    @Override
//    public void onConnectTo(ChemicalExtendedData otherData) {
//        if (lockedChemical != null) {
//            if (!otherData.lockedChemical.isEmpty() && !lockedChemical.equals(otherData.lockedChemical)) {
//                EnderIO.LOGGER.warn("incompatible chemical conduits merged");
//            }
//            otherData.setlockedChemical(lockedChemical);
//        } else if (!otherData.lockedChemical.isEmpty()) {
//            setlockedChemical(otherData.lockedChemical);
//        }
//    }
//
//    @Override
//    public boolean canConnectTo(ChemicalExtendedData otherData) {
//        return lockedChemical.isEmpty() || otherData.lockedChemical.isEmpty() || lockedChemical.equals(otherData.lockedChemical);
//    }
//
//    // region Serialization
//
//    private static final String SHOULD_RESET = "ShouldReset";
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag nbt = new CompoundTag();
//        if (!isMultiChemical) {
//            if (lockedChemical.isEmpty()) {
//                lockedChemical.write(nbt);
//            }
//        }
//        return nbt;
//    }
//    @Override
//    public CompoundTag serializeRenderNBT() {
//        return serializeNBT();
//    }
//
//    @Override
//    public CompoundTag serializeGuiNBT() {
//        CompoundTag nbt = serializeNBT();
//        nbt.putBoolean(SHOULD_RESET, shouldReset);
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        setlockedChemical(BoxedChemical.read(nbt));
//        if (nbt.contains(SHOULD_RESET)) {
//            shouldReset = nbt.getBoolean(SHOULD_RESET);
//        }
//    }
//
//    // endregion
//
//    private void setlockedChemical(BoxedChemical lockedChemical) {
//        this.lockedChemical = lockedChemical;
//    }
//}
