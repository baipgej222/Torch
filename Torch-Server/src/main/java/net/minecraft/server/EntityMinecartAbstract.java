package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;
// CraftBukkit end

public abstract class EntityMinecartAbstract extends Entity implements INamableTileEntity {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> c = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.h);
    private boolean g;
    private static final int[][][] h = new int[][][] { { { 0, 0, -1}, { 0, 0, 1}}, { { -1, 0, 0}, { 1, 0, 0}}, { { -1, -1, 0}, { 1, 0, 0}}, { { -1, 0, 0}, { 1, -1, 0}}, { { 0, 0, -1}, { 0, -1, 1}}, { { 0, -1, -1}, { 0, 0, 1}}, { { 0, 0, 1}, { 1, 0, 0}}, { { 0, 0, 1}, { -1, 0, 0}}, { { 0, 0, -1}, { -1, 0, 0}}, { { 0, 0, -1}, { 1, 0, 0}}};
    private int at;
    private double au;
    private double av;
    private double aw;
    private double ax;
    private double ay;

    // CraftBukkit start
    public boolean slowWhenEmpty = true;
    private double derailedX = 0.5;
    private double derailedY = 0.5;
    private double derailedZ = 0.5;
    private double flyingX = 0.95;
    private double flyingY = 0.95;
    private double flyingZ = 0.95;
    public double maxSpeed = 0.4D;
    // CraftBukkit end

    public EntityMinecartAbstract(World world) {
        super(world);
        this.i = true;
        this.setSize(0.98F, 0.7F);
    }

    public static EntityMinecartAbstract a(World world, double d0, double d1, double d2, EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype) {
        switch (EntityMinecartAbstract.SyntheticClass_1.a[entityminecartabstract_enumminecarttype.ordinal()]) {
        case 1:
            return new EntityMinecartChest(world, d0, d1, d2);

        case 2:
            return new EntityMinecartFurnace(world, d0, d1, d2);

        case 3:
            return new EntityMinecartTNT(world, d0, d1, d2);

        case 4:
            return new EntityMinecartMobSpawner(world, d0, d1, d2);

        case 5:
            return new EntityMinecartHopper(world, d0, d1, d2);

        case 6:
            return new EntityMinecartCommandBlock(world, d0, d1, d2);

        default:
            return new EntityMinecartRideable(world, d0, d1, d2);
        }
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void i() {
        this.datawatcher.register(EntityMinecartAbstract.a, Integer.valueOf(0));
        this.datawatcher.register(EntityMinecartAbstract.b, Integer.valueOf(1));
        this.datawatcher.register(EntityMinecartAbstract.c, Float.valueOf(0.0F));
        this.datawatcher.register(EntityMinecartAbstract.d, Integer.valueOf(0));
        this.datawatcher.register(EntityMinecartAbstract.e, Integer.valueOf(6));
        this.datawatcher.register(EntityMinecartAbstract.f, Boolean.valueOf(false));
    }

    @Nullable
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Nullable
    public AxisAlignedBB af() {
        return null;
    }

    public boolean isCollidable() {
        return true;
    }

    public EntityMinecartAbstract(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
        this.motXAtom.set(0.0D);
        this.motYAtom.set(0.0D);
        this.motZAtom.set(0.0D);
        this.lastXAtom.set(d0);
        this.lastYAtom.set(d1);
        this.lastZAtom.set(d2);

        this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleCreateEvent((Vehicle) this.getBukkitEntity())); // CraftBukkit
    }

    public double ay() {
        return 0.0D;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.world.isClientSide && !this.dead) {
            if (this.isInvulnerable(damagesource)) {
                return false;
            } else {
                // CraftBukkit start - fire VehicleDamageEvent
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.entity.Entity passenger = (damagesource.getEntity() == null) ? null : damagesource.getEntity().getBukkitEntity();

                VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, f);
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                f = (float) event.getDamage();
                // CraftBukkit end
                this.e(-this.u());
                this.d(10);
                this.ao();
                this.setDamage(this.getDamage() + f * 10.0F);
                boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

                if (flag || this.getDamage() > 40.0F) {
                    // CraftBukkit start
                    VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
                    this.world.getServer().getPluginManager().callEvent(destroyEvent);

                    if (destroyEvent.isCancelled()) {
                        this.setDamage(40); // Maximize damage so this doesn't get triggered again right away
                        return true;
                    }
                    // CraftBukkit end
                    this.az();
                    if (flag && !this.hasCustomName()) {
                        this.die();
                    } else {
                        this.a(damagesource);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void a(DamageSource damagesource) {
        this.die();
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = new ItemStack(Items.MINECART, 1);

            if (this.hasCustomName()) { // Paper - Only set item name if custom name is set. Workaround for MC-68446
                itemstack.c(this.getName());
            }

            this.a(itemstack, 0.0F);
        }

    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public void die() {
        super.die();
    }

    public EnumDirection bk() {
        return this.g ? this.getDirection().opposite().e() : this.getDirection().e();
    }

    public void m() {
        // CraftBukkit start
        double prevX = this.locXAtom.get();
        double prevY = this.locYAtom.get();
        double prevZ = this.locZAtom.get();
        float prevYaw = this.yaw;
        float prevPitch = this.pitch;
        // CraftBukkit end

        if (this.getType() > 0) {
            this.d(this.getType() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.locYAtom.get() < -64.0D) {
            this.Y();
        }

        int i;

        if (!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            MinecraftServer minecraftserver = this.world.getMinecraftServer();

            i = this.V();
            if (this.ak) {
                if (true || minecraftserver.getAllowNether()) { // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
                    if (!this.isPassenger() && this.al++ >= i) {
                        this.al = i;
                        this.portalCooldown = this.aC();
                        byte b0;

                        if (this.world.worldProvider.getDimensionManager().getDimensionID() == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.c(b0);
                    }

                    this.ak = false;
                }
            } else {
                if (this.al > 0) {
                    this.al -= 4;
                }

                if (this.al < 0) {
                    this.al = 0;
                }
            }

            if (this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }

        if (this.world.isClientSide) {
            if (this.at > 0) {
                double d0 = this.locXAtom.get() + (this.au - this.locXAtom.get()) / (double) this.at;
                double d1 = this.locYAtom.get() + (this.av - this.locYAtom.get()) / (double) this.at;
                double d2 = this.locZAtom.get() + (this.aw - this.locZAtom.get()) / (double) this.at;
                double d3 = MathHelper.g(this.ax - (double) this.yaw);

                this.yaw = (float) ((double) this.yaw + d3 / (double) this.at);
                this.pitch = (float) ((double) this.pitch + (this.ay - (double) this.pitch) / (double) this.at);
                --this.at;
                this.setPosition(d0, d1, d2);
                this.setYawPitch(this.yaw, this.pitch);
            } else {
                this.setPosition(this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get());
                this.setYawPitch(this.yaw, this.pitch);
            }

        } else {
            this.lastXAtom = this.locXAtom;
            this.lastYAtom = this.locYAtom;
            this.lastZAtom = this.locZAtom;
            this.motYAtom.set(this.motYAtom.get() - 0.03999999910593033D);
            int j = MathHelper.floor(this.locXAtom);

            i = MathHelper.floor(this.locYAtom);
            int k = MathHelper.floor(this.locZAtom);

            if (BlockMinecartTrackAbstract.b(this.world, new BlockPosition(j, i - 1, k))) {
                --i;
            }

            BlockPosition blockposition = new BlockPosition(j, i, k);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (BlockMinecartTrackAbstract.i(iblockdata)) {
                this.a(blockposition, iblockdata);
                if (iblockdata.getBlock() == Blocks.ACTIVATOR_RAIL) {
                    this.a(j, i, k, ((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue());
                }
            } else {
                this.q();
            }

            this.checkBlockCollisions();
            this.pitch = 0.0F;
            double d4 = this.lastXAtom.get() - this.locXAtom.get();
            double d5 = this.lastZAtom.get() - this.locZAtom.get();

            if (d4 * d4 + d5 * d5 > 0.001D) {
                this.yaw = (float) (MathHelper.b(d5, d4) * 180.0D / 3.141592653589793D);
                if (this.g) {
                    this.yaw += 180.0F;
                }
            }

            double d6 = (double) MathHelper.g(this.yaw - this.lastYaw);

            if (d6 < -170.0D || d6 >= 170.0D) {
                this.yaw += 180.0F;
                this.g = !this.g;
            }

            this.setYawPitch(this.yaw, this.pitch);

            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            Location to = new Location(bworld, this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get(), this.yaw, this.pitch);
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();

            this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

            if (!from.equals(to)) {
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
            }
            // CraftBukkit end

            this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D)).parallelStream().forEach(entity -> {
                if (!this.w(entity) && entity.isCollidable() && entity instanceof EntityMinecartAbstract) {
                    entity.collide(this);
                }
            });

            this.aj();
        }
    }

    protected double o() {
        return this.maxSpeed; // CraftBukkit
    }

    public void a(int i, int j, int k, boolean flag) {}

    protected void q() {
        double d0 = this.o();

        this.motXAtom.set(MathHelper.a(this.motXAtom.get(), -d0, d0));
        this.motZAtom.set(MathHelper.a(this.motZAtom.get(), -d0, d0));
        if (this.onGround) {
            // CraftBukkit start - replace magic numbers with our variables
            this.motXAtom.set(this.motXAtom.get() * this.derailedX);
            this.motYAtom.set(this.motYAtom.get() * this.derailedY);
            this.motZAtom.set(this.motZAtom.get() * this.derailedZ);
            // CraftBukkit end
        }

        this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());
        if (!this.onGround) {
            // CraftBukkit start - replace magic numbers with our variables
            this.motXAtom.set(this.motXAtom.get() * this.derailedX);
            this.motYAtom.set(this.motYAtom.get() * this.derailedY);
            this.motZAtom.set(this.motZAtom.get() * this.derailedZ);
            // CraftBukkit end
        }

    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.fallDistance = 0.0F;
        Vec3D vec3d = this.k(this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get());

        this.locYAtom.set((double) blockposition.getY());
        boolean flag = false;
        boolean flag1 = false;
        BlockMinecartTrackAbstract blockminecarttrackabstract = (BlockMinecartTrackAbstract) iblockdata.getBlock();

        if (blockminecarttrackabstract == Blocks.GOLDEN_RAIL) {
            flag = ((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue();
            flag1 = !flag;
        }

        double d0 = 0.0078125D;
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(blockminecarttrackabstract.g());

        switch (EntityMinecartAbstract.SyntheticClass_1.b[blockminecarttrackabstract_enumtrackposition.ordinal()]) {
        case 1:
            this.motXAtom.set(this.motXAtom.get() - 0.0078125D);
            this.locYAtom.addAndGet(1);
            break;

        case 2:
            this.motXAtom.set(this.motXAtom.get() + 0.0078125D);
            this.locYAtom.addAndGet(1);
            break;

        case 3:
            this.motZAtom.set(this.motXAtom.get() + 0.0078125D);
            this.locYAtom.addAndGet(1);
            break;

        case 4:
            this.motZAtom.set(this.motXAtom.get() - 0.0078125D);
            this.locYAtom.addAndGet(1);
        }

        int[][] aint = EntityMinecartAbstract.h[blockminecarttrackabstract_enumtrackposition.a()];
        double d1 = (double) (aint[1][0] - aint[0][0]);
        double d2 = (double) (aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motXAtom.get() * d1 + this.motZAtom.get() * d2;

        if (d4 < 0.0D) {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());

        if (d5 > 2.0D) {
            d5 = 2.0D;
        }

        this.motXAtom.set(d5 * d1 / d3);
        this.motZAtom.set(d5 * d2 / d3);
        Entity entity = this.bv().isEmpty() ? null : (Entity) this.bv().get(0);
        double d6;
        double d7;
        double d8;
        double d9;

        if (entity instanceof EntityLiving) {
            d6 = (double) ((EntityLiving) entity).bf;
            if (d6 > 0.0D) {
                d7 = -Math.sin((double) (entity.yaw * 0.017453292F));
                d8 = Math.cos((double) (entity.yaw * 0.017453292F));
                d9 = this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get();
                if (d9 < 0.01D) {
                    this.motXAtom.addAndGet(d7 * 0.1D);
                    this.motZAtom.addAndGet(d8 * 0.1D);
                    flag1 = false;
                }
            }
        }

        if (flag1) {
            d6 = Math.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());
            if (d6 < 0.03D) {
                this.motXAtom.set(this.motXAtom.get() * 0.0D);
                this.motYAtom.set(this.motYAtom.get() * 0.0D);
                this.motZAtom.set(this.motZAtom.get() * 0.0D);
            } else {
                this.motXAtom.set(this.motXAtom.get() * 0.5D);
                this.motYAtom.set(this.motYAtom.get() * 0.0D);
                this.motZAtom.set(this.motZAtom.get() * 0.5D);
            }
        }

        d6 = 0.0D;
        d7 = (double) blockposition.getX() + 0.5D + (double) aint[0][0] * 0.5D;
        d8 = (double) blockposition.getZ() + 0.5D + (double) aint[0][2] * 0.5D;
        d9 = (double) blockposition.getX() + 0.5D + (double) aint[1][0] * 0.5D;
        double d10 = (double) blockposition.getZ() + 0.5D + (double) aint[1][2] * 0.5D;

        d1 = d9 - d7;
        d2 = d10 - d8;
        double d11;
        double d12;

        if (d1 == 0.0D) {
            this.locXAtom.set((double) blockposition.getX() + 0.5D);
            d6 = this.locZAtom.get() - (double) blockposition.getZ();
        } else if (d2 == 0.0D) {
            this.locZAtom.set((double) blockposition.getZ() + 0.5D);
            d6 = this.locXAtom.get() - (double) blockposition.getX();
        } else {
            d11 = this.locXAtom.get() - d7;
            d12 = this.locZAtom.get() - d8;
            d6 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        this.locXAtom.set(d7 + d1 * d6);
        this.locZAtom.set(d8 + d2 * d6);
        this.setPosition(this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get());
        d11 = this.motXAtom.get();
        d12 = this.motZAtom.get();
        if (this.isVehicle()) {
            d11 *= 0.75D;
            d12 *= 0.75D;
        }

        double d13 = this.o();

        d11 = MathHelper.a(d11, -d13, d13);
        d12 = MathHelper.a(d12, -d13, d13);
        this.move(d11, 0.0D, d12);
        if (aint[0][1] != 0 && MathHelper.floor(this.locXAtom) - blockposition.getX() == aint[0][0] && MathHelper.floor(this.locZAtom) - blockposition.getZ() == aint[0][2]) {
            this.setPosition(this.locXAtom.get(), this.locYAtom.get() + (double) aint[0][1], this.locZAtom.get());
        } else if (aint[1][1] != 0 && MathHelper.floor(this.locXAtom) - blockposition.getX() == aint[1][0] && MathHelper.floor(this.locZAtom) - blockposition.getZ() == aint[1][2]) {
            this.setPosition(this.locXAtom.get(), this.locYAtom.get() + (double) aint[1][1], this.locZAtom.get());
        }

        this.r();
        Vec3D vec3d1 = this.k(this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get());

        if (vec3d1 != null && vec3d != null) {
            double d14 = (vec3d.y - vec3d1.y) * 0.05D;

            d5 = Math.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());
            if (d5 > 0.0D) {
                this.motXAtom.set(this.motXAtom.get() / d5 * (d5 + d14));
                this.motZAtom.set(this.motZAtom.get() / d5 * (d5 + d14));
            }

            this.setPosition(this.locXAtom.get(), vec3d1.y, this.locZAtom.get());
        }

        int i = MathHelper.floor(this.locXAtom);
        int j = MathHelper.floor(this.locZAtom);

        if (i != blockposition.getX() || j != blockposition.getZ()) {
            d5 = Math.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());
            this.motXAtom.set(d5 * (double) (i - blockposition.getX()));
            this.motZAtom.set(d5 * (double) (j - blockposition.getZ()));
        }

        if (flag) {
            double d15 = Math.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());

            if (d15 > 0.01D) {
                double d16 = 0.06D;

                this.motXAtom.addAndGet(this.motXAtom.get() / d15 * d16);
                this.motZAtom.addAndGet(this.motZAtom.get() / d15 * d16);
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST) {
                if (this.world.getType(blockposition.west()).l()) {
                    this.motXAtom.set(0.02D);
                } else if (this.world.getType(blockposition.east()).l()) {
                    this.motXAtom.set(-0.02D);
                }
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) {
                if (this.world.getType(blockposition.north()).l()) {
                    this.motZAtom.set(0.02D);
                } else if (this.world.getType(blockposition.south()).l()) {
                    this.motZAtom.set(-0.02D);
                }
            }
        }

    }

    protected void r() {
        if (this.isVehicle() || !this.slowWhenEmpty) { // CraftBukkit - add !this.slowWhenEmpty
            this.motXAtom.set(this.motXAtom.get() * 0.996999979019165D);
            this.motYAtom.set(this.motYAtom.get() * 0.0D);
            this.motZAtom.set(this.motZAtom.get() * 0.996999979019165D);
        } else {
            this.motXAtom.set(this.motXAtom.get() * 0.9599999785423279D);
            this.motYAtom.set(this.motYAtom.get() * 0.0D);
            this.motZAtom.set(this.motZAtom.get() * 0.9599999785423279D);
        }

    }

    public void setPosition(double d0, double d1, double d2) {
        this.locXAtom.set(d0);
        this.locYAtom.set(d1);
        this.locZAtom.set(d2);
        float f = this.width / 2.0F;
        float f1 = this.length;

        this.a(new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f));
    }

    public Vec3D k(double d0, double d1, double d2) {
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);

        if (BlockMinecartTrackAbstract.b(this.world, new BlockPosition(i, j - 1, k))) {
            --j;
        }

        IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));

        if (BlockMinecartTrackAbstract.i(iblockdata)) {
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).g());
            int[][] aint = EntityMinecartAbstract.h[blockminecarttrackabstract_enumtrackposition.a()];
            double d3 = 0.0D;
            double d4 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
            double d5 = (double) j + 0.0625D + (double) aint[0][1] * 0.5D;
            double d6 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
            double d7 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
            double d8 = (double) j + 0.0625D + (double) aint[1][1] * 0.5D;
            double d9 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;
            double d10 = d7 - d4;
            double d11 = (d8 - d5) * 2.0D;
            double d12 = d9 - d6;

            if (d10 == 0.0D) {
                d0 = (double) i + 0.5D;
                d3 = d2 - (double) k;
            } else if (d12 == 0.0D) {
                d2 = (double) k + 0.5D;
                d3 = d0 - (double) i;
            } else {
                double d13 = d0 - d4;
                double d14 = d2 - d6;

                d3 = (d13 * d10 + d14 * d12) * 2.0D;
            }

            d0 = d4 + d10 * d3;
            d1 = d5 + d11 * d3;
            d2 = d6 + d12 * d3;
            if (d11 < 0.0D) {
                ++d1;
            }

            if (d11 > 0.0D) {
                d1 += 0.5D;
            }

            return new Vec3D(d0, d1, d2);
        } else {
            return null;
        }
    }

    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.getBoolean("CustomDisplayTile")) {
            Block block;

            if (nbttagcompound.hasKeyOfType("DisplayTile", 8)) {
                block = Block.getByName(nbttagcompound.getString("DisplayTile"));
            } else {
                block = Block.getById(nbttagcompound.getInt("DisplayTile"));
            }

            int i = nbttagcompound.getInt("DisplayData");

            this.setDisplayBlock(block == null ? Blocks.AIR.getBlockData() : block.fromLegacyData(i));
            this.setDisplayBlockOffset(nbttagcompound.getInt("DisplayOffset"));
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        if (this.B()) {
            nbttagcompound.setBoolean("CustomDisplayTile", true);
            IBlockData iblockdata = this.getDisplayBlock();
            MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.b(iblockdata.getBlock());

            nbttagcompound.setString("DisplayTile", minecraftkey == null ? "" : minecraftkey.toString());
            nbttagcompound.setInt("DisplayData", iblockdata.getBlock().toLegacyData(iblockdata));
            nbttagcompound.setInt("DisplayOffset", this.getDisplayBlockOffset());
        }

    }

    public void collide(Entity entity) {
        if (!this.world.isClientSide) {
            if (!entity.noclip && !this.noclip) {
                if (!this.w(entity)) {
                    // CraftBukkit start
                    Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                    org.bukkit.entity.Entity hitEntity = (entity == null) ? null : entity.getBukkitEntity();

                    VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
                    this.world.getServer().getPluginManager().callEvent(collisionEvent);

                    if (collisionEvent.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                    if (entity instanceof EntityLiving && this.v() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE && this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get() > 0.01D && !(entity instanceof EntityHuman) && !(entity instanceof EntityIronGolem) && !this.isVehicle() && !entity.isPassenger()) {
                        entity.startRiding(this);
                    }

                    double d0 = entity.locXAtom.get() - this.locXAtom.get();
                    double d1 = entity.locZAtom.get() - this.locZAtom.get();
                    double d2 = d0 * d0 + d1 * d1;

                    // CraftBukkit - collision
                    if (d2 >= 9.999999747378752E-5D && !collisionEvent.isCollisionCancelled()) {
                        d2 = (double) MathHelper.sqrt(d2);
                        d0 /= d2;
                        d1 /= d2;
                        double d3 = 1.0D / d2;

                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 *= d3;
                        d1 *= d3;
                        d0 *= 0.10000000149011612D;
                        d1 *= 0.10000000149011612D;
                        d0 *= (double) (1.0F - this.R);
                        d1 *= (double) (1.0F - this.R);
                        d0 *= 0.5D;
                        d1 *= 0.5D;
                        if (entity instanceof EntityMinecartAbstract) {
                            double d4 = entity.locXAtom.get() - this.locXAtom.get();
                            double d5 = entity.locZAtom.get() - this.locZAtom.get();
                            Vec3D vec3d = (new Vec3D(d4, 0.0D, d5)).a();
                            Vec3D vec3d1 = (new Vec3D((double) MathHelper.cos(this.yaw * 0.017453292F), 0.0D, (double) MathHelper.sin(this.yaw * 0.017453292F))).a();
                            double d6 = Math.abs(vec3d.b(vec3d1));

                            if (d6 < 0.800000011920929D) {
                                return;
                            }

                            double d7 = entity.motXAtom.get() + this.motXAtom.get();
                            double d8 = entity.motZAtom.get() + this.motZAtom.get();

                            if (((EntityMinecartAbstract) entity).v() == EntityMinecartAbstract.EnumMinecartType.FURNACE && this.v() != EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                entity.motXAtom.set(entity.motXAtom.get() * 0.20000000298023224D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.20000000298023224D);
                                this.g(entity.motXAtom.get() - d0, 0.0D, entity.motZAtom.get() - d1);
                                entity.motXAtom.set(entity.motXAtom.get() * 0.949999988079071D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.949999988079071D);
                            } else if (((EntityMinecartAbstract) entity).v() != EntityMinecartAbstract.EnumMinecartType.FURNACE && this.v() == EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                entity.motXAtom.set(entity.motXAtom.get() * 0.20000000298023224D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.20000000298023224D);
                                entity.g(this.motXAtom.get() + d0, 0.0D, this.motZAtom.get() + d1);
                                entity.motXAtom.set(entity.motXAtom.get() * 0.949999988079071D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.949999988079071D);
                            } else {
                                d7 /= 2.0D;
                                d8 /= 2.0D;
                                entity.motXAtom.set(entity.motXAtom.get() * 0.20000000298023224D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.20000000298023224D);
                                this.g(d7 - d0, 0.0D, d8 - d1);
                                entity.motXAtom.set(entity.motXAtom.get() * 0.20000000298023224D);
                                entity.motZAtom.set(entity.motZAtom.get() * 0.20000000298023224D);
                                entity.g(d7 + d0, 0.0D, d8 + d1);
                            }
                        } else {
                            this.g(-d0, 0.0D, -d1);
                            entity.g(d0 / 4.0D, 0.0D, d1 / 4.0D);
                        }
                    }

                }
            }
        }
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityMinecartAbstract.c, Float.valueOf(f));
    }

    public float getDamage() {
        return ((Float) this.datawatcher.get(EntityMinecartAbstract.c)).floatValue();
    }

    public void d(int i) {
        this.datawatcher.set(EntityMinecartAbstract.a, Integer.valueOf(i));
    }

    public int getType() {
        return ((Integer) this.datawatcher.get(EntityMinecartAbstract.a)).intValue();
    }

    public void e(int i) {
        this.datawatcher.set(EntityMinecartAbstract.b, Integer.valueOf(i));
    }

    public int u() {
        return ((Integer) this.datawatcher.get(EntityMinecartAbstract.b)).intValue();
    }

    public abstract EntityMinecartAbstract.EnumMinecartType v();

    public IBlockData getDisplayBlock() {
        return !this.B() ? this.x() : Block.getByCombinedId(((Integer) this.getDataWatcher().get(EntityMinecartAbstract.d)).intValue());
    }

    public IBlockData x() {
        return Blocks.AIR.getBlockData();
    }

    public int getDisplayBlockOffset() {
        return !this.B() ? this.A() : ((Integer) this.getDataWatcher().get(EntityMinecartAbstract.e)).intValue();
    }

    public int A() {
        return 6;
    }

    public void setDisplayBlock(IBlockData iblockdata) {
        this.getDataWatcher().set(EntityMinecartAbstract.d, Integer.valueOf(Block.getCombinedId(iblockdata)));
        this.a(true);
    }

    public void setDisplayBlockOffset(int i) {
        this.getDataWatcher().set(EntityMinecartAbstract.e, Integer.valueOf(i));
        this.a(true);
    }

    public boolean B() {
        return ((Boolean) this.getDataWatcher().get(EntityMinecartAbstract.f)).booleanValue();
    }

    public void a(boolean flag) {
        this.getDataWatcher().set(EntityMinecartAbstract.f, Boolean.valueOf(flag));
    }

    static class SyntheticClass_1 {

        static final int[] a;
        static final int[] b = new int[BlockMinecartTrackAbstract.EnumTrackPosition.values().length];

        static {
            try {
                EntityMinecartAbstract.SyntheticClass_1.b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.b[BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            a = new int[EntityMinecartAbstract.EnumMinecartType.values().length];

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.CHEST.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.FURNACE.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.TNT.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror6) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.SPAWNER.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror7) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.HOPPER.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror8) {
                ;
            }

            try {
                EntityMinecartAbstract.SyntheticClass_1.a[EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror9) {
                ;
            }

        }
    }

    public static enum EnumMinecartType {

        RIDEABLE(0, "MinecartRideable"), CHEST(1, "MinecartChest"), FURNACE(2, "MinecartFurnace"), TNT(3, "MinecartTNT"), SPAWNER(4, "MinecartSpawner"), HOPPER(5, "MinecartHopper"), COMMAND_BLOCK(6, "MinecartCommandBlock");

        private static final Map<Integer, EntityMinecartAbstract.EnumMinecartType> h = Maps.newHashMap();
        private final int i;
        private final String j;

        private EnumMinecartType(int i, String s) {
            this.i = i;
            this.j = s;
        }

        public int a() {
            return this.i;
        }

        public String b() {
            return this.j;
        }

        static {
            EntityMinecartAbstract.EnumMinecartType[] aentityminecartabstract_enumminecarttype = values();
            int i = aentityminecartabstract_enumminecarttype.length;

            for (int j = 0; j < i; ++j) {
                EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype = aentityminecartabstract_enumminecarttype[j];

                EntityMinecartAbstract.EnumMinecartType.h.put(Integer.valueOf(entityminecartabstract_enumminecarttype.a()), entityminecartabstract_enumminecarttype);
            }

        }
    }

    // CraftBukkit start - Methods for getting and setting flying and derailed velocity modifiers
    public Vector getFlyingVelocityMod() {
        return new Vector(flyingX, flyingY, flyingZ);
    }

    public void setFlyingVelocityMod(Vector flying) {
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    public Vector getDerailedVelocityMod() {
        return new Vector(derailedX, derailedY, derailedZ);
    }

    public void setDerailedVelocityMod(Vector derailed) {
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }
    // CraftBukkit end
}
