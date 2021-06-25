package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;

public interface ForwardContext {

    String getToken();

    String getHost();

    InetSocketAddress getInetSocketAddress();

    static ForwardContext of(String s) {
        return new DefaultForwardContext(s);
    }

    static ForwardContext empty(){
        return new DefaultForwardContext("", "", null);
    }

}
