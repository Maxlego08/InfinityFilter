package eu.realalpha.infinityfilter.bungeecord;

import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.IpLoader;
import eu.realalpha.infinityfilter.ReflectionUtils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class SetProtocolHandler implements Listener {

	private final FilterBungee filterBungee;
	private final IpLoader ipLoader = new IpLoader();

	public SetProtocolHandler(FilterBungee filterBungee) {
		this.filterBungee = filterBungee;
		this.ipLoader.fetchIps();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerHandshake(PlayerHandshakeEvent event) {
		String rawData = event.getHandshake().getHost();
		PendingConnection pendingConnection = event.getConnection();

		boolean hasToken = rawData.contains(this.filterBungee.getKey());
		ForwardContext forwardContext = (hasToken ? ForwardContext.of(rawData) : ForwardContext.empty());
		Forward forward = new BungeeForward(pendingConnection, forwardContext);
		if (hasToken) {
			forward.setAddress(forwardContext.getInetSocketAddress());
			try {
				ReflectionUtils.setField(event.getHandshake(), "host", forwardContext.getHost());
			} catch (IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			String currentIp = event.getConnection().getSocketAddress().toString().split(":")[0].substring(1);

			if (this.ipLoader.canPing(currentIp))
				return;

			if (!this.filterBungee.allowExertalConnexion())
				forward.disconnect();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPingEvent(ProxyPingEvent event) {

		PendingConnection connection = event.getConnection();

		String currentIp = connection.getSocketAddress().toString().split(":")[0].substring(1);
		this.ipLoader.fetchIps();

		if (this.ipLoader.canPing(currentIp))
			return;

		if (!this.filterBungee.allowExertalConnexion())
			connection.disconnect();
	}

}
