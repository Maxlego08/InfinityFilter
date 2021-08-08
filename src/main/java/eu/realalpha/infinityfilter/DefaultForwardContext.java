package eu.realalpha.infinityfilter;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public class DefaultForwardContext implements ForwardContext {

	private String token;
	private String host;
	private InetSocketAddress inetSocketAddress;
	private final int port;

	public DefaultForwardContext(String token, String host, InetSocketAddress inetSocketAddress) {
		this.token = token;
		this.host = host;
		this.inetSocketAddress = inetSocketAddress;
		this.port = 0;
	}

	public DefaultForwardContext(String s) {
		String[] strings = s.split(Pattern.quote("\\\\"));
		String[] address = strings[2].split(":");
		String hostname = address[0];
		this.port = Integer.parseInt(address[1]);
		this.inetSocketAddress = new InetSocketAddress(hostname, port);
		this.host = hostname;
		this.token = strings[1];
	}

	@Override
	public String getToken() {
		return this.token;
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public InetSocketAddress getInetSocketAddress() {
		return this.inetSocketAddress;
	}

	@Override
	public int getPort() {
		return this.port;
	}
}
