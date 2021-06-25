package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;

public interface Forward {

    void setAddress(InetSocketAddress inetSocketAddress);

    void disconnect();


}
