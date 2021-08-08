package eu.realalpha.infinityfilter;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IpLoader {

	private final String apiURL = "https://www.infinity-filter.com/ips/";
	private final int cooldownSecond = 60 * 10;

	private final List<String> ips = new ArrayList<String>();
	private long lastFetchCooldown = 0;

	public void fetchIps() {
		if (System.currentTimeMillis() > this.lastFetchCooldown) {
			try {
				this.ips.clear();
				URL url = new URL(apiURL);
				URLConnection hc;
				hc = url.openConnection();
				hc.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				Scanner scanner = new Scanner(hc.getInputStream());
				while (scanner.hasNext()) {
					String currentString = scanner.next();
					if (!currentString.startsWith("<")) {
						String ip = currentString.replace("</br>", "");
						this.ips.add(ip);
					}
				}
				scanner.close();
				lastFetchCooldown = System.currentTimeMillis() + (1000 * cooldownSecond);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Check if ip is in list
	 * 
	 * @param ip
	 * @return boolean
	 */
	public boolean canPing(String ip) {
		return this.ips.contains(ip);
	}

}
