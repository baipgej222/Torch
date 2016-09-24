package net.minecraft.server;

import com.google.common.base.Optional;
import javax.annotation.Nullable;

public class EntityFireworks extends Entity {

    public static final DataWatcherObject<Optional<ItemStack>> FIREWORK_ITEM = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.f);
    private int ticksFlown;
    public int expectedLifespan;

    public EntityFireworks(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    // Spigot Start
    @Override
    public void inactiveTick() {
        this.ticksFlown += 1;
        super.inactiveTick();
    }
    // Spigot End

    protected void i() {
        this.datawatcher.register(EntityFireworks.FIREWORK_ITEM, Optional.absent());
    }

    public EntityFireworks(World world, double d0, double d1, double d2, @Nullable ItemStack itemstack) {
        super(world);
        this.ticksFlown = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d0, d1, d2);
        int i = 1;

        if (itemstack != null && itemstack.hasTag()) {
            this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, Optional.of(itemstack));
            NBTTagCompound nbttagcompound = itemstack.getTag();
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Fireworks");

            i += nbttagcompound1.getByte("Flight");
        }

        this.motXAtom.set(this.random.nextGaussian() * 0.001D);
        this.motZAtom.set(this.random.nextGaussian() * 0.001D);
        this.motYAtom.set(0.05D);
        this.expectedLifespan = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public void m() {
        this.M = this.locXAtom.get();
        this.N = this.locYAtom.get();
        this.O = this.locZAtom.get();
        super.m();
        this.motXAtom.set(this.motXAtom.get() * 1.15D);
        this.motZAtom.set(this.motZAtom.get() * 1.15D);
        this.motYAtom.addAndGet(0.04D);
        this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());
        float f = MathHelper.sqrt(this.motXAtom.get() * this.motXAtom.get() + this.motZAtom.get() * this.motZAtom.get());

        this.yaw = (float) (MathHelper.b(this.motXAtom.get(), this.motZAtom.get()) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.b(this.motYAtom.get(), (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
        if (this.ticksFlown == 0 && !this.ad()) {
            this.world.a((EntityHuman) null, this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get(), SoundEffects.br, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.ticksFlown;
        if (this.world.isClientSide && this.ticksFlown % 2 < 2) {
            this.world.addParticle(EnumParticle.FIREWORKS_SPARK, this.locXAtom.get(), this.locYAtom.get() - 0.3D, this.locZAtom.get(), this.random.nextGaussian() * 0.05D, -this.motYAtom.get() * 0.5D, this.random.nextGaussian() * 0.05D, new int[0]);
        }

        if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) this.world.broadcastEntityEffect(this, (byte) 17); // CraftBukkit
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Life", this.ticksFlown);
        nbttagcompound.setInt("LifeTime", this.expectedLifespan);
        ItemStack itemstack = (ItemStack) ((Optional) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM)).orNull();

        if (itemstack != null) {
            NBTTagCompound nbttagcompound1 = itemstack.save(new NBTTagCompound());

            nbttagcompound.set("FireworksItem", nbttagcompound1);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.ticksFlown = nbttagcompound.getInt("Life");
        this.expectedLifespan = nbttagcompound.getInt("LifeTime");
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("FireworksItem");

        if (nbttagcompound1 != null) {
            ItemStack itemstack = ItemStack.createStack(nbttagcompound1);

            if (itemstack != null) {
                this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, Optional.of(itemstack));
            }
        }

    }

    public float e(float f) {
        return super.e(f);
    }

    public boolean aT() {
        return false;
    }
}
