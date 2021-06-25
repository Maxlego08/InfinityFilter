package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;

public interface ForwardConnection {

    default InetSocketAddress getAddress(String s){
        
    }

    void setAddress(InetSocketAddress inetSocketAddress);

    void disconnect();


}
