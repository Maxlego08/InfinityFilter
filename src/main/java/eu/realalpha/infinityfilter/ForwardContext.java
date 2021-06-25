package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;

public interface ForwardConnection {

    String getToken();

    String getHost();

    InetSocketAddress getInetSocketAddress();

}
