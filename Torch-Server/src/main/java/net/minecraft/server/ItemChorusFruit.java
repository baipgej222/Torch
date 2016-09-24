package net.minecraft.server;

import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
// CraftBukkit end

public class ItemChorusFruit extends ItemFood {

    public ItemChorusFruit(int i, float f) {
        super(i, f, false);
    }

    @Nullable
    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        ItemStack itemstack1 = super.a(itemstack, world, entityliving);

        if (!world.isClientSide) {
            double d0 = entityliving.locXAtom.get();
            double d1 = entityliving.locYAtom.get();
            double d2 = entityliving.locZAtom.get();

            for (int i = 0; i < 16; ++i) {
                double d3 = entityliving.locXAtom.get() + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.a(entityliving.locYAtom.get() + (double) (entityliving.getRandom().nextInt(16) - 8), 0.0D, (double) (world.Z() - 1));
                double d5 = entityliving.locZAtom.get() + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;

                // CraftBukkit start
                if (entityliving instanceof EntityPlayer) {
                    Player player = ((EntityPlayer) entityliving).getBukkitEntity();
                    PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), new Location(player.getWorld(), d3, d4, d5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
                    world.getServer().getPluginManager().callEvent(teleEvent);
                    if (teleEvent.isCancelled()) {
                        break;
                    }
                    d3 = teleEvent.getTo().getX();
                    d4 = teleEvent.getTo().getY();
                    d5 = teleEvent.getTo().getZ();
                }

                if (entityliving.k(d3, d4, d5)) {
                    // CraftBukkit end
                    world.a((EntityHuman) null, d0, d1, d2, SoundEffects.af, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entityliving.a(SoundEffects.af, 1.0F, 1.0F);
                    break;
                }
            }

            if (entityliving instanceof EntityHuman) {
                ((EntityHuman) entityliving).db().a(this, 20);
            }
        }

        return itemstack1;
    }
}
