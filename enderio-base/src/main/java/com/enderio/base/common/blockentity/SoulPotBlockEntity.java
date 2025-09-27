package com.enderio.base.common.blockentity;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.common.block.SoulCatchingPotBlock;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.machines.common.soulpot.OriginContext;
import com.enderio.machines.common.soulpot.SoulEnvironmentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SoulPotBlockEntity extends BlockEntity {

    @Nullable private Soul caughtEntity;

    private PotDecorations decorations;

    public SoulPotBlockEntity(BlockPos pos, BlockState blockState) {
        super(EIOBlockEntities.SOUL_POT.get(), pos, blockState);
        decorations = PotDecorations.EMPTY;
    }

    @Nullable
    public Soul getCaughtEntity() {
        return caughtEntity;
    }

    public Soul catchEntity() {
        caughtEntity = SoulEnvironmentData
            .findEntity(level.getRandom(), new OriginContext(level, getBlockPos()))
            .map(Soul::of)
            .orElse(null); // TODO: handle no entity gracefully
        return caughtEntity;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (caughtEntity != null)
            tag.put("caught_entity", caughtEntity.save(registries));
        decorations.save(tag);
    }

    public void saveDecorations(CompoundTag tag) {
        decorations.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("caught_entity")) {
            Tag caughtEntityTag = tag.get("caught_entity");
            caughtEntity = Soul.parse(registries, caughtEntityTag).orElse(null);
        }
        decorations = PotDecorations.load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.POT_DECORATIONS, this.decorations);
        components.set(EIODataComponents.SOUL, caughtEntity);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.decorations = componentInput.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
        this.caughtEntity = componentInput.get(EIODataComponents.SOUL);
        if (caughtEntity != null && level != null && getBlockState().hasProperty(SoulCatchingPotBlock.CATCHING_PROPERTY) && getBlockState().getValue(SoulCatchingPotBlock.CATCHING_PROPERTY) != SoulCatchingPotBlock.State.CAUGHT) {

            level.setBlock(getBlockPos(), getBlockState().setValue(SoulCatchingPotBlock.CATCHING_PROPERTY, SoulCatchingPotBlock.State.CAUGHT), Block.UPDATE_ALL);
        }
    }
}
