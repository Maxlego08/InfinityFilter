package eu.realalpha.infinityfilter.bungeecord;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.ReflectionUtils;
import io.netty.channel.AbstractChannel;
import net.md_5.bungee.api.connection.PendingConnection;

public class BungeeForward implements Forward {

	private final PendingConnection pendingConnection;
	private final ForwardContext forwardContext;

	/**
	 * @param pendingConnection
	 * @param forwardContext
	 */
	public BungeeForward(PendingConnection pendingConnection, ForwardContext forwardContext) {
		super();
		this.pendingConnection = pendingConnection;
		this.forwardContext = forwardContext;
	}

	@Override
	public void setAddress(InetSocketAddress inetSocketAddress) {
		InetSocketAddress address = new InetSocketAddress(forwardContext.getHost(), forwardContext.getPort());
		
		try {
			Object channelWrapper = ReflectionUtils.getObjectInPrivateField(pendingConnection, "ch");
			Object channel = ReflectionUtils.getObjectInPrivateField(channelWrapper, "ch");

			try {
				Field socketAddressField = ReflectionUtils.searchFieldByClass(channelWrapper.getClass(),
						SocketAddress.class);
				ReflectionUtils.setFinalField(channelWrapper, socketAddressField, inetSocketAddress);
			} catch (IllegalArgumentException ignored) {
				// Some BungeeCord versions, notably those on 1.7 (e.g.
				// zBungeeCord) don't have an SocketAddress field in the
				// ChannelWrapper class
			}

			ReflectionUtils.setFinalField(channel,
					ReflectionUtils.getPrivateField(AbstractChannel.class, "remoteAddress"), address);
			ReflectionUtils.setFinalField(channel,
					ReflectionUtils.getPrivateField(AbstractChannel.class, "localAddress"), address);

		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		try {
			InetSocketAddress virtualHost = InetSocketAddress.createUnresolved(forwardContext.getHost(),
					forwardContext.getPort());
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
