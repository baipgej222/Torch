package net.minecraft.server;

public class BlockSlime extends BlockHalfTransparent {

    public BlockSlime() {
        super(Material.CLAY, false, MaterialMapColor.c);
        this.a(CreativeModeTab.c);
        this.frictionFactor = 0.8F;
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        if (entity.isSneaking()) {
            super.fallOn(world, blockposition, entity, f);
        } else {
            entity.e(f, 0.0F);
        }

    }

    public void a(World world, Entity entity) {
        if (entity.isSneaking()) {
            super.a(world, entity);
        } else if (entity.motYAtom.get() < 0.0D) {
            entity.motYAtom.set(-entity.motYAtom.get());
            if (!(entity instanceof EntityLiving)) {
                entity.motYAtom.set(entity.motYAtom.get() * 0.8D);
            }
        }

    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (Math.abs(entity.motYAtom.get()) < 0.1D && !entity.isSneaking()) {
            double d0 = 0.4D + Math.abs(entity.motYAtom.get()) * 0.2D;

            entity.motXAtom.set(entity.motXAtom.get() * d0);
            entity.motZAtom.set(entity.motZAtom.get() * d0);
        }

        super.stepOn(world, blockposition, entity);
    }
}
