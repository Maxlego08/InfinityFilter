package eu.realalpha.infinityfilter.velocity;

import java.lang.reflect.Field;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.proxy.InboundConnection;

import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.IpLoader;
import eu.realalpha.infinityfilter.ReflectionUtils;

public class SetProtocolHandler {

	private static final Field HANDSHAKE_FIELD;
	private static final Field HOSTNAME_FIELD;
	private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;
	private final IpLoader ipLoader = new IpLoader();

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
		this.ipLoader.fetchIps();
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
			} else {

				String currentIp = event.getConnection().getRemoteAddress().toString().split(":")[0].substring(1);

				this.ipLoader.fetchIps();
				
				if (this.ipLoader.canPing(currentIp))
					return;				

				if (!this.filterVelocity.allowExertalConnexion())
					velocityForward.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*@Subscribe(order = PostOrder.FIRST)
	public void onPing(ProxyPingEvent event) {
		InboundConnection connection = event.getConnection();

		String currentIp = connection.getRemoteAddress().toString().split(":")[0].substring(1);
		System.out.println("je ping ! - " + currentIp);
		this.ipLoader.fetchIps();

		if (this.ipLoader.canPing(currentIp))
			return;

		if (!this.filterVelocity.allowExertalConnexion())
			;
	}*/

}
