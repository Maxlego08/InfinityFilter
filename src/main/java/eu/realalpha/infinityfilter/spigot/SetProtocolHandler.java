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
import java.lang.reflect.Field;
import java.util.logging.Level;

public class SetProtocolHandler extends PacketAdapter {

    private final FilterSpigot filterSpigot;

    public SetProtocolHandler(FilterSpigot filterSpigot) {
        super(filterSpigot, ListenerPriority.NORMAL, PacketType.Handshake.Client.SET_PROTOCOL);
        this.filterSpigot = filterSpigot;
    }

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


    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        Object handle = packet.getHandle();
        Class<?> aClass = handle.getClass();
        if (packet.getProtocols().read(0) == PacketType.Protocol.LOGIN) {
            try {
                Field hostname = aClass.getDeclaredField("hostname");
                hostname.setAccessible(true);
                String rawData = (String) hostname.get(handle);
                boolean hasToken = rawData.contains(filterSpigot.getKey());
                ForwardContext forwardContext = (hasToken ? ForwardContext.of(rawData) : ForwardContext.empty());
                Forward forward = new SpigotForward(player, forwardContext);
                if (hasToken){
                    forward.setAddress(forwardContext.getInetSocketAddress());
                    hostname.set(handle, forwardContext.getHost());
                }else if(filterSpigot.isOnlineMode()) forward.disconnect();
            } catch (Exception e) {
                this.getPlugin().getLogger().log(Level.SEVERE, e, () -> "");
            }
        }

    }
}
