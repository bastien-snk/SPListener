package dev.fls.spl;

import io.netty.channel.*;
import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * <b>SPL (Simple Packet Listener)</b>
 * <p/>
 * This library was created by _FearLessS (@rootxls) and allows you to listen and modify incoming and outgoing packets on a Bukkit/Spigot server and all their forks.
 * <p/>
 * You are welcome to use it, modify it and redistribute it under the following conditions:
 * <ul>
 * <li>Don't claim this as your own
 * <li>Don't remove this disclaimer
 * </ul>
 * <p/>
 *
 * This class is the parent class of SPL.
 * It is the class that manages the whole packet exchange system and calls the event listeners you add.
 *
 * @
 * @author Bastien S. (github.com/rootxls) & AndrobaL (github.com/Androbal)
 * @since Feb. 2022
 * @version 1.0
 */
@Getter
public final class SPListener implements Listener {

    @Getter
    private static SPListener instance;
    private static boolean registered;

    /**
     * This method is crucial.
     * It is what initializes SPL.
     * Without it, the packet listener cannot function.
     * You can call it at any time, but we advise you to call it in your onEnable.
     *
     * @param plugin
     */
    public static void register(JavaPlugin plugin) {
        if(registered) return;
        instance = new SPListener(plugin);
        registered = true;
    }

    /**
     * This map contains all PacketListener class instances with their methods that listen to packets.
     */
    private final Map<PacketListener, List<Method>> listeners = new HashMap<>();

    private SPListener(JavaPlugin plugin) {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * This method registers a {@link PacketListener} class so that SPL listens for all incoming and outgoing packet events
     *
     * @param listener
     */
    public static void registerListener(PacketListener listener) {
        if(!registered) return;
        List<Method> methods = new ArrayList<>();

        for(Method method : listener.getClass().getMethods()) {
            if(!method.isAnnotationPresent(PacketHandler.class)) continue;

            methods.add(method);
        }

        getInstance().getListeners().put(listener, methods);
    }

    /**
     * This method is called when a packet is received or sent.
     * It can be used to call any method that contains the {@link PacketHandler} annotation
     *
     * @param packet The packet that is exchanged
     * @param player The player with whom the server exchanges the packet.
     * @return true if packet is cancelled
     */
    private boolean handlePacket(Packet packet, Player player) {
        boolean cancel = false;

        for(Map.Entry<PacketListener, List<Method>> entry : listeners.entrySet()) {
            PacketListener listener = entry.getKey();
            List<Method> methods = entry.getValue();

            for(Method method : methods) {
                PacketHandler annotation = method.getAnnotation(PacketHandler.class);
                if(annotation == null) continue;

                for(Class<? extends Packet> packetClass : annotation.packet())  {
                    if(!packet.getClass().equals(packetClass)) continue;
                    try {
                        PacketEvent event = new PacketEvent(player, packet);

                        method.setAccessible(true);
                        method.invoke(listener, event);
                        method.setAccessible(false);

                        // Permet de mettre en true uniquement quand cancel
                        if (event.isCancelled()) {
                            cancel = true;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return cancel;
    }

    /**
     * This event listener will register the player in the list of packet exchange targets so that it knows when packets are exchanged.
     * @param event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
               if (handlePacket((Packet) o, player)) {
                    return;
                }
                super.write(channelHandlerContext, o, channelPromise);
            }

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                if (handlePacket((Packet) o, player)) {
                    return;
                }
                super.channelRead(channelHandlerContext, o);
            }
        };

        //ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.m.pipeline();
        pipeline.addBefore("packet_handler", "lyramc_packetlistener", duplexHandler);
    }

    /**
     * This event listener will unregister the player in the list of packet exchange targets.
     *
     * @param event
     */
    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        Channel channel = ((CraftPlayer) player).getHandle().b.a.m;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
        });
    }
}