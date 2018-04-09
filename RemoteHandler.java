package com.ashishp.dc.assignment.cl.handler;

import java.rmi.Naming;
import java.rmi.RemoteException;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashishp.dc.assignment.cl.entity.Node;
import com.ashishp.dc.assignment.cl.remote.NullNodeRemote;
import com.ashishp.dc.assignment.cl.remote.api.NodeServer;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public abstract class RemoteHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHandler.class);

	/**
	 * Get reference to remote node
	 *
	 * @param node
	 *            remote node
	 * @return reference to remote object
	 */
	@NotNull
	public static NodeServer getRemoteNode(@NotNull Node node) {
		return getRemoteNode(node.getId(), node.getHost());
	}

	/**
	 * Get reference to remote node
	 *
	 * @param id
	 *            of the node
	 * @param host
	 *            of the node
	 * @return reference to remote object
	 */
	@NotNull
	public static NodeServer getRemoteNode(int id, @NotNull String host) {
		try {
			return (NodeServer) Naming.lookup("rmi://" + host + "/NodeRemote" + id);
		} catch (Exception e) {
			LOGGER.error("Failed to get remote interface for id=" + id, e);
			try {
				return new NullNodeRemote(new Node());
			} catch (RemoteException re) {
				LOGGER.error("Failed to get Null Node Pattern", re);
				throw new RuntimeException("RMI failed miserably", re);
			}
		}
	}
}
