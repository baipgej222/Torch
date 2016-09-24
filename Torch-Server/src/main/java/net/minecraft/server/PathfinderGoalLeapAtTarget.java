package net.minecraft.server;

public class PathfinderGoalLeapAtTarget extends PathfinderGoal {

    EntityInsentient a;
    EntityLiving b;
    float c;

    public PathfinderGoalLeapAtTarget(EntityInsentient entityinsentient, float f) {
        this.a = entityinsentient;
        this.c = f;
        this.a(5);
    }

    public boolean a() {
        this.b = this.a.getGoalTarget();
        if (this.b == null) {
            return false;
        } else {
            double d0 = this.a.h(this.b);

            return d0 >= 4.0D && d0 <= 16.0D ? (!this.a.onGround ? false : this.a.getRandom().nextInt(5) == 0) : false;
        }
    }

    public boolean b() {
        return !this.a.onGround;
    }

    public void c() {
        double d0 = this.b.locXAtom.get() - this.a.locXAtom.get();
        double d1 = this.b.locZAtom.get() - this.a.locZAtom.get();
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1);

        this.a.motXAtom.addAndGet(d0 / (double) f * 0.5D * 0.800000011920929D + this.a.motXAtom.get() * 0.20000000298023224D);
        this.a.motZAtom.addAndGet(d1 / (double) f * 0.5D * 0.800000011920929D + this.a.motZAtom.get() * 0.20000000298023224D);
        this.a.motYAtom.set((double) this.c);
    }
}
