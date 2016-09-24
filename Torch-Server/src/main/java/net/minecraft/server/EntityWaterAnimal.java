package net.minecraft.server;

public abstract class EntityWaterAnimal extends EntityInsentient implements IAnimal {

    public EntityWaterAnimal(World world) {
        super(world);
    }

    public boolean bC() {
        return true;
    }

    public boolean cG() {
        // Paper start - Don't let water mobs spawn in non-water blocks
        // Based around EntityAnimal's implementation
        int i = MathHelper.floor(this.locXAtom);
        int j = MathHelper.floor(this.getBoundingBox().b); // minY of bounding box
        int k = MathHelper.floor(this.locZAtom);
        Block block = this.world.getType(new BlockPosition(i, j, k)).getBlock();

        return block == Blocks.WATER || block == Blocks.FLOWING_WATER;
        // Paper end
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this);
    }

    public int C() {
        return 120;
    }

    protected boolean isTypeNotPersistent() {
        return true;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public void U() {
        int i = this.getAirTicks();

        super.U();
        if (this.isAlive() && !this.isInWater()) {
            --i;
            this.setAirTicks(i);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirTicks(300);
        }

    }

    public boolean be() {
        return false;
    }
}
