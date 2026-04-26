package com.enderio.enderio.datagen.client.sounds;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class EIOSoundDefinitionProvider extends SoundDefinitionsProvider {

    public EIOSoundDefinitionProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID);
    }

    @Override
    public void registerSounds() {
        this.add(EIOSounds.STIRLING.get(), definition().subtitle("block.stirling_generator.burn")
            .with(sound(EnderIO.id("block/stirling_generator/burn"))));
        this.add(EIOSounds.SLICER.get(), definition().subtitle("block.slice_and_splice.slice")
            .with(sound(EnderIO.id("block/slice_and_splice/slice"))));
        this.add(EIOSounds.SAG_MILL.get(), definition().subtitle("block.sag_mill.tumble")
            .with(sound(EnderIO.id("block/sag_mill/tumble"))));
        this.add(EIOSounds.SOUL_BINDER.get(), definition().subtitle("block.soul_binder.grind")
            .with(sound(EnderIO.id("block/soul_binder/grind"))));
        this.add(EIOSounds.VAT.get(), definition().subtitle("block.vat.gurgle")
            .with(sound(EnderIO.id("block/vat/gurgle"))));
    }
}
