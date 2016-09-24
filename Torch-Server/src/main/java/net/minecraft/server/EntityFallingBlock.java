package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityFallingBlock extends Entity {

    private IBlockData block;
    public int ticksLived;
    public boolean dropItem = true;
    private boolean f;
    public boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    public NBTTagCompound tileEntityData;
    protected static final DataWatcherObject<BlockPosition> d = DataWatcher.a(EntityFallingBlock.class, DataWatcherRegistry.j);

    public EntityFallingBlock(World world) {
        super(world);
    }

    public EntityFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata) {
        super(world);
        this.block = iblockdata;
        this.i = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(d0, d1 + (double) ((1.0F - this.length) / 2.0F), d2);
        this.motXAtom.set(0.0D);
        this.motYAtom.set(0.0D);
        this.motZAtom.set(0.0D);
        this.lastXAtom.set(d0);
        this.lastYAtom.set(d1);
        this.lastZAtom.set(d2);
        this.a(new BlockPosition(this));
    }

    public void a(BlockPosition blockposition) {
        this.datawatcher.set(EntityFallingBlock.d, blockposition);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void i() {
        this.datawatcher.register(EntityFallingBlock.d, BlockPosition.ZERO);
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public void m() {
        Block block = this.block.getBlock();

        if (this.block.getMaterial() == Material.AIR) {
            this.die();
        } else {
            this.lastXAtom = this.locXAtom;
            this.lastYAtom = this.locYAtom;
            this.lastZAtom = this.locZAtom;
            BlockPosition blockposition;

            if (this.ticksLived++ == 0) {
                blockposition = new BlockPosition(this);
                if (this.world.getType(blockposition).getBlock() == block && !CraftEventFactory.callEntityChangeBlockEvent(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.AIR, 0).isCancelled()) {
                    this.world.setAir(blockposition);
                } else if (!this.world.isClientSide) {
                    this.die();
                    return;
                }
            }

            this.motYAtom.set(this.motYAtom.get() - 0.03999999910593033D);
            this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());

            // Paper start - Configurable EntityFallingBlock height nerf
            if (this.world.paperConfig.fallingBlockHeightNerf != 0 && this.locYAtom.get() > this.world.paperConfig.fallingBlockHeightNerf) {
                if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                    this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                }

                this.die();
            }
            // Paper end

            this.motXAtom.set(this.motXAtom.get() * 0.9800000190734863D);
            this.motYAtom.set(this.motYAtom.get() * 0.9800000190734863D);
            this.motZAtom.set(this.motZAtom.get() * 0.9800000190734863D);
            if (!this.world.isClientSide) {
                blockposition = new BlockPosition(this);
                if (this.onGround) {
                    IBlockData iblockdata = this.world.getType(blockposition);
                    if (!isOnGround()) {
                        this.onGround = false;
                        return; // Paper
                    }

                    this.motXAtom.set(this.motXAtom.get() * 0.699999988079071D);
                    this.motXAtom.set(this.motXAtom.get() * 0.699999988079071D);
                    this.motXAtom.set(this.motXAtom.get() * -0.5D);
                    if (iblockdata.getBlock() != Blocks.PISTON_EXTENSION) {
                        this.die();
                        if (!this.f) {
                            // CraftBukkit start
                            if (this.world.a(block, blockposition, true, EnumDirection.UP, (Entity) null, (ItemStack) null) && !BlockFalling.i(this.world.getType(blockposition.down()))) {
                                if (CraftEventFactory.callEntityChangeBlockEvent(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.block.getBlock(), this.block.getBlock().toLegacyData(this.block)).isCancelled()) {
                                    return;
                                }
                                this.world.setTypeAndData(blockposition, this.block, 3);
                                // CraftBukkit end
                                if (block instanceof BlockFalling) {
                                    ((BlockFalling) block).a_(this.world, blockposition);
                                }

                                if (this.tileEntityData != null && block instanceof ITileEntity) {
                                    TileEntity tileentity = this.world.getTileEntity(blockposition);

                                    if (tileentity != null) {
                                        NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());
                                        Iterator iterator = this.tileEntityData.c().iterator();

                                        while (iterator.hasNext()) {
                                            String s = (String) iterator.next();
                                            NBTBase nbtbase = this.tileEntityData.get(s);

                                            if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
                                                nbttagcompound.set(s, nbtbase.clone());
                                            }
                                        }

                                        tileentity.a(nbttagcompound);
                                        tileentity.update();
                                    }
                                }
                            } else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                                this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                            }
                        }
                    }
                } else if (this.ticksLived > 100 && !this.world.isClientSide && (blockposition.getY() < 1 || blockposition.getY() > 256) || this.ticksLived > 600) {
                    if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                    }

                    this.die();
                }
            }

        }
    }

    // Paper start
    private boolean isOnGround() {
        BlockPosition where = new BlockPosition(this.locXAtom.get(), this.locYAtom.get() - 0.009999999776482582D, this.locZAtom.get());

        if (!BlockFalling.canMoveThrough(this.world.getType(where))) {
            return true;
        }

        IBlockData blockData = this.world.getType(where.down());
        if (BlockFalling.canMoveThrough(blockData)) {
            return false;
        }

        List<AxisAlignedBB> list = new ArrayList<>();
        addCollisions(blockData, getWorld(), where, this.getBoundingBox(), list, this);
        return list.size() > 0;
    }

    // OBFHELPER
    private void addCollisions(IBlockData blockData, World world, BlockPosition where, AxisAlignedBB collider, List<AxisAlignedBB> list, Entity entity) {
        blockData.a(world, where, collider, list, entity);
    }
    // Paper end

    public void e(float f, float f1) {
        Block block = this.block.getBlock();

        if (this.hurtEntities) {
            int i = MathHelper.f(f - 1.0F);

            if (i > 0) {
                ArrayList arraylist = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
                boolean flag = block == Blocks.ANVIL;
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    CraftEventFactory.entityDamage = this; // CraftBukkit
                    entity.damageEntity(damagesource, (float) Math.min(MathHelper.d((float) i * this.fallHurtAmount), this.fallHurtMax));
                    CraftEventFactory.entityDamage = null; // CraftBukkit
                }

                if (flag && (double) this.random.nextFloat() < 0.05000000074505806D + (double) i * 0.05D) {
                    int j = ((Integer) this.block.get(BlockAnvil.DAMAGE)).intValue();

                    ++j;
                    if (j > 2) {
                        this.f = true;
                    } else {
                        this.block = this.block.set(BlockAnvil.DAMAGE, Integer.valueOf(j));
                    }
                }
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        Block block = this.block != null ? this.block.getBlock() : Blocks.AIR;
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.b(block);

        nbttagcompound.setString("Block", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("Data", (byte) block.toLegacyData(this.block));
        nbttagcompound.setInt("Time", this.ticksLived);
        nbttagcompound.setBoolean("DropItem", this.dropItem);
        nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
        nbttagcompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        nbttagcompound.setInt("FallHurtMax", this.fallHurtMax);
        if (this.tileEntityData != null) {
            nbttagcompound.set("TileEntityData", this.tileEntityData);
        }

    }

    protected void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getByte("Data") & 255;

        if (nbttagcompound.hasKeyOfType("Block", 8)) {
            this.block = Block.getByName(nbttagcompound.getString("Block")).fromLegacyData(i);
        } else if (nbttagcompound.hasKeyOfType("TileID", 99)) {
            this.block = Block.getById(nbttagcompound.getInt("TileID")).fromLegacyData(i);
        } else {
            this.block = Block.getById(nbttagcompound.getByte("Tile") & 255).fromLegacyData(i);
        }

        this.ticksLived = nbttagcompound.getInt("Time");
        Block block = this.block.getBlock();

        if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
            this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
            this.fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
            this.fallHurtMax = nbttagcompound.getInt("FallHurtMax");
        } else if (block == Blocks.ANVIL) {
            this.hurtEntities = true;
        }

        if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
            this.dropItem = nbttagcompound.getBoolean("DropItem");
        }

        if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
            this.tileEntityData = nbttagcompound.getCompound("TileEntityData");
        }

        if (block == null || block.getBlockData().getMaterial() == Material.AIR) {
            this.block = Blocks.SAND.getBlockData();
        }

        // Paper start - Try and load origin location from the old NBT tags for backwards compatibility
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            int srcX = nbttagcompound.getInt("SourceLoc_x");
            int srcY = nbttagcompound.getInt("SourceLoc_y");
            int srcZ = nbttagcompound.getInt("SourceLoc_z");
            origin = new org.bukkit.Location(world.getWorld(), srcX, srcY, srcZ);
        }
        // Paper end
    }

    public void a(boolean flag) {
        this.hurtEntities = flag;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        super.appendEntityCrashDetails(crashreportsystemdetails);
        if (this.block != null) {
            Block block = this.block.getBlock();

            crashreportsystemdetails.a("Immitating block ID", (Object) Integer.valueOf(Block.getId(block)));
            crashreportsystemdetails.a("Immitating block data", (Object) Integer.valueOf(block.toLegacyData(this.block)));
        }

    }

    @Nullable
    public IBlockData getBlock() {
        return this.block;
    }

    public boolean bs() {
        return true;
    }

    // Paper start - Old TNT cannon behaviors
    @Override
    public double getDistance(double d0, double d1, double d2) {
        if (!world.paperConfig.oldCannonBehaviors) return super.getDistance(d0, d1, d2);

        double newX = this.locXAtom.get() - d0;
        double newY = this.locYAtom.get() + this.getHeadHeight() - d1;
        double newZ = this.locZAtom.get() - d2;

        return (double) MathHelper.sqrt(newX * newX + newY * newY + newZ * newZ);
    }


    // Paper end
}
