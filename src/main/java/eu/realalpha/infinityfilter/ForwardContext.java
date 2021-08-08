package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;

public interface ForwardContext {

    String getToken();

    String getHost();

    InetSocketAddress getInetSocketAddress();

    int getPort();
    
    static ForwardContext of(String s) {
        return new DefaultForwardContext(s);
    }

    static ForwardContext empty(){
        return new DefaultForwardContext("", "", null);
    }


}
