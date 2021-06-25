package eu.realalpha.infinityfilter.bungeecord;

import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.ReflectionUtils;
import io.netty.channel.AbstractChannel;
import net.md_5.bungee.api.connection.PendingConnection;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class BungeeForward implements Forward {

    private PendingConnection pendingConnection;
    private ForwardContext forwardContext;

    public BungeeForward(PendingConnection pendingConnection, ForwardContext forwardContext) {
        this.pendingConnection = pendingConnection;
        this.forwardContext = forwardContext;
    }

    @Override
    public void setAddress(InetSocketAddress inetSocketAddress) {
        try {
            Object channelWrapper = ReflectionUtils.getObjectInPrivateField(pendingConnection, "ch");
            Object channel = ReflectionUtils.getObjectInPrivateField(channelWrapper, "ch");

            try {
                Field socketAddressField = ReflectionUtils.searchFieldByClass(channelWrapper.getClass(), SocketAddress.class);
                ReflectionUtils.setFinalField(channelWrapper, socketAddressField, inetSocketAddress);
            } catch (IllegalArgumentException ignored) {
                // Some BungeeCord versions, notably those on 1.7 (e.g. zBungeeCord) don't have an SocketAddress field in the ChannelWrapper class
            }

            ReflectionUtils.setFinalField(channel, ReflectionUtils.getPrivateField(AbstractChannel.class, "remoteAddress"), inetSocketAddress);
            ReflectionUtils.setFinalField(channel, ReflectionUtils.getPrivateField(AbstractChannel.class, "localAddress"), inetSocketAddress);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            InetSocketAddress virtualHost = InetSocketAddress.createUnresolved(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
            try {
                ReflectionUtils.setFinalField(pendingConnection, "virtualHost", virtualHost);
            } catch (Exception ex) {
                ReflectionUtils.setFinalField(pendingConnection, "vHost", virtualHost);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        pendingConnection.disconnect();
    }
}
