package net.minecraft.server;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Collections;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BlockRedstoneWire extends Block {

	private List<BlockPosition> turnOff = Lists.newArrayList();
	private List<BlockPosition> turnOn = Lists.newArrayList();
	private final Set<BlockPosition> updatedRedstoneWire = Collections.synchronizedSet(new LinkedHashSet());
	private static final EnumDirection[] facingsVertical = { EnumDirection.DOWN, EnumDirection.UP };
	private static final EnumDirection[] facingsHorizontal = { EnumDirection.WEST, EnumDirection.EAST,
			EnumDirection.NORTH, EnumDirection.SOUTH };

	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> NORTH = BlockStateEnum.of("north",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> EAST = BlockStateEnum.of("east",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> SOUTH = BlockStateEnum.of("south",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> WEST = BlockStateEnum.of("west",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);

	protected static final AxisAlignedBB[] f = new AxisAlignedBB[] {
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D) };

	private boolean g = true;
	private final Set<BlockPosition> B = Sets.newConcurrentHashSet();
	private final Set<BlockPosition> blocksToUpdate = B; // Paper - OBFHELPER
	private static final BaseBlockPosition[] surroundingBlocksOffset;
	private static final EnumDirection[] facings = ArrayUtils.addAll(facingsVertical, facingsHorizontal);

	static {
		Set set = Sets.newLinkedHashSet();
		EnumDirection[] arrayOfEnumDirection1 = facings;
		int i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			EnumDirection facing = arrayOfEnumDirection1[j];
			set.add(facing.m);
		}
		arrayOfEnumDirection1 = facings;
		i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			EnumDirection facing1 = arrayOfEnumDirection1[j];
			BaseBlockPosition v1 = facing1.m;
			EnumDirection[] arrayOfEnumDirection2 = facings;
			int k = arrayOfEnumDirection2.length;
			for (int l = 0; l < k; ++l) {
				EnumDirection facing2 = arrayOfEnumDirection2[l];
				BaseBlockPosition v2 = facing2.m;

				set.add(new BlockPosition(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
			}
		}
		set.remove(BlockPosition.ZERO);
		surroundingBlocksOffset = (BaseBlockPosition[]) set.toArray(new BaseBlockPosition[set.size()]);
	}

	public BlockRedstoneWire() {
		super(Material.ORIENTABLE);
		this.w(this.blockStateList.getBlockData()
				.set(BlockRedstoneWire.NORTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.EAST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.SOUTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.WEST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.POWER, Integer.valueOf(0)));
	}

	@Override
	public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
		return BlockRedstoneWire.f[x(iblockdata.b(iblockaccess, blockposition))];
	}

	private static int x(IBlockData iblockdata) {
		int i = 0;
		boolean flag = iblockdata.get(BlockRedstoneWire.NORTH) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
		boolean flag1 = iblockdata.get(BlockRedstoneWire.EAST) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
		boolean flag2 = iblockdata.get(BlockRedstoneWire.SOUTH) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
		boolean flag3 = iblockdata.get(BlockRedstoneWire.WEST) != BlockRedstoneWire.EnumRedstoneWireConnection.NONE;

		if (flag || flag2 && !flag && !flag1 && !flag3) {
			i |= 1 << EnumDirection.NORTH.get2DRotationValue();
		}

		if (flag1 || flag3 && !flag && !flag1 && !flag2) {
			i |= 1 << EnumDirection.EAST.get2DRotationValue();
		}

		if (flag2 || flag && !flag1 && !flag2 && !flag3) {
			i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
		}

		if (flag3 || flag1 && !flag && !flag2 && !flag3) {
			i |= 1 << EnumDirection.WEST.get2DRotationValue();
		}

		return i;
	}

	@Override
	public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
		iblockdata = iblockdata.set(BlockRedstoneWire.WEST, this.b(iblockaccess, blockposition, EnumDirection.WEST));
		iblockdata = iblockdata.set(BlockRedstoneWire.EAST, this.b(iblockaccess, blockposition, EnumDirection.EAST));
		iblockdata = iblockdata.set(BlockRedstoneWire.NORTH, this.b(iblockaccess, blockposition, EnumDirection.NORTH));
		iblockdata = iblockdata.set(BlockRedstoneWire.SOUTH, this.b(iblockaccess, blockposition, EnumDirection.SOUTH));
		return iblockdata;
	}

	private BlockRedstoneWire.EnumRedstoneWireConnection b(IBlockAccess iblockaccess, BlockPosition blockposition,
			EnumDirection enumdirection) {
		BlockPosition blockposition1 = blockposition.shift(enumdirection);
		IBlockData iblockdata = iblockaccess.getType(blockposition.shift(enumdirection));

		if (!a(iblockaccess.getType(blockposition1), enumdirection)
				&& (iblockdata.l() || !i(iblockaccess.getType(blockposition1.down())))) {
			IBlockData iblockdata1 = iblockaccess.getType(blockposition.up());

			if (!iblockdata1.l()) {
				boolean flag = iblockaccess.getType(blockposition1).q()
						|| iblockaccess.getType(blockposition1).getBlock() == Blocks.GLOWSTONE;

				if (flag && i(iblockaccess.getType(blockposition1.up()))) {
					if (iblockdata.k()) {
						return BlockRedstoneWire.EnumRedstoneWireConnection.UP;
					}

					return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
				}
			}

			return BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
		} else {
			return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
		}
	}

	@Override
	@Nullable
	public AxisAlignedBB a(IBlockData iblockdata, World world, BlockPosition blockposition) {
		return Block.k;
	}

	@Override
	public boolean b(IBlockData iblockdata) {
		return false;
	}

	@Override
	public boolean c(IBlockData iblockdata) {
		return false;
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		return world.getType(blockposition.down()).q()
				|| world.getType(blockposition.down()).getBlock() == Blocks.GLOWSTONE;
	}

	private void calculateCurrentChanges(World world, BlockPosition blockposition) {
		BlockPosition pos;
		IBlockData state;
		int oldPower;
		int blockPower;
		int wirePower;
		int newPower;
		BlockRedstoneEvent event;
		if (world.getType(blockposition).getBlock() == this) {
			this.turnOff.add(blockposition);
		} else {
			checkSurroundingWires(world, blockposition);
		}

		while (!(this.turnOff.isEmpty())) {
			pos = this.turnOff.remove(0);
			state = world.getType(pos);
			oldPower = state.get(POWER).intValue();
			this.g = false;
			blockPower = world.z(pos);
			this.g = true;
			wirePower = getSurroundingWirePower(world, pos);

			--wirePower;
			newPower = Math.max(blockPower, wirePower);

			if (oldPower != newPower) {
				event = new BlockRedstoneEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
						oldPower, newPower);
				world.getServer().getPluginManager().callEvent(event);

				newPower = event.getNewCurrent();
			}

			if (newPower < oldPower) {
				if ((blockPower > 0) && (!(this.turnOn.contains(pos)))) {
					this.turnOn.add(pos);
				}

				state = setWireState(world, pos, state, 0);
			} else if (newPower > oldPower) {
				state = setWireState(world, pos, state, newPower);
			}

			checkSurroundingWires(world, pos);
		}

		while (!(this.turnOn.isEmpty())) {
			pos = this.turnOn.remove(0);
			state = world.getType(pos);
			oldPower = state.get(POWER).intValue();
			this.g = false;
			blockPower = world.z(pos);
			this.g = true;
			wirePower = getSurroundingWirePower(world, pos);

			--wirePower;
			newPower = Math.max(blockPower, wirePower);

			if (oldPower != newPower) {
				event = new BlockRedstoneEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
						oldPower, newPower);
				world.getServer().getPluginManager().callEvent(event);

				newPower = event.getNewCurrent();
			}

			if (newPower > oldPower) {
				state = setWireState(world, pos, state, newPower);
			} else if (newPower < oldPower) {
				;
			}
			checkSurroundingWires(world, pos);
		}
		this.turnOff.clear();
		this.turnOn.clear();
	}

	private int getSurroundingWirePower(World worldIn, BlockPosition pos) {
		int wirePower = 0;
		for (Object element : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
			EnumDirection enumfacing = (EnumDirection) element;
			BlockPosition offsetPos = pos.shift(enumfacing);

			wirePower = getPower(worldIn, offsetPos, wirePower);

			if ((worldIn.getType(offsetPos).l()) && (!(worldIn.getType(pos.up()).l()))) {
				wirePower = getPower(worldIn, offsetPos.up(), wirePower);
			} else if (!(worldIn.getType(offsetPos).l())) {
				wirePower = getPower(worldIn, offsetPos.down(), wirePower);
			}
		}

		return wirePower;
	}

	private IBlockData setWireState(World worldIn, BlockPosition pos, IBlockData state, int power) {
		state = state.set(POWER, Integer.valueOf(power));
		worldIn.setTypeAndData(pos, state, 2);
		this.updatedRedstoneWire.add(pos);
		return state;
	}

	private void checkSurroundingWires(World worldIn, BlockPosition pos) {
		BlockPosition offsetPos;
		IBlockData state = worldIn.getType(pos);
		int ownPower = 0;
		if (state.getBlock() == this) {
			ownPower = state.get(POWER).intValue();
		}

		EnumDirection[] arrayOfEnumDirection1 = facingsHorizontal;
		int i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			EnumDirection facing = arrayOfEnumDirection1[j];
			offsetPos = pos.shift(facing);
			if (facing.k().c()) {
				addWireToList(worldIn, offsetPos, ownPower);
			}
		}

		arrayOfEnumDirection1 = facingsVertical;
		i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			EnumDirection facingVertical = arrayOfEnumDirection1[j];
			offsetPos = pos.shift(facingVertical);
			boolean solidBlock = worldIn.getType(offsetPos).k();
			EnumDirection[] arrayOfEnumDirection2 = facingsHorizontal;
			int k = arrayOfEnumDirection2.length;
			for (int l = 0; l < k; ++l) {
				EnumDirection facingHorizontal = arrayOfEnumDirection2[l];

				if (((facingVertical == EnumDirection.UP) && (!(solidBlock))) || ((facingVertical == EnumDirection.DOWN)
						&& (solidBlock) && (!(worldIn.getType(offsetPos.shift(facingHorizontal)).l())))) {
					addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
				}
			}
		}
	}

	private void addWireToList(World worldIn, BlockPosition pos, int otherPower) {
		IBlockData state = worldIn.getType(pos);
		if (state.getBlock() == this) {
			int power = state.get(POWER).intValue();

			if ((power < otherPower - 1) && (!(this.turnOn.contains(pos)))) {
				this.turnOn.add(pos);
			}

			if ((power > otherPower) && (!(this.turnOff.contains(pos)))) {
				this.turnOff.add(pos);
			}
		}
	}

	private void addAllSurroundingBlocks(BlockPosition pos, Set<BlockPosition> set) {
		BaseBlockPosition[] arrayOfBaseBlockPosition = surroundingBlocksOffset;
		int i = arrayOfBaseBlockPosition.length;
		for (int j = 0; j < i; ++j) {
			BaseBlockPosition vect = arrayOfBaseBlockPosition[j];
			set.add(pos.a(vect));
		}
	}

	private boolean canBlockBePoweredFromSide(IBlockData state, EnumDirection side, boolean isWire) {
		if ((state.getBlock() instanceof BlockPiston) && (state.get(BlockDirectional.FACING) == side.opposite())) {
			return false;
		}

		if ((state.getBlock() instanceof BlockDiodeAbstract)
				&& (state.get(BlockFacingHorizontal.FACING) != side.opposite())) {
			return ((isWire) && (state.getBlock() instanceof BlockRedstoneComparator)
					&& (state.get(BlockFacingHorizontal.FACING).k() != side.k()) && (side.k().c()));
		}

		return ((!(state.getBlock() instanceof BlockRedstoneTorch))
				|| ((!(isWire)) && (state.get(BlockTorch.FACING) == side)));
	}

	private void addBlocksNeedingUpdate(World worldIn, BlockPosition pos, Set<BlockPosition> set) {
		EnumDirection facing;
		BlockPosition offsetPos;
		List connectedSides = getSidesToPower(worldIn, pos);

		EnumDirection[] arrayOfEnumDirection1 = facings;
		int i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			facing = arrayOfEnumDirection1[j];
			offsetPos = pos.shift(facing);

			if ((((connectedSides.contains(facing.opposite())) || (facing == EnumDirection.DOWN)
					|| ((facing.k().c()) && (a(worldIn.getType(offsetPos), facing)))))
					&& (canBlockBePoweredFromSide(worldIn.getType(offsetPos), facing, true))) {
				set.add(offsetPos);
			}

		}

		arrayOfEnumDirection1 = facings;
		i = arrayOfEnumDirection1.length;
		for (int j = 0; j < i; ++j) {
			facing = arrayOfEnumDirection1[j];
			offsetPos = pos.shift(facing);
			if ((((connectedSides.contains(facing.opposite())) || (facing == EnumDirection.DOWN)))
					&& (worldIn.getType(offsetPos).l())) {
				EnumDirection[] arrayOfEnumDirection2 = facings;
				int k = arrayOfEnumDirection2.length;
				for (int l = 0; l < k; ++l) {
					EnumDirection facing1 = arrayOfEnumDirection2[l];
					if (canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing1)), facing1, false)) {
						set.add(offsetPos.shift(facing1));
					}
				}
			}
		}
	}

	private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		calculateCurrentChanges(world, blockposition);
		Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();

		for (BlockPosition posi : updatedRedstoneWire) {
			addBlocksNeedingUpdate(world, posi, blocksNeedingUpdate);
		}

		Iterator it = Lists.newLinkedList(this.updatedRedstoneWire).descendingIterator();
		while (it.hasNext()) {
			addAllSurroundingBlocks((BlockPosition) it.next(), blocksNeedingUpdate);
		}

		blocksNeedingUpdate.removeAll(this.updatedRedstoneWire);
		this.updatedRedstoneWire.clear();

		for (BlockPosition posi : blocksNeedingUpdate) {
			world.applyPhysics(posi, this);
		}
	}

	private IBlockData a(World world, BlockPosition blockposition, BlockPosition blockposition1,
			IBlockData iblockdata) {
		IBlockData iblockdata1 = iblockdata;
		int i = iblockdata.get(BlockRedstoneWire.POWER).intValue();
		byte b0 = 0;
		int j = this.getPower(world, blockposition1, b0);

		this.g = false;
		int k = world.z(blockposition);

		this.g = true;
		if (k > 0 && k > j - 1) {
			j = k;
		}

		int l = 0;
		Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumDirection enumdirection = (EnumDirection) iterator.next();
			BlockPosition blockposition2 = blockposition.shift(enumdirection);
			boolean flag = blockposition2.getX() != blockposition1.getX()
					|| blockposition2.getZ() != blockposition1.getZ();

			if (flag) {
				l = this.getPower(world, blockposition2, l);
			}

			if (world.getType(blockposition2).l() && !world.getType(blockposition.up()).l()) {
				if (flag && blockposition.getY() >= blockposition1.getY()) {
					l = this.getPower(world, blockposition2.up(), l);
				}
			} else if (!world.getType(blockposition2).l() && flag && blockposition.getY() <= blockposition1.getY()) {
				l = this.getPower(world, blockposition2.down(), l);
			}
		}

		if (l > j) {
			j = l - 1;
		} else if (j > 0) {
			--j;
		} else {
			j = 0;
		}

		if (k > j - 1) {
			j = k;
		}

		// CraftBukkit start
		if (i != j) {
			BlockRedstoneEvent event = new BlockRedstoneEvent(
					world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i,
					j);
			world.getServer().getPluginManager().callEvent(event);

			j = event.getNewCurrent();
		}
		// CraftBukkit end

		if (i != j) {
			iblockdata = iblockdata.set(BlockRedstoneWire.POWER, Integer.valueOf(j));
			if (world.getType(blockposition) == iblockdata1) {
				world.setTypeAndData(blockposition, iblockdata, 2);
			}

			this.B.add(blockposition);
			// Paper start - Old TNT cannon behaviors
			if (world.paperConfig.oldCannonBehaviors) {
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.WEST));
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.EAST));
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.DOWN));
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.UP));
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.NORTH));
				this.blocksToUpdate.add(blockposition.shift(EnumDirection.SOUTH));
				return iblockdata;
			}
			// Paper end

			EnumDirection[] aenumdirection = EnumDirection.values();

			for (EnumDirection enumdirection1 : aenumdirection) {
				this.B.add(blockposition.shift(enumdirection1));
			}
		}

		return iblockdata;
	}

	private void b(World world, BlockPosition blockposition) {
		if (world.getType(blockposition).getBlock() == this) {
			world.applyPhysics(blockposition, this);
			// Paper start - Old TNT cannon behaviors
			if (world.paperConfig.oldCannonBehaviors) {
				world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
				world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
				return;
			}
			// Paper end
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i = aenumdirection.length;

			for (int j = 0; j < i; ++j) {
				EnumDirection enumdirection = aenumdirection[j];

				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

		}
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (!(world.isClientSide)) {
			EnumDirection enumdirection;
			e(world, blockposition, iblockdata);
			Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				b(world, blockposition.shift(enumdirection));
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				BlockPosition blockposition1 = blockposition.shift(enumdirection);

				if (world.getType(blockposition1).l()) {
					b(world, blockposition1.up());
				} else {
					b(world, blockposition1.down());
				}
			}
		}
	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		super.remove(world, blockposition, iblockdata);
		if (!(world.isClientSide)) {
			EnumDirection enumdirection1;
			EnumDirection[] aenumdirection = EnumDirection.values();

			for (EnumDirection enumdirection : aenumdirection) {
				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

			e(world, blockposition, iblockdata);
			Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection1 = (EnumDirection) iterator.next();
				this.b(world, blockposition.shift(enumdirection1));
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection1 = (EnumDirection) iterator.next();
				BlockPosition blockposition1 = blockposition.shift(enumdirection1);

				if (world.getType(blockposition1).l()) {
					this.b(world, blockposition1.up());
				} else {
					this.b(world, blockposition1.down());
				}
			}
		}
	}

	public int getPower(World world, BlockPosition blockposition, int i) {
		if (world.getType(blockposition).getBlock() != this) {
			return i;
		} else {
			int j = world.getType(blockposition).get(BlockRedstoneWire.POWER).intValue();

			return j > i ? j : i;
		}
	}

	@Override
	public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
		if (!(world.isClientSide)) {
			if (canPlace(world, blockposition)) {
				e(world, blockposition, iblockdata);
			} else {
				b(world, blockposition, iblockdata, 0);
				world.setAir(blockposition);
			}
		}
	}

	@Override
	@Nullable
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return Items.REDSTONE;
	}

	@Override
	public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition,
			EnumDirection enumdirection) {
		return !this.g ? 0 : iblockdata.a(iblockaccess, blockposition, enumdirection);
	}

	@Override
	public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition,
			EnumDirection enumdirection) {
		if (!(this.g)) {
			return 0;
		}

		int i = iblockdata.get(BlockRedstoneWire.POWER).intValue();

		if (i == 0) {
			return 0;
		}
		if (enumdirection == EnumDirection.UP) {
			return i;
		}

		if (getSidesToPower((World) iblockaccess, blockposition).contains(enumdirection)) {
			return i;
		}
		return 0;
	}

	private List<EnumDirection> getSidesToPower(World worldIn, BlockPosition pos) {
		int i;
		int k;
		List retval = Lists.newArrayList();
		EnumDirection[] arrayOfEnumDirection = facingsHorizontal;
		int j = arrayOfEnumDirection.length;
		for (int l = 0; l < j; ++l) {
			EnumDirection facing = arrayOfEnumDirection[l];
			if (c(worldIn, pos, facing)) {
				retval.add(facing);
			}
		}

		if (retval.isEmpty()) {
			return Lists.newArrayList(facingsHorizontal);
		}

		i = ((retval.contains(EnumDirection.NORTH)) || (retval.contains(EnumDirection.SOUTH))) ? 1 : 0;
		k = ((retval.contains(EnumDirection.EAST)) || (retval.contains(EnumDirection.WEST))) ? 1 : 0;
		if (i != 0) {
			retval.remove(EnumDirection.EAST);
			retval.remove(EnumDirection.WEST);
		}
		if (k != 0) {
			retval.remove(EnumDirection.NORTH);
			retval.remove(EnumDirection.SOUTH);
		}
		return retval;
	}

	private boolean c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
		BlockPosition blockposition1 = blockposition.shift(enumdirection);
		IBlockData iblockdata = iblockaccess.getType(blockposition1);
		boolean flag = iblockdata.l();
		boolean flag1 = iblockaccess.getType(blockposition.up()).l();

		return ((!(flag1)) && (flag) && (c(iblockaccess, blockposition1.up())));
	}

	protected static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition) {
		return i(iblockaccess.getType(blockposition));
	}

	protected static boolean i(IBlockData iblockdata) {
		return a(iblockdata, (EnumDirection) null);
	}

	protected static boolean a(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
		Block block = iblockdata.getBlock();

		if (block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if (Blocks.UNPOWERED_REPEATER.C(iblockdata)) {
			EnumDirection enumdirection1 = iblockdata.get(BlockFacingHorizontal.FACING);

			return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
		} else {
			return iblockdata.m() && enumdirection != null;
		}
	}

	@Override
	public boolean isPowerSource(IBlockData iblockdata) {
		return this.g;
	}

	@Override
	public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		return new ItemStack(Items.REDSTONE);
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockRedstoneWire.POWER, Integer.valueOf(i));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockRedstoneWire.POWER).intValue();
	}

	@Override
	public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
		switch (BlockRedstoneWire.SyntheticClass_1.a[enumblockrotation.ordinal()]) {
		case 1:
			return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))
					.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))
					.set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH))
					.set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));

		case 2:
			return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.EAST))
					.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.SOUTH))
					.set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.WEST))
					.set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.NORTH));

		case 3:
			return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.WEST))
					.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.NORTH))
					.set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.EAST))
					.set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.SOUTH));

		default:
			return iblockdata;
		}
	}

	@Override
	public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
		switch (BlockRedstoneWire.SyntheticClass_1.b[enumblockmirror.ordinal()]) {
		case 1:
			return iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))
					.set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH));

		case 2:
			return iblockdata.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))
					.set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));

		default:
			return super.a(iblockdata, enumblockmirror);
		}
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST,
				BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER });
	}

	static class SyntheticClass_1 {

		static final int[] a = new int[EnumBlockRotation.values().length];
		static final int[] b = new int[EnumBlockMirror.values().length];

		static {
			try {
				BlockRedstoneWire.SyntheticClass_1.b[EnumBlockMirror.LEFT_RIGHT.ordinal()] = 1;
				BlockRedstoneWire.SyntheticClass_1.b[EnumBlockMirror.FRONT_BACK.ordinal()] = 2;
				BlockRedstoneWire.SyntheticClass_1.a[EnumBlockRotation.CLOCKWISE_180.ordinal()] = 1;
				BlockRedstoneWire.SyntheticClass_1.a[EnumBlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
				BlockRedstoneWire.SyntheticClass_1.a[EnumBlockRotation.CLOCKWISE_90.ordinal()] = 3;
			} catch (NoSuchFieldError nosuchfielderror) {
				;
			}

		}
	}

	static enum EnumRedstoneWireConnection implements INamable {

		UP("up"), SIDE("side"), NONE("none");

		private final String d;

		private EnumRedstoneWireConnection(String s) {
			this.d = s;
		}

		@Override
		public String toString() {
			return this.getName();
		}

		@Override
		public String getName() {
			return this.d;
		}
	}
}
