package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.types.SimpleConduitType;
import mekanism.common.capabilities.Capabilities;

public class ChemicalConduitType extends SimpleConduitType<ChemicalExtendedData> {
    public static final IConduitMenuData MENU_DATA = new IConduitMenuData.Simple(false, false, false, true, true, true);

    public ChemicalConduitType() {
        super(EnderIO.loc("block/conduit/chemical"), new ChemicalTicker(1000, Capabilities.GAS.block(), Capabilities.SLURRY.block(), Capabilities.INFUSION.block(), Capabilities.PIGMENT.block()),
            () -> new ChemicalExtendedData(false), EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, 120), MENU_DATA);
    }
}
