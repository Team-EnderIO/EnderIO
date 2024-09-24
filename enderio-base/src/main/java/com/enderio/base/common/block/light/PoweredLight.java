package com.enderio.base.common.block.light;

import com.enderio.base.common.blockentity.PoweredLightBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jetbrains.annotations.Nullable;

/**
 * Class for all power consuming lights.
 * Handles {@code PoweredLightBlockEntity} interactions.
 * Handles "Wireless" subtype.
 */
public class PoweredLight extends Light implements EntityBlock{
    public static final MapCodec<PoweredLight> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(
                Codec.BOOL.fieldOf("inverted").forGetter(i -> i.isInverted),
                Codec.BOOL.fieldOf("wireless").forGetter(i -> i.wireless),
                propertiesCodec()
            )
            .apply(inst, PoweredLight::new)
    );

	public final boolean wireless;

	public PoweredLight(boolean inverted, boolean wireless, Properties properties) {
		super(inverted, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, false).setValue(FACE, AttachFace.WALL));
		this.wireless = wireless;
	}

	public boolean isWireless() {
		return wireless;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return EIOBlockEntities.POWERED_LIGHT.get().create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return createTickerHelper(blockEntityType, EIOBlockEntities.POWERED_LIGHT.get(), PoweredLightBlockEntity::tick);
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
		return typeA == typeE ? (BlockEntityTicker<A>)ticker : null;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.getBlockState(fromPos).is(EIOBlocks.LIGHT_NODE.get())) {
			if(level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light) {
				light.needsUpdate();
			}
		}
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
	}

	@Override
	public void checkPoweredState(Level level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light) {
			if (light.isActive()) {
				super.checkPoweredState(level, pos, state);
				return;
			}
		}
		level.setBlock(pos, state.setValue(ENABLED, false), 3);
	}

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
