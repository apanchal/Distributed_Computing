/**
 * 
 */
package com.ashishp.dc.assignment.cl.main;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashishp.dc.assignment.cl.config.Configuration;
import com.ashishp.dc.assignment.cl.entity.Node;
import com.ashishp.dc.assignment.cl.handler.InputHandler;
import com.ashishp.dc.assignment.cl.handler.NetworkHandler;
import com.ashishp.dc.assignment.cl.handler.RemoteHandler;
import com.ashishp.dc.assignment.cl.handler.StorageHandler;
import com.ashishp.dc.assignment.cl.remote.NodeRemote;

/**
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public class Server {

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private Configuration configuration;

	@Nullable
	private static Node node;

	private static NodeState nodeState = NodeState.DISCONNECTED;

	/**
	 * Thread pool scheduler of N threads for money transfer and snapshot taking
	 */
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public Server() {
		super();
		configuration = Configuration.getInstance();
		LOGGER.debug("========System Configuration====\n {}", configuration);
	}

	public static void main(String args[]) {
		LOGGER.debug("Starting Server...");
		new Server();
		if (BankTransfer.MIN_AMOUNT >= BankTransfer.MAX_AMOUNT
				|| BankTransfer.MAX_AMOUNT >= BankTransfer.INITIAL_BALANCE) {
			LOGGER.warn(
					"Bank transfer properties must maintain formula [ MIN_AMOUNT < MAX_AMOUNT < INITIAL_BALANCE ] !");
			return;
		}

		StorageHandler.init();

		try {
			create("localhost", 10);
			join("localhost", 15, "localhost", 10);
			join("localhost", 20, "localhost", 15);
			join("localhost", 25, "localhost", 20);
			join("localhost", 30, "localhost", 25);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NetworkHandler.printMachineIPv4();
		LOGGER.info("In oredr to view Branch details type 'view'");
		LOGGER.info("In order to create snapshot type 'snapshot'");
		LOGGER.info("\n");
		LOGGER.info("Bank is ready for request >");

		InputHandler.readInput(Server.class.getName());

	}

	/**
	 * Signals current node to create the graph
	 *
	 * @param nodeHost
	 *            host for new current node
	 * @param nodeId
	 *            id for new current node
	 */
	public static void create(@NotNull String nodeHost, int nodeId) throws Exception {
		if (nodeState != NodeState.DISCONNECTED) {
			LOGGER.warn("Must be DISCONNECTED to create! Current nodeState=" + nodeState);
			return;
		}
		if (nodeId <= 0) {
			LOGGER.warn("Node id must be positive integer [ nodeID > 0 ] !");
			return;
		}
		startRMIRegistry();
		LOGGER.info("NodeId=" + nodeId + " is the first bank in the graph");
		node = register(nodeId, nodeHost);
		LOGGER.info("NodeId=" + nodeId + " is connected as first node=" + node);
		nodeState = NodeState.CONNECTED;
		startMoneyTransferring();
		LOGGER.debug("Node State after Create: {}", nodeState);
	}

	/**
	 * Signals current node to join the graph: - accumulate the graph structure of
	 * all available banks from the existing node - start randomly sending/accepting
	 * money transfers
	 * <p>
	 * Existing node MUST be operational!
	 *
	 * @param nodeHost
	 *            host for new current node
	 * @param nodeId
	 *            id for new current node
	 * @param existingNodeHost
	 *            of node in the graph to fetch data from
	 * @param existingNodeId
	 *            of node in the graph to fetch data from
	 */
	public static void join(@NotNull String nodeHost, int nodeId, @NotNull String existingNodeHost, int existingNodeId)
			throws Exception {
		LOGGER.debug("Node state at time of joining : {}", nodeState);
		if (nodeState == NodeState.DISCONNECTED) {
			LOGGER.warn("Must be CONNECTED to join! Current nodeState=" + nodeState);
			return;
		}
		if (nodeId <= 0) {
			LOGGER.warn("Node id must be positive integer [ nodeID > 0 ] !");
			return;
		}
		startRMIRegistry();
		LOGGER.info("NodeId=" + nodeId + " connects to existing nodeId=" + existingNodeId);
		Node existingNode = RemoteHandler.getRemoteNode(existingNodeId, existingNodeHost).getNode();
		if (existingNode.getNodes().isEmpty()) {
			LOGGER.warn("Existing node must be operational!");
			return;
		}
		if (existingNode.getNodes().containsKey(nodeId)) {
			LOGGER.warn("Cannot join as nodeId=" + nodeId + " already taken!");
			return;
		}
		node = register(nodeId, nodeHost);
		node.putNodes(existingNode.getNodes());
		announceJoin();
		LOGGER.info("NodeId=" + nodeId + " connected as node=" + node + " from existingNode=" + existingNode);
		nodeState = NodeState.CONNECTED;
		startMoneyTransferring();
	}

	/**
	 * View the graph topology aka all the banks in connected component
	 */
	public static void view() throws RemoteException {
		if (nodeState != NodeState.CONNECTED) {
			LOGGER.warn("Must be CONNECTED to view topology! Current nodeState=" + nodeState);
			return;
		}
		LOGGER.info("Viewing topology from node=" + node);
		node.getNodes().entrySet().forEach(n -> {
			try {
				RemoteHandler.getRemoteNode(n.getKey(), n.getValue()).getNode();
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * Initiate distributed snapshot to all known nodes (all nodes are
	 * interconnected as a digraph)
	 */
	public static void snapshot() throws RemoteException {
		if (nodeState != NodeState.CONNECTED) {
			LOGGER.warn("Must be CONNECTED to initiate the distributed snapshot! Current nodeState=" + nodeState);
			return;
		}
		LOGGER.info("Starting distributed snapshot from node=" + node);
		RemoteHandler.getRemoteNode(node).receiveMarker(node.getId());
	}

	/**
	 * Registers RMI for new node, initializes node object
	 *
	 * @param id
	 *            of the new node
	 * @param host
	 *            of the new node
	 */
	@NotNull
	private static Node register(int id, @NotNull String host) throws Exception {
		System.setProperty("java.rmi.server.hostname", host);
		Node node = new Node(id, host);
		Naming.bind("rmi://" + node.getHost() + "/NodeRemote" + node.getId(), new NodeRemote(node));
		String bindedNodes[] = Naming.list("rmi://" + node.getHost());
		for (String n : bindedNodes) {
			LOGGER.info("Node {} bounded.", n);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.info("Auto-leaving process initiated...");
				try {
					if (nodeState == NodeState.CONNECTED) {
						leave();
					}
				} catch (Exception e) {
					//LOGGER.error("Failed to leave node", e);
				}
			}
		});
		return node;
	}

	/**
	 * Signals current node to leave the graph
	 */
	private static void leave() throws Exception {
		LOGGER.info("NodeId=" + node.getId() + " is disconnecting from the graph...");
		Naming.unbind("rmi://" + node.getHost() + "/NodeRemote" + node.getId());
		StorageHandler.removeFile(node.getId());
		LOGGER.info("NodeId=" + node.getId() + " disconnected");
		node = null;
		nodeState = NodeState.DISCONNECTED;
	}

	/**
	 * Announce JOIN operation to the nodes in the graph
	 */
	private static void announceJoin() throws RemoteException {
		LOGGER.debug("Announcing join to nodes=" + Arrays.toString(node.getNodes().entrySet().toArray()));
		node.getNodes().entrySet().parallelStream().filter(n -> n.getKey() != node.getId()).forEach(n -> {
			try {
				RemoteHandler.getRemoteNode(n.getKey(), n.getValue()).addNode(node.getId(), node.getHost());
				LOGGER.trace("Announced join to nodeId=" + n.getKey());
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static void startMoneyTransferring() {
		// LOGGER.debug("--------- Starting money transferring ------------");
		// LOGGER.debug("\n");
		executor.scheduleAtFixedRate((Runnable) () -> {
			try {
				if (node != null && node.getNodes().size() > 1) {
					Node randomNode = getRandomNode();
					int randomAmount = new Random().nextInt(BankTransfer.MAX_AMOUNT + 1) + BankTransfer.MIN_AMOUNT;
					// LOGGER.debug("Trasnferring {} funds to node {} ", randomAmount,
					// randomNode.getId());
					RemoteHandler.getRemoteNode(node).transferMoney(randomNode.getId(), randomAmount);
				}
			} catch (RemoteException e) {
				LOGGER.error("Failed to transfer to random node!", e);
			}
		}, 0, BankTransfer.TIMEOUT_FREQUENCY, TimeUnit.valueOf(BankTransfer.TIMEOUT_UNIT));
		LOGGER.debug("\n");
	}

	/**
	 * Gets node given nodeId
	 *
	 * @return currentNode if nodeId is the same, remote node otherwise
	 */
	private static Node getRandomNode() {
		int index = new Random().nextInt(node.getNodes().size() - 1);
		int nodeId = node.getNodes().keySet().parallelStream().filter(n -> n != node.getId())
				.collect(Collectors.toList()).get(index);
		return new Node(nodeId, node.getNodes().get(nodeId));
	}

	/**
	 * Starts RMI registry on default port if not started already
	 */
	private static void startRMIRegistry() {
		try {
			LocateRegistry.createRegistry(Configuration.getInstance().getRmiPort());
		} catch (RemoteException e) {
			// already started
		}
	}
}
