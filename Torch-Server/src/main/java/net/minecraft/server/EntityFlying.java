package net.minecraft.server;

public abstract class EntityFlying extends EntityInsentient {

    public EntityFlying(World world) {
        super(world);
    }

    public void e(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public void g(float f, float f1) {
        if (this.isInWater()) {
            this.a(f, f1, 0.02F);
            this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());
            this.motXAtom.set(this.motXAtom.get() * 0.800000011920929D);
            this.motYAtom.set(this.motYAtom.get() * 0.800000011920929D);
            this.motZAtom.set(this.motZAtom.get() * 0.800000011920929D);
        } else if (this.an()) {
            this.a(f, f1, 0.02F);
            this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());
            this.motXAtom.set(this.motXAtom.get() * 0.5D);
            this.motYAtom.set(this.motYAtom.get() * 0.5D);
            this.motZAtom.set(this.motZAtom.get() * 0.5D);
        } else {
            float f2 = 0.91F;

            if (this.onGround) {
                f2 = this.world.getType(new BlockPosition(MathHelper.floor(this.locXAtom), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZAtom))).getBlock().frictionFactor * 0.91F;
            }

            float f3 = 0.16277136F / (f2 * f2 * f2);

            this.a(f, f1, this.onGround ? 0.1F * f3 : 0.02F);
            f2 = 0.91F;
            if (this.onGround) {
                f2 = this.world.getType(new BlockPosition(MathHelper.floor(this.locXAtom), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZAtom))).getBlock().frictionFactor * 0.91F;
            }

            this.move(this.motXAtom.get(), this.motYAtom.get(), this.motZAtom.get());
            this.motXAtom.set(this.motXAtom.get() * (double) f2);
            this.motYAtom.set(this.motYAtom.get() * (double) f2);
            this.motZAtom.set(this.motZAtom.get() * (double) f2);
        }

        this.aF = this.aG;
        double d0 = this.locXAtom.get() - this.lastXAtom.get();
        double d1 = this.locZAtom.get() - this.lastZAtom.get();
        float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        this.aG += (f4 - this.aG) * 0.4F;
        this.aH += this.aG;
    }

    public boolean n_() {
        return false;
    }
}
