package com.enderio.machines.common.blocks.sag_mill;

import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.FloatSlotPayload;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public abstract class GrindingBallDataSyncSlot implements SyncSlot {

    public static GrindingBallDataSyncSlot standalone() {
        return new GrindingBallDataSyncSlot() {
            private GrindingBallData value = GrindingBallData.IDENTITY;

            @Override
            public GrindingBallData get() {
                return value;
            }

            @Override
            public void set(GrindingBallData value) {
                this.value = value;
            }
        };
    }

    public static GrindingBallDataSyncSlot simple(Supplier<GrindingBallData> getter,
            Consumer<GrindingBallData> setter) {
        return new GrindingBallDataSyncSlot() {

            @Override
            public GrindingBallData get() {
                return getter.get();
            }

            @Override
            public void set(GrindingBallData value) {
                setter.accept(value);
            }
        };
    }

    public static GrindingBallDataSyncSlot readOnly(Supplier<GrindingBallData> getter) {
        return new GrindingBallDataSyncSlot() {

            @Override
            public GrindingBallData get() {
                return getter.get();
            }

            @Override
            public void set(GrindingBallData value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private GrindingBallData lastValue;

    public abstract GrindingBallData get();

    public abstract void set(GrindingBallData value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();

        var changeType = Objects.equals(lastValue, currentValue) ? ChangeType.NONE : ChangeType.FULL;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var value = get();

        return new ListSlotPayload(
                List.of(new FloatSlotPayload(value.outputMultiplier()), new FloatSlotPayload(value.bonusMultiplier()),
                        new FloatSlotPayload(value.powerUse()), new IntSlotPayload(value.durability())));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof ListSlotPayload list && list.contents().size() == 4) {
            for (int i = 0; i < 4; i++) {
                if (i < 3 && list.contents().get(i).type() != SlotPayloadType.FLOAT) {
                    return;
                }

                if (i == 3 && list.contents().get(i).type() != SlotPayloadType.INT) {
                    return;
                }
            }

            set(new GrindingBallData(((FloatSlotPayload) list.contents().get(0)).value(),
                    ((FloatSlotPayload) list.contents().get(1)).value(),
                    ((FloatSlotPayload) list.contents().get(2)).value(),
                    ((IntSlotPayload) list.contents().get(3)).value()));
        }
    }
}
