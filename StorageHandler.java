package com.ashishp.dc.assignment.cl.handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashishp.dc.assignment.cl.entity.Node;
import com.ashishp.dc.assignment.cl.entity.Snapshot;

/**
 * 
 *  @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public abstract class StorageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageHandler.class);

	private static final String SEPARATOR = ",";

	private static final String STORAGE_FOLDER = "storage";

	/**
	 * Creates/Updates list of nodes snapshots into CSV file
	 *
	 * @param node
	 *            to write
	 */
	public static void write(@NotNull Node node) {
		try (PrintWriter writer = new PrintWriter(
				new BufferedWriter(new FileWriter(getFileName(node.getId()), true)))) {
			@NotNull
			Snapshot snapshot = node.getSnapshot();
			writer.println(snapshot.getId() + SEPARATOR + snapshot.getLocalBalance() + SEPARATOR
					+ snapshot.getMoneyInTransfer());
			LOGGER.debug("Storage wrote a snapshot=" + snapshot);
		} catch (Exception e) {
			LOGGER.error("Failed to write snapshot of node=" + node, e);
		}
	}

	/**
	 * Creates storage folder to keep node's CSV files in
	 */
	public static void init() {
		try {
			Path path = Paths.get(STORAGE_FOLDER);
			if (!Files.exists(path)) {
				Files.createDirectory(path);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to create storage directory", e);
		}
	}

	/**
	 * Removes node's CSV file
	 *
	 * @param nodeId
	 *            of the node
	 */
	public static void removeFile(int nodeId) {
		try {
			Path path = Paths.get(getFileName(nodeId));
			if (Files.exists(path)) {
				Files.delete(path);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to remove file for nodeId=" + nodeId, e);
		}
	}

	@NotNull
	private static String getFileName(int nodeId) {
		return STORAGE_FOLDER + "/Node-" + nodeId + ".csv";
	}
}
