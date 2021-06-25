package eu.realalpha.infinityfilter.velocity;

import com.velocitypowered.api.proxy.InboundConnection;
import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class VelocityForward implements Forward {


    private static final Field HANDSHAKE_FIELD;
    private static final Field HOSTNAME_FIELD;
    private static final Field CLEANED_ADDRESS_FIELD;
    private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;
    private static final Field MINECRAFT_CONNECTION_FIELD;
    private static final Field REMOTE_ADDRESS_FIELD;
    private static final Field LEGACY_MINECRAFT_CONNECTION_FIELD;
    private static final Method CLOSE_CHANNEL_METHOD;
    private InboundConnection inboundConnection;
    private ForwardContext forwardContext;

    public VelocityForward(InboundConnection inboundConnection, ForwardContext forwardContext) {
        this.inboundConnection = inboundConnection;
        this.forwardContext = forwardContext;
    }

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
        try {
            ReflectionUtils.setFinalField(inboundConnection, CLEANED_ADDRESS_FIELD, inetSocketAddress);

            Object handshake = HANDSHAKE_FIELD.get(inboundConnection);
            HOSTNAME_FIELD.set(handshake, forwardContext.getHost());

            Object minecraftConnection = MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
            REMOTE_ADDRESS_FIELD.set(minecraftConnection, inetSocketAddress);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        boolean legacy = inboundConnection.getClass() != INITIAL_INBOUND_CONNECTION_CLASS;
        try {
            Object minecraftConnection = legacy ? LEGACY_MINECRAFT_CONNECTION_FIELD.get(inboundConnection) : MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
            CLOSE_CHANNEL_METHOD.invoke(minecraftConnection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
