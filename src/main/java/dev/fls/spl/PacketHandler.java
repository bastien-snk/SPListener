package dev.fls.spl;

import net.minecraft.network.protocol.Packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to register a method as a Packet Listener. Without this annotation, a packet listener will never be executed.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketHandler {

    Class<? extends Packet>[] packet() default Packet.class;

}