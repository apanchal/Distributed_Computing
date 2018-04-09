package com.ashishp.dc.assignment.cl.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * Configuration of the system.
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class Configuration {

	private static Configuration instance;

	private static final String CONFIG_FILE = "chandy-lamport.conf";

	private final Properties properties = new Properties();

	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

	static {
		instance = new Configuration();
	}

	/**
	 * Create default configuration.
	 */
	private Configuration() {
		super();
		loadConfiguration();
	}

	/**
	 * 
	 * @return instance of system configuration.
	 */
	public static Configuration getInstance() {
		return instance;
	}

	public int getRmiPort() {
		return Integer.parseInt(properties.getProperty("rmi-port"));
	}

	public int getInitialAmount() {
		return Integer.parseInt(properties.getProperty("initial-amount"));
	}

	public int getMinAmount() {
		return Integer.parseInt(properties.getProperty("min-amount"));
	}

	public int getMaxAmount() {
		return Integer.parseInt(properties.getProperty("max-amount"));
	}

	public int getTimeoutFrequency() {
		return Integer.parseInt(properties.getProperty("timeout-frequency"));
	}

	public String getTimeoutUnit() {
		return properties.getProperty("timeout-unit");
	}

	/**
	 * loads configuration file.
	 */
	private void loadConfiguration() {
		LOGGER.debug("Loading Configuration...");
		try {
			properties.load(new FileInputStream(CONFIG_FILE));

		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to find '{}' configuration file. Make sure it is available in class path.",
					CONFIG_FILE, e);
		} catch (IOException e) {
			LOGGER.error("Failed to load '{}' file.", CONFIG_FILE, e);
		}

	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Configuration.class).add("RMI port", getRmiPort())
				.add("INITIAL_BALANCE", getInitialAmount()).add("MIN_AMOUNT", getMinAmount())
				.add("MAX_AMOUNT", getMaxAmount())
				.add("TIMEOUT_FREQUENCY", getTimeoutFrequency())
				.add("TIMEOUT_UNIT", getTimeoutUnit()).toString();
	}
}
