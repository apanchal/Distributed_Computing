package com.ashishp.dc.assignment.cl.handler;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public abstract class NetworkHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHandler.class);

	private static final String LOCALHOST = "127.0.0.1";

	/**
	 * List all possible IPv4 addresses of the current machine
	 */
	public static void printMachineIPv4() {
		try {
			LOGGER.info("Printing all possible IPv4 of your machine:");
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					String hostAddress = inetAddresses.nextElement().getHostAddress();
					if (hostAddress.contains(".") && !hostAddress.equals(LOCALHOST)) {
						LOGGER.info(hostAddress);
					}
				}
			}
		} catch (SocketException e) {
			LOGGER.error("Failed to get network interface", e);
		}
	}
}
