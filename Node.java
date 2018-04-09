package com.ashishp.dc.assignment.cl.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.ashishp.dc.assignment.cl.handler.StorageHandler;
import com.ashishp.dc.assignment.cl.main.BankTransfer;
import com.google.common.base.MoreObjects;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Positive integer to determine position in the graph
	 */
	private final int id;

	/**
	 * IP address of the server node
	 */
	@NotNull
	private final String host;

	/**
	 * Current state of the bank
	 */
	@NotNull
	private final Item item;

	/**
	 * Snapshot record of the bank
	 */
	@NotNull
	private final Snapshot snapshot;

	/**
	 * All known nodes in the graph, including itself
	 * <p>
	 * Map<NodeId, Host>
	 */
	@NotNull
	private final Map<Integer, String> nodes = new HashMap<>();

	public Node() {
		this(0, "");
	}

	public Node(int id, @NotNull String host) {
		this.id = id;
		this.host = host;
		item = new Item(BankTransfer.INITIAL_BALANCE);
		snapshot = new Snapshot();
		nodes.put(id, host);
	}

	public int getId() {
		return id;
	}

	@NotNull
	public Item getItem() {
		return item;
	}

	@NotNull
	public Snapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * Starts distributed snapshot by capturing local balance and waiting for marker
	 * from other nodes
	 */
	public void startSnapshotRecording() {
		snapshot.startSnapshotRecording(id, item.getBalance(), nodes);
	}

	public void stopSnapshotRecording() {
		StorageHandler.write(this);
		snapshot.stopSnapshotRecording();
	}

	public void putNodes(@NotNull Map<Integer, String> nodes) {
		this.nodes.putAll(nodes);
	}

	public void putNode(int id, @NotNull String host) {
		nodes.put(id, host);
	}

	@NotNull
	public String getHost() {
		return host;
	}

	public Map<Integer, String> getNodes() {
		return Collections.unmodifiableMap(nodes);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		if (o instanceof Node) {
			Node object = (Node) o;

			return Objects.equals(id, object.id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("host", host).add("item", item)
				.add("snapshot", snapshot).add("nodes", Arrays.toString(nodes.entrySet().toArray())).toString();
	}
}
