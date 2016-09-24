package net.minecraft.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

public class NavigationListener implements IWorldAccess {

    // private final List<NavigationAbstract> a = Lists.newArrayList(); // Torch - remove unused list
    private final Map<EntityInsentient, NavigationAbstract> navigators = new LinkedHashMap<EntityInsentient, NavigationAbstract>(); // Torch - Optimized navigation listener

    public NavigationListener() {}

    // Torch start - Optimized navigation listener
	public void a(final World world, final BlockPosition blockPosition, final IBlockData blockData, final IBlockData blockData2, final int n) {
		if (!a(world, blockPosition, blockData, blockData2)) {
			return;
		}
		for (Entry<EntityInsentient, NavigationAbstract> entry : navigators.entrySet()) {
			NavigationAbstract navigation = entry.getValue();
			if (!navigation.i()) {
				PathEntity pathentity = navigation.k();
				if (pathentity != null && !pathentity.b()) {
					if (pathentity.d() != 0) {
						PathPoint pathpoint = pathentity.c();
						EntityInsentient insentient = entry.getKey();
						if (
							blockPosition.distanceSquared(
								(pathpoint.a + insentient.locXAtom.get()) / 2.0,
								(pathpoint.b + insentient.locYAtom.get()) / 2.0,
								(pathpoint.c + insentient.locZAtom.get()) / 2.0
							) < (pathentity.d() - pathentity.e()) * (pathentity.d() - pathentity.e())
						) {
							navigation.j();
						}
					}
				}
			}
		}
	}
	// Torch end

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        AxisAlignedBB axisalignedbb = iblockdata.d(world, blockposition);
        AxisAlignedBB axisalignedbb1 = iblockdata1.d(world, blockposition);

        return axisalignedbb != axisalignedbb1 && (axisalignedbb == null || !axisalignedbb.equals(axisalignedbb1));
    }

    public void a(BlockPosition blockposition) {}

    public void a(int i, int j, int k, int l, int i1, int j1) {}

    public void a(@Nullable EntityHuman entityhuman, SoundEffect soundeffect, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1) {}

    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}
    
    // Torch start - Optimized navigation listener
	public void a(Entity entity) {
		if (entity instanceof EntityInsentient) {
			EntityInsentient insentient = (EntityInsentient) entity;
			NavigationAbstract navigation = insentient.getNavigation();
			if (navigation != null) {
				navigators.put(insentient, navigation);
			}
		}
	}

	public void b(Entity entity) {
		navigators.remove(entity);
	}
	// Torch end

    public void a(SoundEffect soundeffect, BlockPosition blockposition) {}

    public void a(int i, BlockPosition blockposition, int j) {}

    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {}

    public void b(int i, BlockPosition blockposition, int j) {}
}
