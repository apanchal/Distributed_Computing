package com.ashishp.dc.assignment.cl.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

	public FileHandler() {
		super();
	}

	public String readFile(@NotNull String branchFileName) {
		StringBuilder data = new StringBuilder();
		Path path;
		try {
			LOGGER.debug("Loading Bank Branch Configuration {}", branchFileName);
			Class clazz = getClass();
			LOGGER.debug("Clazz: {}", clazz);
			ClassLoader classLoader = clazz.getClassLoader();
			LOGGER.debug("ClassLoader {}", classLoader);
			URL url = classLoader.getResource(branchFileName);
			LOGGER.debug("File URL : {}", url);
			path = Paths.get(url.toURI());
			Stream<String> lines = Files.lines(path);
			lines.forEach(line -> data.append(line).append(";"));
			LOGGER.debug("{}", data.toString());
			lines.close();

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data.toString();

	}

	// public static void readFile(@NotNull String branchFileName) {
	// try {
	// Class clazz = FileHandler.class;
	// InputStream inputStream = clazz.getResourceAsStream(branchFileName);
	// String data = readFromInputStream(inputStream);
	// LOGGER.debug("data : {}", data);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private static String readFromInputStream(InputStream inputStream) throws
	// IOException {
	// StringBuilder resultStringBuilder = new StringBuilder();
	// try (BufferedReader br = new BufferedReader(new
	// InputStreamReader(inputStream))) {
	// String line;
	// while ((line = br.readLine()) != null) {
	// resultStringBuilder.append(line).append(";");
	// }
	// br.close();
	// }
	// return resultStringBuilder.toString();
	//
	// }
}
