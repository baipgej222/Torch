package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockSnowBlock extends Block {

    protected BlockSnowBlock() {
        super(Material.SNOW_BLOCK);
        // this.a(true); // Paper - snow blocks don't need to tick
        this.a(CreativeModeTab.b);
    }

    @Nullable
    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.SNOWBALL;
    }

    public int a(Random random) {
        return 4;
    }

    // Paper start - snow blocks don't need to tick
    /*
    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.b(EnumSkyBlock.BLOCK, blockposition) > 11) {
            this.b(world, blockposition, world.getType(blockposition), 0);
            world.setAir(blockposition);
        }

    }
    */
    // Paper end
}
