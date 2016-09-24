package net.minecraft.server;

import javax.annotation.Nullable;

public class DragonControllerHover extends AbstractDragonController {

    private Vec3D b;

    public DragonControllerHover(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public void c() {
        if (this.b == null) {
            this.b = new Vec3D(this.a.locXAtom.get(), this.a.locYAtom.get(), this.a.locZAtom.get());
        }

    }

    public boolean a() {
        return true;
    }

    public void d() {
        this.b = null;
    }

    public float f() {
        return 1.0F;
    }

    @Nullable
    public Vec3D g() {
        return this.b;
    }

    public DragonControllerPhase<DragonControllerHover> getControllerPhase() {
        return DragonControllerPhase.k;
    }
}
