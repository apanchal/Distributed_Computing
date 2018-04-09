package com.ashishp.dc.assignment.cl.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jetbrains.annotations.NotNull;

import com.ashishp.dc.assignment.cl.entity.Node;
import com.ashishp.dc.assignment.cl.remote.api.NodeServer;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class NullNodeRemote extends UnicastRemoteObject implements NodeServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotNull
	private final Node node;

	public NullNodeRemote(@NotNull Node node) throws RemoteException {
		this.node = node;
	}

	@NotNull
	@Override
	public Node getNode() throws RemoteException {
		return node;
	}

	@Override
	public void addNode(int id, @NotNull String host) throws RemoteException {
	}

	@Override
	public void transferMoney(int recipientNodeId, int amount) throws RemoteException {
	}

	@Override
	public boolean acceptMoney(int senderNodeId, int amount) throws RemoteException {
		return false;
	}

	@Override
	public void receiveMarker(int nodeId) throws RemoteException {
	}
}
