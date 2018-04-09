package com.ashishp.dc.assignment.cl.remote.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jetbrains.annotations.NotNull;

import com.ashishp.dc.assignment.cl.entity.Node;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public interface NodeServer extends Remote {

	@NotNull
	Node getNode() throws RemoteException;

	void addNode(int id, @NotNull String host) throws RemoteException;

	void transferMoney(int recipientNodeId, int amount) throws RemoteException;

	boolean acceptMoney(int senderNodeId, int amount) throws RemoteException;

	void receiveMarker(int nodeId) throws RemoteException;
}
