package dev.fls.spl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

/**
 * This class contains the data for a packet exchange event.
 */
@RequiredArgsConstructor
@Getter
@Setter
public class PacketEvent {

    /**
     * The player with whom the packet is exchanged.
     */
    private final Player player;

    /**
     * The packet exchanged.
     */
    private final Packet packet;

    /**
     * Indicates whether the packet exchange should be cancelled
     */
    private boolean isCancelled;

    /**
     * This methods indicates the direction of the packet
     * @return
     */
    public PacketDirection getPacketDirection() {
        if(packet.getClass().getSimpleName().contains("PlayIn")) return PacketDirection.IN;
        if(packet.getClass().getSimpleName().contains("PlayOut")) return PacketDirection.OUT;
        return null;
    }

    public enum PacketDirection {
        IN,
        OUT;
    }
}