package com.enderio.machines.common.block;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.MachineBlockEntities;
import com.enderio.machines.common.data.LootTableUtils;
import com.enderio.machines.data.model.MachineModelBuilder;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Function;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f,8))
        .loot(LootTableUtils::copyNBT)
        .blockstate((ctx, prov) -> {
            prov.horizontalBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
                .customLoader(MachineModelBuilder::begin)
                .component(EIOMachines.loc("block/machine_frame"))
                .component(EIOMachines.loc("block/placeholder_machine_front"))
                .end());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ENCHANTER = REGISTRATE
        .block("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER))
        .properties(props -> props.strength(2.5f,8))
        .loot(LootTableUtils::copyNBT)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> SIMPLE_POWERED_FURNACE = REGISTRATE
        .block("simple_powered_furnace", props -> new MachineBlock(props, MachineBlockEntities.SIMPLE_POWERED_FURNACE))
        .properties(props -> props.strength(2.5f,8))
        .loot(LootTableUtils::copyNBT)
        .blockstate((ctx, prov) -> {
            prov.horizontalBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
                .customLoader(MachineModelBuilder::begin)
                .component(EIOMachines.loc("block/simple_machine_frame"))
                .component(EIOMachines.loc("block/simple_placeholder_machine_front"))
                .end());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .block("simple_alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.SIMPLE_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f,8))
        .loot(LootTableUtils::copyNBT)
        .blockstate((ctx, prov) -> {
            // TODO: Common model utils class
            Function<Boolean, ModelFile> getModel = (on) -> prov.models().getBuilder(ctx.getName() + (on ? "_on" : ""))
                .customLoader(MachineModelBuilder::begin)
                .component(EIOMachines.loc("block/simple_machine_frame"))
                .component(EIOMachines.loc("block/simple_alloy_smelter_front" + (on ? "_on" : "")))
                .end();

            prov.getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(getModel.apply(state.getValue(ProgressMachineBlock.POWERED)))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build()
                );
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ALLOY_SMELTER = REGISTRATE
        .block("alloy_smelter", props -> new MachineBlock(props, MachineBlockEntities.ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f,8))
        .loot(LootTableUtils::copyNBT)
        .blockstate((ctx, prov) -> {
            prov.horizontalBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
                .customLoader(MachineModelBuilder::begin)
                .component(EIOMachines.loc("block/machine_frame"))
                .component(EIOMachines.loc("block/alloy_smelter_front"))
                .end());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static void register() {}
}
