package eu.realalpha.infinityfilter.spigot;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.management.ReflectionException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.injector.server.SocketInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;

import eu.realalpha.infinityfilter.Forward;
import eu.realalpha.infinityfilter.ReflectionUtils;

public class SpigotForward implements Forward {

	private static Class<?> abstractChannelClass;

	static {
		try {
			abstractChannelClass = Class.forName("io.netty.channel.AbstractChannel");
		} catch (ClassNotFoundException e) {
			try {
				abstractChannelClass = Class.forName("net.minecraft.util.io.netty.channel.AbstractChannel");
			} catch (ClassNotFoundException e2) {
				throw new RuntimeException(new ReflectionException(e2));
			}
		}
	}

	private Player player;

	public SpigotForward(Player player) {
		this.player = player;
	}

	@Override
	public void setAddress(InetSocketAddress inetSocketAddress) {
		SocketInjector ignored = TemporaryPlayerFactory.getInjectorFromPlayer(player);
		try {
			Object injector = ReflectionUtils.getObjectInPrivateField(ignored, "injector");
			Object networkManager = ReflectionUtils.getObjectInPrivateField(injector, "networkManager");

			ReflectionUtils.setFinalField(networkManager,
					ReflectionUtils.searchFieldByClass(networkManager.getClass(), SocketAddress.class),
					inetSocketAddress);

			Object channel = ReflectionUtils.getObjectInPrivateField(injector, "originalChannel");
			ReflectionUtils.setFinalField(channel,
					ReflectionUtils.getDeclaredField(abstractChannelClass, "remoteAddress"), inetSocketAddress);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		player.kickPlayer("");
	}
}
