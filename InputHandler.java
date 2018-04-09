package com.ashishp.dc.assignment.cl.handler;

import java.util.Scanner;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public abstract class InputHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InputHandler.class);

	private static final String SEPARATOR = ",";

	private static final Pattern INTEGER = Pattern.compile("^-?\\d+$");

	/**
	 * Wait for user input indefinitely, which consist of method name and sequence
	 * of parameters, separated by SEPARATOR
	 * <p>
	 * Supported parameter types are: - Integer - String
	 * <p>
	 * Description: methodName,parameter1,parameter2 Example: sayLouderNTimes,5,I
	 * ate pillow
	 *
	 * @param className
	 *            of the class to invoke public static methods in
	 */
	public static void readInput(@NotNull String className) {
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String[] commands = scanner.nextLine().split(SEPARATOR);
			Object[] params = new Object[commands.length - 1];
			Class<?>[] methodParameterTypes = new Class<?>[commands.length - 1];
			for (int i = 1; i < commands.length; i++) {
				int param = i - 1;
				if (INTEGER.matcher(commands[i]).find()) {
					params[param] = Integer.parseInt(commands[i]);
					methodParameterTypes[param] = int.class;
				} else {
					params[param] = commands[i];
					methodParameterTypes[param] = String.class;
				}
			}
			// LOGGER.debug("Calling method=" + commands[0] + Arrays.toString(params));
			if ("view".equals(commands[0]) || "snapshot".equals(commands[0])) {
				try {
					LOGGER.debug("Procesing {} request.", commands[0]);
					Class.forName(className).getMethod(commands[0], methodParameterTypes).invoke(null, params);
				} catch (Exception e) {
					LOGGER.error("Input scanner error", e);
				}
			} else {
				LOGGER.warn("{} is invalid Command", commands[0]);
				LOGGER.info("In oredr to view Branch details type 'view'");
				LOGGER.info("In order to create snapshot type 'snapshot'");
			}

		}
	}
}
