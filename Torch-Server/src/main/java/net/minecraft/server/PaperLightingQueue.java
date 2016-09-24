package net.minecraft.server;

import co.aikar.timings.Timing;
import java.util.ArrayDeque;

class PaperLightingQueue {
    private static final long MAX_TIME = (long) (1000000000 / 20 * .95);
    private static int updatesThisTick;


    static void processQueue(long curTime) {
        updatesThisTick = 0;

        final long startTime = System.nanoTime();
        final long maxTickTime = MAX_TIME - (startTime - curTime);

        START:
        for (World world : MinecraftServer.getServer().worlds) {
            if (!world.paperConfig.queueLightUpdates) {
                continue;
            }

            for (Chunk chunk : ((WorldServer) world).getChunkProviderServer().chunks.values()) {
                if (chunk.lightingQueue.processQueue(startTime, maxTickTime)) {
                    break START;
                }
            }
        }
    }

    static class LightingQueue extends ArrayDeque<Runnable> {
        final private Chunk chunk;

        LightingQueue(Chunk chunk) {
            super();
            this.chunk = chunk;
        }

        @Override
        public boolean add(Runnable runnable) {
            if (chunk.world.paperConfig.queueLightUpdates) {
                return super.add(runnable);
            }
            runnable.run();
            return true;
        }

        /**
         * Processes the lighting queue for this chunk
         *
         * @param startTime If start Time is 0, we will not limit execution time
         * @param maxTickTime Maximum time to spend processing lighting updates
         * @return true to abort processing furthur lighting updates
         */
        private boolean processQueue(long startTime, long maxTickTime) {
            if (this.isEmpty()) {
                return false;
            }
            try (Timing ignored = chunk.world.timings.lightingQueueTimer.startTiming()) {
                Runnable lightUpdate;
                while ((lightUpdate = this.poll()) != null) {
                    lightUpdate.run();
                    if (startTime > 0 && ++PaperLightingQueue.updatesThisTick % 10 == 0 && PaperLightingQueue.updatesThisTick > 10) {
                        if (System.nanoTime() - startTime > maxTickTime) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        /**
         * Flushes lighting updates to unload the chunk
         */
        void processUnload() {
            if (!chunk.world.paperConfig.queueLightUpdates) {
                return;
            }
            processQueue(0, 0); // No timeout

            final int radius = 1; // TODO: bitflip, why should this ever be 2?
            for (int x = chunk.locX - radius; x <= chunk.locX + radius; ++x) {
                for (int z = chunk.locZ - radius; z <= chunk.locZ + radius; ++z) {
                    if (x == chunk.locX && z == chunk.locZ) {
                        continue;
                    }

                    Chunk neighbor = MCUtil.getLoadedChunkWithoutMarkingActive(chunk.world, x, z);
                    if (neighbor != null) {
                        neighbor.lightingQueue.processQueue(0, 0); // No timeout
                    }
                }
            }
        }
    }
}
