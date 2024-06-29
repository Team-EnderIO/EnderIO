//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.EnderIO;
//import com.enderio.api.conduit.ConduitData;
//import com.enderio.api.conduit.ConduitDataSerializer;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import mekanism.api.chemical.merged.BoxedChemical;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//
//import java.util.Optional;
//
//public class ChemicalConduitData implements ConduitData<ChemicalConduitData> {
//
//    public final boolean isMultiChemical;
//
//    BoxedChemical lockedChemical = BoxedChemical.EMPTY;
//    boolean shouldReset = false;
//
//    public ChemicalConduitData( boolean shouldReset, boolean isMultiChemical, Optional<BoxedChemical> lockedChemical) {
//        this.shouldReset = shouldReset;
//        this.isMultiChemical = isMultiChemical;
//        this.lockedChemical = lockedChemical.orElse(BoxedChemical.EMPTY);
//    }
//
//    public ChemicalConduitData(boolean isMultiChemical) {this.isMultiChemical = isMultiChemical;}
//
//    @Override
//    public void onConnectTo(ChemicalConduitData otherData) {
//        if (!lockedChemical.isEmpty()) {
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
//    public void applyClientChanges(ChemicalConduitData guiData) {
//        this.shouldReset = guiData.shouldReset;
//    }
//
//    @Override
//    public ConduitDataSerializer<ChemicalConduitData> serializer() {
//        return MekanismIntegration.CHEMICAL_DATA_SERIALIZER.get();
//    }
//
//    @Override
//    public boolean canConnectTo(ChemicalConduitData otherData) {
//        return lockedChemical.isEmpty() || otherData.lockedChemical.isEmpty() || lockedChemical.equals(otherData.lockedChemical);
//    }
//
//    private void setlockedChemical(BoxedChemical lockedChemical) {
//        this.lockedChemical = lockedChemical;
//    }
//
//    public static class Serializer implements ConduitDataSerializer<ChemicalConduitData> {
//        public static MapCodec<ChemicalConduitData> CODEC = RecordCodecBuilder.mapCodec(
//            instance -> instance.group(
//                Codec.BOOL.fieldOf("is_multi_fluid").forGetter(i -> i.isMultiChemical),
//                Codec.BOOL.fieldOf("should_reset").forGetter(i -> i.shouldReset),
//                BoxedChemical.OPTIONAL_CODEC
//                    .optionalFieldOf("locked_fluid")
//                    .forGetter(i -> Optional.of(i.lockedChemical))
//            ).apply(instance, ChemicalConduitData::new)
//        );
//
//        public static StreamCodec<RegistryFriendlyByteBuf, ChemicalConduitData> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.BOOL,
//            i -> i.isMultiChemical,
//            ByteBufCodecs.BOOL,
//            i -> i.shouldReset,
//            ByteBufCodecs.optional(BoxedChemical.OPTIONAL_STREAM_CODEC),
//            i -> Optional.of(i.lockedChemical),
//            ChemicalConduitData::new
//        );
//
//        @Override
//        public MapCodec<ChemicalConduitData> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public StreamCodec<RegistryFriendlyByteBuf, ChemicalConduitData> streamCodec() {
//            return STREAM_CODEC;
//        }
//    }
//}
