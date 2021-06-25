package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public class DefaultForwardConnection implements ForwardConnection{

    private String token;
    private String host;
    private InetSocketAddress inetSocketAddress;

    public DefaultForwardConnection(String s) {
        String[] strings = s.split(Pattern.quote("\\\\"));
        String[] address = strings[2].split(":");
        String hostname = address[0];
        int port = Integer.parseInt(address[1]);
        this.inetSocketAddress = new InetSocketAddress(hostname, port);
        this.host = strings[0];
        this.token = strings[1];
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }
}
