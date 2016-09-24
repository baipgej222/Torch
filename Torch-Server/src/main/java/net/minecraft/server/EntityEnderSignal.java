package net.minecraft.server;

public class EntityEnderSignal extends Entity {

    private double a;
    private double b;
    private double c;
    private int d;
    private boolean e;

    public EntityEnderSignal(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    protected void i() {}

    public EntityEnderSignal(World world, double d0, double d1, double d2) {
        super(world);
        this.d = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d0, d1, d2);
    }

    public void a(BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        int i = blockposition.getY();
        double d1 = (double) blockposition.getZ();
        double d2 = d0 - this.locXAtom.get();
        double d3 = d1 - this.locZAtom.get();
        float f = MathHelper.sqrt(d2 * d2 + d3 * d3);

        if (f > 12.0F) {
            this.a = this.locXAtom.get() + d2 / (double) f * 12.0D;
            this.c = this.locZAtom.get() + d3 / (double) f * 12.0D;
            this.b = this.locYAtom.get() + 8.0D;
        } else {
            this.a = d0;
            this.b = (double) i;
            this.c = d1;
        }

        this.d = 0;
        this.e = this.random.nextInt(5) > 0;
    }

    public void m() {
        this.M = this.locXAtom.get();
        this.N = this.locYAtom.get();
        this.O = this.locZAtom.get();
        super.m();
        this.locXAtom.addAndGet(this.motXAtom.get());
        this.locYAtom.addAndGet(this.motYAtom.get());
        this.locZAtom.addAndGet(this.motZAtom.get());
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
        if (!this.world.isClientSide) {
            double d0 = this.a - this.locXAtom.get();
            double d1 = this.c - this.locZAtom.get();
            float f1 = (float) Math.sqrt(d0 * d0 + d1 * d1);
            float f2 = (float) MathHelper.b(d1, d0);
            double d2 = (double) f + (double) (f1 - f) * 0.0025D;

            if (f1 < 1.0F) {
                d2 *= 0.8D;
                this.motYAtom.set(this.motYAtom.get() * 0.8D);
            }

            this.motXAtom.set(Math.cos((double) f2) * d2);
            this.motZAtom.set(Math.sin((double) f2) * d2);
            if (this.locYAtom.get() < this.b) {
                this.motYAtom.addAndGet((1.0D - this.motYAtom.get()) * 0.014999999664723873D);
            } else {
                this.motYAtom.addAndGet((-1.0D - this.motYAtom.get()) * 0.014999999664723873D);
            }
        }

        float f3 = 0.25F;

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locXAtom.get() - this.motXAtom.get() * (double) f3, this.locYAtom.get() - this.motYAtom.get() * (double) f3, this.locZAtom.get() - this.motZAtom.get() * (double) f3, this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get(), new int[0]);
            }
        } else {
            this.world.addParticle(EnumParticle.PORTAL, this.locXAtom.get() - this.motXAtom.get() * (double) f3 + this.random.nextDouble() * 0.6D - 0.3D, this.locYAtom.get() - this.motYAtom.get() * (double) f3 - 0.5D, this.locZAtom.get() - this.motZAtom.get() * (double) f3 + this.random.nextDouble() * 0.6D - 0.3D, this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get(), new int[0]);
        }

        if (!this.world.isClientSide) {
            this.setPosition(this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get());
            ++this.d;
            if (this.d > 80 && !this.world.isClientSide) {
                this.die();
                if (this.e) {
                    this.world.addEntity(new EntityItem(this.world, this.locXAtom.get(), this.locYAtom.get(), this.locZAtom.get(), new ItemStack(Items.ENDER_EYE)));
                } else {
                    this.world.triggerEffect(2003, new BlockPosition(this), 0);
                }
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {}

    public void a(NBTTagCompound nbttagcompound) {}

    public float e(float f) {
        return 1.0F;
    }

    public boolean aT() {
        return false;
    }
}
