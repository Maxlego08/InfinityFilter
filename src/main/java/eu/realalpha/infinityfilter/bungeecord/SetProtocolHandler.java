package eu.realalpha.infinityfilter.bungeecord;

import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import eu.realalpha.infinityfilter.ReflectionUtils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class SetProtocolHandler implements Listener {

    private final FilterBungee filterBungee;

    public SetProtocolHandler(FilterBungee filterBungee) {
        this.filterBungee = filterBungee;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHandshake(PlayerHandshakeEvent event) {
        String rawData = event.getHandshake().getHost();
        PendingConnection pendingConnection = event.getConnection();
        if (event.getHandshake().getRequestedProtocol() == 2) {
            boolean hasToken = rawData.contains(filterBungee.getKey());
            ForwardContext forwardContext = (hasToken ? ForwardContext.of(rawData) : ForwardContext.empty());
            Forward forward = new BungeeForward(pendingConnection, forwardContext);
            if (hasToken) {
                forward.setAddress(forwardContext.getInetSocketAddress());
                try {
                    ReflectionUtils.setField(event.getHandshake(), "host", forwardContext.getHost());
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else if (filterBungee.isOnlineMode()) forward.disconnect();
        }
    }
}
