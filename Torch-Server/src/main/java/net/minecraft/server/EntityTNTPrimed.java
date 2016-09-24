package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityTNTPrimed extends Entity {

    private static final DataWatcherObject<Integer> FUSE_TICKS = DataWatcher.a(EntityTNTPrimed.class, DataWatcherRegistry.b);
    private EntityLiving source;
    private int c;
    public float yield = 4; // CraftBukkit - add field
    public boolean isIncendiary = false; // CraftBukkit - add field

    public EntityTNTPrimed(World world) {
        super(world);
        this.c = 80;
        this.i = true;
        this.setSize(0.98F, 0.98F);
    }

    public EntityTNTPrimed(World world, double d0, double d1, double d2, EntityLiving entityliving) {
        this(world);
        this.setPosition(d0, d1, d2);
        float f = (float) (Math.random() * 6.2831854820251465D);

        this.motXAtom.set((double) (-((float) Math.sin((double) f)) * 0.02F));
        this.motYAtom.set(0.20000000298023224D);
        this.motZAtom.set((double) (-((float) Math.cos((double) f)) * 0.02F));
        this.setFuseTicks(80);
        this.lastXAtom.set(d0);
        this.lastYAtom.set(d1);
        this.lastZAtom.set(d2);
        this.source = entityliving;
        if (world.paperConfig.oldCannonBehaviors) this.motXAtom.set(0.0F); this.motZAtom.set(0.0F); // Paper - Old TNT cannon behaviors
    }

    protected void i() {
        this.datawatcher.register(EntityTNTPrimed.FUSE_TICKS, Integer.valueOf(80));
    }

    protected boolean playStepSound() {
        return false;
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public void m() {
        if (world.spigotConfig.currentPrimedTnt++ > world.spigotConfig.maxTntTicksPerTick) { return; } // Spigot
        this.lastXAtom = this.locXAtom;
        this.lastYAtom = this.locYAtom;
        this.lastZAtom = this.locZAtom;
        this.motYAtom.set(this.motYAtom.get() - 0.03999999910593033D);
        this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());

        // Paper start - Configurable TNT entity height nerf
        if (this.world.paperConfig.entityTNTHeightNerf != 0 && this.locYAtom.get() > this.world.paperConfig.entityTNTHeightNerf) {
            this.die();
        }
        // Paper end

        this.motXAtom.set(this.motXAtom.get() * 0.9800000190734863D);
        this.motYAtom.set(this.motYAtom.get() * 0.9800000190734863D);
        this.motZAtom.set(this.motZAtom.get() * 0.9800000190734863D);
        if (this.onGround) {
        	this.motXAtom.set(this.motXAtom.get() * 0.699999988079071D);
        	this.motZAtom.set(this.motZAtom.get() * 0.699999988079071D);
        	this.motYAtom.set(this.motYAtom.get() * -0.5D);
        }

        --this.c;
        if (this.c <= 0) {
            // CraftBukkit start - Need to reverse the order of the explosion and the entity death so we have a location for the event
            // this.die();
            if (!this.world.isClientSide) {
                this.explode();
            }
            this.die();
            // CraftBukkit end
        } else {
            this.aj();
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locXAtom.get(), this.locYAtom.get() + 0.5D, this.locZAtom.get(), 0.0D, 0.0D, 0.0D, new int[0]); // Torch
        }

    }

    private void explode() {
        // CraftBukkit start
        // float f = 4.0F;

        org.bukkit.craftbukkit.CraftServer server = this.world.getServer();

        ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(server, this));
        server.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.world.createExplosion(this, this.locXAtom.get(), this.locYAtom.get() + (double) (this.length / 2.0F), this.locZAtom.get(), event.getRadius(), event.getFire(), true); // Torch
        }
        // CraftBukkit end
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Fuse", (short) this.getFuseTicks());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.setFuseTicks(nbttagcompound.getShort("Fuse"));
        // Paper start - Try and load origin location from the old NBT tags for backwards compatibility
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            int srcX = nbttagcompound.getInt("SourceLoc_x");
            int srcY = nbttagcompound.getInt("SourceLoc_y");
            int srcZ = nbttagcompound.getInt("SourceLoc_z");
            origin = new org.bukkit.Location(world.getWorld(), srcX, srcY, srcZ);
        }
        // Paper end
    }

    public EntityLiving getSource() {
        return this.source;
    }

    public float getHeadHeight() {
        return world.paperConfig.oldCannonBehaviors ? this.length / 2 : 0.0F; // Paper - Old TNT cannon behaviors
    }

    public void setFuseTicks(int i) {
        this.datawatcher.set(EntityTNTPrimed.FUSE_TICKS, Integer.valueOf(i));
        this.c = i;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityTNTPrimed.FUSE_TICKS.equals(datawatcherobject)) {
            this.c = this.k();
        }

    }

    public int k() {
        return ((Integer) this.datawatcher.get(EntityTNTPrimed.FUSE_TICKS)).intValue();
    }

    public int getFuseTicks() {
        return this.c;
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

    @Override
    public boolean pushedByWater() {
        return !world.paperConfig.oldCannonBehaviors && super.pushedByWater();
    }

    /**
     * Author: Jedediah Smith <jedediah@silencegreys.com>
     */
    @Override
    public boolean doWaterMovement() {
        if (!world.paperConfig.oldCannonBehaviors) return super.doWaterMovement();

        // Preserve velocity while calling the super method
        double oldMotX = this.motXAtom.get();
        double oldMotY = this.motYAtom.get();
        double oldMotZ = this.motZAtom.get();

        super.doWaterMovement();

        this.motXAtom.set(oldMotX);
        this.motYAtom.set(oldMotY);
        this.motZAtom.set(oldMotZ);

        if (this.inWater) {
            // Send position and velocity updates to nearby players on every tick while the TNT is in water.
            // This does pretty well at keeping their clients in sync with the server.
            EntityTrackerEntry ete = ((WorldServer) this.getWorld()).getTracker().trackedEntities.get(this.getId());
            if (ete != null) {
                PacketPlayOutEntityVelocity velocityPacket = new PacketPlayOutEntityVelocity(this);
                PacketPlayOutEntityTeleport positionPacket = new PacketPlayOutEntityTeleport(this);

                ete.trackedPlayers.parallelStream().filter(viewer -> (viewer.locXAtom.get() - this.locXAtom.get()) * (viewer.locYAtom.get() - this.locYAtom.get()) * (viewer.locZAtom.get() - this.locZAtom.get()) < 16 * 16).forEach(viewer -> {
                    viewer.playerConnection.sendPacket(velocityPacket);
                    viewer.playerConnection.sendPacket(positionPacket);
                });
            }
        }

        return this.inWater;
    }
    // Paper end
}
