package com.ashishp.dc.assignment.cl.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashishp.dc.assignment.cl.entity.Node;
import com.ashishp.dc.assignment.cl.entity.Snapshot;
import com.ashishp.dc.assignment.cl.handler.RemoteHandler;
import com.ashishp.dc.assignment.cl.remote.api.NodeServer;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class NodeRemote extends UnicastRemoteObject implements NodeServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeRemote.class);

	/**
	 * Locks operations over the nodes
	 */
	private static final ReadWriteLock nodesLock = new ReentrantReadWriteLock();

	/**
	 * Locks operations over the item
	 */
	private static final ReadWriteLock itemTransferLock = new ReentrantReadWriteLock();

	/**
	 * Locks operations over the item
	 */
	private static final ReadWriteLock itemAcceptLock = new ReentrantReadWriteLock();

	/**
	 * Locks operations over the marker
	 */
	private static final ReadWriteLock markerLock = new ReentrantReadWriteLock();

	@NotNull
	private final Node node;

	public NodeRemote(@NotNull Node node) throws RemoteException {
		this.node = node;
	}

	@NotNull
	@Override
	public Node getNode() throws RemoteException {
		LOGGER.debug("Get node=" + node);
		return node;
	}

	@Override
	public void addNode(int id, @NotNull String host) throws RemoteException {
		nodesLock.writeLock().lock();
		try {
			LOGGER.debug("Add id=" + id + ", host=" + host);
			node.putNode(id, host);
			LOGGER.debug("Current nodes=" + Arrays.toString(node.getNodes().entrySet().toArray()));
		} finally {
			nodesLock.writeLock().unlock();
		}
	}

	@Override
	public void transferMoney(int recipientNodeId, int amount) throws RemoteException {
		itemTransferLock.writeLock().lock();
		try {
			boolean isWithdraw = node.getItem().decrementBalance(amount);
			if (isWithdraw) {
				LOGGER.trace("Transferring amount=" + amount + " to recipientNodeId=" + recipientNodeId);
				boolean isAccepted = RemoteHandler.getRemoteNode(recipientNodeId, node.getNodes().get(recipientNodeId))
						.acceptMoney(node.getId(), amount);
				if (isAccepted) {
					LOGGER.trace("Transferred amount=" + amount + " to recipientNodeId=" + recipientNodeId);
				} else {
					node.getItem().restoreBalance();
					LOGGER.trace("NOT Transferred amount=" + amount + " to recipientNodeId=" + recipientNodeId);
				}
			} else {
				LOGGER.trace("NOT Withdraw money amount=" + amount);
			}
		} finally {
			itemTransferLock.writeLock().unlock();
		}
	}

	@Override
	public boolean acceptMoney(int senderNodeId, int amount) throws RemoteException {
		itemAcceptLock.writeLock().lock();
		try {
			LOGGER.trace("Accepting money amount=" + amount + " from senderNodeId=" + senderNodeId);
			node.getSnapshot().incrementMoneyInTransfer(senderNodeId, amount);
			node.getItem().incrementBalance(amount);
			LOGGER.trace("Accepted, new balance=" + node.getItem().getBalance());
			return true;
		} finally {
			itemAcceptLock.writeLock().unlock();
		}
	}

	@Override
	public void receiveMarker(int nodeId) throws RemoteException {
		markerLock.writeLock().lock();
		itemAcceptLock.writeLock().lock();
		itemTransferLock.writeLock().lock();
		try {
			LOGGER.debug("Received marker from nodeId=" + nodeId);
			@NotNull
			Snapshot snapshot = node.getSnapshot();
			if (!snapshot.isRecording()) {
				node.startSnapshotRecording();
				LOGGER.debug("Broadcasting marker to neighbours");
				ExecutorService executorService = Executors.newFixedThreadPool(node.getNodes().size() - 1);
				node.getNodes().entrySet().parallelStream().filter(n -> n.getKey() != node.getId()).forEach(entry -> {
					executorService.execute(() -> {
						try {
							RemoteHandler.getRemoteNode(entry.getKey(), entry.getValue()).receiveMarker(node.getId());
							LOGGER.debug("Marker sent to nodeId=" + entry.getKey());
						} catch (RemoteException e) {
							LOGGER.error("Failed to sent marker to nodeId=" + entry.getKey(), e);
						}
					});
				});
			}
			LOGGER.debug("stopping snapshot recording.");
			snapshot.stopRecording(nodeId);
			if (!snapshot.isRecording()) {
				LOGGER.debug("Received all markers for snapshot on nodeId=" + nodeId);
				node.stopSnapshotRecording();
			}
		} finally {
			markerLock.writeLock().unlock();
			itemAcceptLock.writeLock().unlock();
			itemTransferLock.writeLock().unlock();
		}
	}
}
