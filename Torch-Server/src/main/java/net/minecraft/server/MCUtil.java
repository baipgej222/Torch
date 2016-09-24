package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.Waitable;
import org.spigotmc.AsyncCatcher;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class MCUtil {
    private static final Pattern REPLACE_QUOTES = Pattern.compile("\"");
    private static final Executor asyncExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Paper Async Task Handler Thread - %1$d").build());

    private MCUtil() {}


    /**
     * Builds a chat componenent from a string.
     * @param str
     * @return
     */
    public static IChatBaseComponent cmpFromMessage(String str) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + REPLACE_QUOTES.matcher(str).replaceAll("\\\"") + "\"}");
    }

    /**
     * Ensures the target code is running on the main thread
     * @param reason
     * @param run
     * @param <T>
     * @return
     */
    public static <T> T ensureMain(String reason, Supplier<T> run) {
        if (AsyncCatcher.enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread) {
            new IllegalStateException( "Asynchronous " + reason + "! Blocking thread until it returns ").printStackTrace();
            Waitable<T> wait = new Waitable<T>() {
                @Override
                protected T evaluate() {
                    return run.get();
                }
            };
            MinecraftServer.getServer().processQueue.add(wait);
            try {
                return wait.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
        return run.get();
    }

    /**
     * Calculates distance between 2 entities
     * @param e1
     * @param e2
     * @return
     */
    public static double distance(Entity e1, Entity e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }


    /**
     * Calculates distance between 2 block positions
     * @param e1
     * @param e2
     * @return
     */
    public static double distance(BlockPosition e1, BlockPosition e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }

    /**
     * Gets the distance between 2 positions
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Get's the distance squared between 2 entities
     * @param e1
     * @param e2
     * @return
     */
    public static double distanceSq(Entity e1, Entity e2) {
        return distanceSq(e1.locXAtom.get(),e1.locYAtom.get(),e1.locZAtom.get(), e2.locXAtom.get(),e2.locYAtom.get(),e2.locZAtom.get());
    }

    /**
     * Gets the distance sqaured between 2 block positions
     * @param pos1
     * @param pos2
     * @return
     */
    public static double distanceSq(BlockPosition pos1, BlockPosition pos2) {
        return distanceSq(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    /**
     * Gets the distance squared between 2 positions
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }

    /**
     * Converts a NMS World/BlockPosition to Bukkit Location
     * @param world
     * @param pos
     * @return
     */
    public static Location toLocation(World world, BlockPosition pos) {
        return new Location(world.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Converts an NMS entity's current location to a Bukkit Location
     * @param entity
     * @return
     */
    public static Location toLocation(Entity entity) {
        return new Location(entity.getWorld().getWorld(), entity.locXAtom.get(), entity.locYAtom.get(), entity.locZAtom.get());
    }

    public static BlockPosition toBlockPosition(Location loc) {
        return new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static boolean isEdgeOfChunk(BlockPosition pos) {
        final int modX = pos.getX() & 15;
        final int modZ = pos.getZ() & 15;
        return (modX == 0 || modX == 15 || modZ == 0 || modZ == 15);
    }

    /**
     * Gets a chunk without changing its boolean for should unload
     * @param world
     * @param x
     * @param z
     * @return
     */
    @Nullable public static Chunk getLoadedChunkWithoutMarkingActive(World world, int x, int z) {
        return ((ChunkProviderServer) world.chunkProvider).chunks.get(ChunkCoordIntPair.a(x, z));
    }

    /**
     * Gets a chunk without changing its boolean for should unload
     * @param provider
     * @param x
     * @param z
     * @return
     */
    @Nullable public static Chunk getLoadedChunkWithoutMarkingActive(IChunkProvider provider, int x, int z) {
        return ((ChunkProviderServer)provider).chunks.get(ChunkCoordIntPair.a(x, z));
    }

    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static void scheduleAsyncTask(Runnable run) {
        asyncExecutor.execute(run);
    }
}
