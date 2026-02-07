package com.enderio.enderio.api.conduits;

import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;

@ApiStatus.Experimental
public class ConduitUtility {
    public static boolean canConnectConduits(Holder<Conduit<?, ?>> conduitA,  Holder<Conduit<?, ?>> conduitB) {
        var type = conduitA.value().type();
        return compare(type, conduitA, conduitB, Conduit::canConnectToConduit);
    }

    public static boolean canConduitReplace(Holder<Conduit<?, ?>> replacementConduit,  Holder<Conduit<?, ?>> conduitToReplace) {
        var type = replacementConduit.value().type();
        return compare(type, replacementConduit, conduitToReplace, Conduit::canReplaceConduit);
    }

    @SuppressWarnings("unchecked")
    private static <TConduit extends Conduit<TConduit, ?>> boolean compare(ConduitType<TConduit, ?> type, Holder<Conduit<?, ?>> a,  Holder<Conduit<?, ?>> b,
        BiPredicate<TConduit, TConduit> predicate) {
        if (a.value().type() != b.value().type()) {
            return false;
        }

        var conduitA = (TConduit)a.value();
        var conduitB = (TConduit)b.value();
        return predicate.test(conduitA, conduitB);
    }
}
