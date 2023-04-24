package net.sonmok14.fromtheshadows.entity;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sonmok14.fromtheshadows.Fromtheshadows;
import net.sonmok14.fromtheshadows.utils.registry.EntityRegistry;

public class FallingBlockEntity extends Entity {
	private static Logger LOGGER;
	private BlockState blockState;
	public int time;
	public boolean dropItem;
	private boolean cancelDrop;
	private boolean hurtEntities;
	private int fallDamageMax;
	private float fallDamagePerDistance;
	@Nullable
	public CompoundTag blockData;
	protected static EntityDataAccessor<BlockPos> DATA_START_POS;

	public FallingBlockEntity(EntityType<FallingBlockEntity> type, Level level) {
		super(type, level);
		blockState = Blocks.SAND.defaultBlockState();
		dropItem = true;
		fallDamageMax = 40;
	}

	public FallingBlockEntity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
		this((EntityType<FallingBlockEntity>) EntityRegistry.FALLING_BLOCK.get(), p_31953_);
		blockState = p_31957_;
		blocksBuilding = true;
		setPos(p_31954_, p_31955_ + (1.0f - getBbHeight()) / 2.0f, p_31956_);
		setDeltaMovement(Vec3.ZERO);
		xo = p_31954_;
		yo = p_31955_;
		zo = p_31956_;
		setStartPos(blockPosition());
	}

	public boolean isAttackable() {
		return false;
	}

	public void setStartPos(BlockPos p_31960_) {
		entityData.set((EntityDataAccessor) FallingBlockEntity.DATA_START_POS, p_31960_);
	}

	public BlockPos getStartPos() {
		return (BlockPos) entityData.get((EntityDataAccessor) FallingBlockEntity.DATA_START_POS);
	}

	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	protected void defineSynchedData() {
		entityData.define((EntityDataAccessor) FallingBlockEntity.DATA_START_POS, BlockPos.ZERO);
	}

	public boolean isPickable() {
		return !this.isRemoved();
	}

	public void tick() {
		if (blockState.isAir()) {
			discard();
		}
		else {
			Block block = blockState.getBlock();
			++this.time;
			if (!this.isNoGravity()) {
				setDeltaMovement(getDeltaMovement().add(0.0, -0.04, 0.0));
			}
			move(MoverType.SELF, getDeltaMovement());
			if (!this.level.isClientSide) {
				BlockPos blockpos = blockPosition();
				boolean flag = blockState.getBlock() instanceof ConcretePowderBlock;
				boolean flag2 = flag && level.getFluidState(blockpos).is(FluidTags.WATER);
				double d0 = getDeltaMovement().lengthSqr();
				if (flag && d0 > 1.0) {
					BlockHitResult blockhitresult = level.clip(new ClipContext(new Vec3(xo, yo, zo), position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
					if (blockhitresult.getType() != HitResult.Type.MISS && level.getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
						blockpos = blockhitresult.getBlockPos();
						flag2 = true;
					}
				}
				if (!this.onGround && !flag2) {
					if (!this.level.isClientSide && ((time > 100 && (blockpos.getY() <= level.getMinBuildHeight() || blockpos.getY() > level.getMaxBuildHeight())) || time > 600)) {
						if (dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							spawnAtLocation(block);
						}
						discard();
					}
				}
				else {
					BlockState blockstate = level.getBlockState(blockpos);
					setDeltaMovement(getDeltaMovement().multiply(0.7, -0.5, 0.7));
					if (!blockstate.is(Blocks.MOVING_PISTON)) {
						if (!this.cancelDrop) {
							boolean flag3 = blockstate.canBeReplaced((BlockPlaceContext) new DirectionalPlaceContext(level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean flag4 = FallingBlock.isFree(level.getBlockState(blockpos.below())) && (!flag || !flag2);
							boolean flag5 = blockState.canSurvive((LevelReader) level, blockpos) && !flag4;
							if (flag3 && flag5) {
								if (blockState.hasProperty((Property) BlockStateProperties.WATERLOGGED) && level.getFluidState(blockpos).getType() == Fluids.WATER) {
									blockState = (BlockState) blockState.setValue((Property) BlockStateProperties.WATERLOGGED, (Comparable) true);
								}
								if (level.setBlock(blockpos, blockState, 3)) {
									((ServerLevel) level).getChunkSource().chunkMap.broadcast(this, (Packet) new ClientboundBlockUpdatePacket(blockpos, level.getBlockState(blockpos)));
									discard();
									if (blockData != null && blockState.hasBlockEntity()) {
										BlockEntity blockentity = level.getBlockEntity(blockpos);
										if (blockentity != null) {
											CompoundTag compoundtag = blockentity.saveWithoutMetadata();
											for (String s : blockData.getAllKeys()) {
												compoundtag.put(s, blockData.get(s).copy());
											}
											try {
												blockentity.load(compoundtag);
											}
											catch (Exception exception) {
												Fromtheshadows.LOGGER.error("Failed to load block entity from falling block", (Throwable) exception);
											}
											blockentity.setChanged();
										}
									}
								}
								else if (dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									discard();
									spawnAtLocation(block);
								}
							}
							else {
								discard();
								if (dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									spawnAtLocation(block);
								}
							}
						}
						else {
							discard();
						}
					}
				}
			}
			setDeltaMovement(getDeltaMovement().scale(0.98));
		}
	}

	public boolean causeFallDamage(float p_149643_, float p_149644_, DamageSource p_149645_) {
		return false;
	}

	protected void addAdditionalSaveData(CompoundTag p_31973_) {
		p_31973_.put("BlockState", (Tag) NbtUtils.writeBlockState(blockState));
		p_31973_.putInt("Time", time);
		p_31973_.putBoolean("DropItem", dropItem);
		p_31973_.putBoolean("HurtEntities", hurtEntities);
		p_31973_.putFloat("FallHurtAmount", fallDamagePerDistance);
		p_31973_.putInt("FallHurtMax", fallDamageMax);
		if (blockData != null) {
			p_31973_.put("TileEntityData", (Tag) blockData);
		}
	}

	protected void readAdditionalSaveData(CompoundTag p_31964_) {
		blockState = NbtUtils.readBlockState(p_31964_.getCompound("BlockState"));
		time = p_31964_.getInt("Time");
		if (p_31964_.contains("HurtEntities", 99)) {
			hurtEntities = p_31964_.getBoolean("HurtEntities");
			fallDamagePerDistance = p_31964_.getFloat("FallHurtAmount");
			fallDamageMax = p_31964_.getInt("FallHurtMax");
		}
		else if (blockState.is(BlockTags.ANVIL)) {
			hurtEntities = true;
		}
		if (p_31964_.contains("DropItem", 99)) {
			dropItem = p_31964_.getBoolean("DropItem");
		}
		if (p_31964_.contains("TileEntityData", 10)) {
			blockData = p_31964_.getCompound("TileEntityData");
		}
		if (blockState.isAir()) {
			blockState = Blocks.SAND.defaultBlockState();
		}
	}

	public void setHurtsEntities(float p_149657_, int p_149658_) {
		hurtEntities = true;
		fallDamagePerDistance = p_149657_;
		fallDamageMax = p_149658_;
	}

	public boolean displayFireAnimation() {
		return false;
	}

	public void fillCrashReportCategory(CrashReportCategory p_31962_) {
		super.fillCrashReportCategory(p_31962_);
		p_31962_.setDetail("Immitating BlockState", blockState.toString());
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public boolean onlyOpCanSetNbt() {
		return true;
	}

	public Packet<?> getAddEntityPacket() {
		return (Packet<?>) new ClientboundAddEntityPacket(this, Block.getId(getBlockState()));
	}

	public void recreateFromPacket(ClientboundAddEntityPacket p_149654_) {
		super.recreateFromPacket(p_149654_);
		blockState = Block.stateById(p_149654_.getData());
		blocksBuilding = true;
		double d0 = p_149654_.getX();
		double d2 = p_149654_.getY();
		double d3 = p_149654_.getZ();
		setPos(d0, d2, d3);
		setStartPos(blockPosition());
	}

	static {
		LOGGER = LogUtils.getLogger();
		DATA_START_POS = SynchedEntityData.defineId(net.minecraft.world.entity.item.FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
	}
}
