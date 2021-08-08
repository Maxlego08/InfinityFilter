package eu.realalpha.infinityfilter.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.ReflectionUtils;

import java.lang.reflect.Field;

public class SetProtocolHandler {

	private static final Field HANDSHAKE_FIELD;
	private static final Field HOSTNAME_FIELD;
	private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;

	static {
		try {
			INITIAL_INBOUND_CONNECTION_CLASS = Class
					.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");
			HANDSHAKE_FIELD = ReflectionUtils.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "handshake");
			HOSTNAME_FIELD = ReflectionUtils.getPrivateField(
					Class.forName("com.velocitypowered.proxy.protocol.packet.Handshake"), "serverAddress");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private FilterVelocity filterVelocity;

	public SetProtocolHandler(FilterVelocity filterVelocity) {
		this.filterVelocity = filterVelocity;
	}

	@Subscribe(order = PostOrder.FIRST)
	public void onHandshake(ConnectionHandshakeEvent event) {
		InboundConnection inboundConnection = event.getConnection();
		try {
			String rawHost = (String) HOSTNAME_FIELD.get(HANDSHAKE_FIELD.get(inboundConnection));
			boolean hasToken = rawHost.contains(filterVelocity.getKey());
			ForwardContext forwardContext = (hasToken ? ForwardContext.of(rawHost) : ForwardContext.empty());
			VelocityForward velocityForward = new VelocityForward(inboundConnection, forwardContext);
			if (hasToken) {
				velocityForward.setAddress(forwardContext.getInetSocketAddress());
			} else if (filterVelocity.isOnlineMode())
				velocityForward.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
