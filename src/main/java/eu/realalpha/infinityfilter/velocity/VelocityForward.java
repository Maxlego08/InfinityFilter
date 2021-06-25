package eu.realalpha.infinityfilter.velocity;

import eu.realalpha.infinityfilter.ForwardConnection;
import eu.realalpha.infinityfilter.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class VelocityForwardConnection implements ForwardConnection {


    private static final Field HANDSHAKE_FIELD;
    private static final Field HOSTNAME_FIELD;
    private static final Field CLEANED_ADDRESS_FIELD;
    private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;
    private static final Field MINECRAFT_CONNECTION_FIELD;
    private static final Field REMOTE_ADDRESS_FIELD;
    private static final Field LEGACY_MINECRAFT_CONNECTION_FIELD;
    private static final Method CLOSE_CHANNEL_METHOD;

    static {
        try {
            INITIAL_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");

            HANDSHAKE_FIELD = ReflectionUtils.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "handshake");
            HOSTNAME_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.protocol.packet.Handshake"), "serverAddress");
            CLEANED_ADDRESS_FIELD = ReflectionUtils.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "cleanedAddress");
            MINECRAFT_CONNECTION_FIELD = ReflectionUtils.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "connection");
            LEGACY_MINECRAFT_CONNECTION_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection"), "connection");

            Class<?> minecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
            REMOTE_ADDRESS_FIELD = ReflectionUtils.getPrivateField(minecraftConnection, "remoteAddress");
            CLOSE_CHANNEL_METHOD = minecraftConnection.getMethod("close");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setAddress(InetSocketAddress inetSocketAddress) {

    }

    @Override
    public void disconnect() {

    }
}
