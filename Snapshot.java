package com.ashishp.dc.assignment.cl.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.MoreObjects;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class Snapshot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Sequential number, current snapshot ID to be taken
	 */
	private int id;

	/**
	 * Current amount of money at the bank
	 */
	private int localBalance;

	/**
	 * All money transfers from incoming channels upon receiving the marker
	 */
	private int moneyInTransfer;

	/**
	 * Incoming nodes to be recorded for distributed snapshot holds only node ids
	 * from where the marker has not arrived yet if collection is empty -> all
	 * markers are received
	 * <p>
	 * Map<NodeId>
	 */
	private final @NotNull Set<Integer> unrecordedChannels = new HashSet<Integer>();

	public void startSnapshotRecording(int nodeId, int balance, Map<Integer, String> nodes) {
		id++;
		localBalance = balance;
		moneyInTransfer = 0;
		unrecordedChannels.addAll(nodes.entrySet().parallelStream().filter(n -> n.getKey() != nodeId)
				.map(Map.Entry::getKey).collect(Collectors.toSet()));
	}

	public void stopSnapshotRecording() {
		localBalance = 0;
		moneyInTransfer = 0;
		unrecordedChannels.clear();
	}

	public int getId() {
		return id;
	}

	public int getLocalBalance() {
		return localBalance;
	}

	public int getMoneyInTransfer() {
		return moneyInTransfer;
	}

	/**
	 * Increments the money-in-transfer upon receiving the marker from that node
	 *
	 * @param recipientNodeId
	 *            recipient of the money transfer
	 * @param amount
	 *            of the money transfer
	 */
	public void incrementMoneyInTransfer(int recipientNodeId, int amount) {
		if (unrecordedChannels.contains(recipientNodeId)) {
			moneyInTransfer += amount;
		}
	}

	public void stopRecording(int nodeId) {
		unrecordedChannels.remove(nodeId);
	}

	public boolean isRecording() {
		return unrecordedChannels.size() != 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		if (o instanceof Snapshot) {
			Snapshot object = (Snapshot) o;

			return Objects.equals(id, object.id) && Objects.equals(localBalance, object.localBalance)
					&& Objects.equals(moneyInTransfer, object.moneyInTransfer);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, localBalance, moneyInTransfer);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("localBalance", localBalance)
				.add("moneyInTransfer", moneyInTransfer)
				.add("unrecordedChannels", Arrays.toString(unrecordedChannels.toArray())).toString();
	}
}
