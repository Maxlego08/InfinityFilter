package eu.realalpha.infinityfilter.spigot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ForwardContext;
import org.bukkit.entity.Player;

import javax.management.ReflectionException;
import java.util.logging.Level;

public class SetProtocolHandler extends PacketAdapter {

	private final FilterSpigot filterSpigot;

	public SetProtocolHandler(FilterSpigot filterSpigot) {
		super(filterSpigot, ListenerPriority.NORMAL, PacketType.Handshake.Client.SET_PROTOCOL);
		this.filterSpigot = filterSpigot;
	}

	static {
		try {
			Class.forName("io.netty.channel.AbstractChannel");
		} catch (ClassNotFoundException e) {
			try {
				Class.forName("net.minecraft.util.io.netty.channel.AbstractChannel");
			} catch (ClassNotFoundException e2) {
				throw new RuntimeException(new ReflectionException(e2));
			}

		}
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		Player player = event.getPlayer();
		if (packet.getProtocols().read(0) == PacketType.Protocol.LOGIN) {
			try {
				String hostname = packet.getStrings().read(0);
				boolean hasToken = hostname.contains(filterSpigot.getKey());
				ForwardContext forwardContext = (hasToken ? ForwardContext.of(hostname) : ForwardContext.empty());
				Forward forward = new SpigotForward(player);
				if (hasToken) {
					forward.setAddress(forwardContext.getInetSocketAddress());
					packet.getStrings().write(0, forwardContext.getHost());
				} else if (filterSpigot.isOnlineMode())
					forward.disconnect();
			} catch (Exception e) {
				this.getPlugin().getLogger().log(Level.SEVERE, e, () -> "");
			}
		}

	}
}
