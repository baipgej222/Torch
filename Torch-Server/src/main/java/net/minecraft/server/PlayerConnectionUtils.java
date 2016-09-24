package net.minecraft.server;

public class PlayerConnectionUtils {

    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T t0, IAsyncTaskHandler iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isMainThread()) {
        	// Torch start - faster network processing
        	MinecraftServer.schedulePacket(() -> {
        		packet.a(t0);
        		return null;
            });
            // Torch end
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
