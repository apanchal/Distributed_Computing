package com.ashishp.dc.assignment.cl.main;

import com.ashishp.dc.assignment.cl.config.Configuration;

/**
 * 
 * @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public interface BankTransfer {

	/**
	 * Initial amount at bank account
	 */
	int INITIAL_BALANCE = Configuration.getInstance().getInitialAmount();

	/**
	 * Minimal amount to transfer
	 */
	int MIN_AMOUNT = Configuration.getInstance().getMinAmount();

	/**
	 * Maximal amount to transfer
	 */
	int MAX_AMOUNT = Configuration.getInstance().getMaxAmount();

	/**
	 * Frequency of money transfers
	 */
	int TIMEOUT_FREQUENCY = Configuration.getInstance().getTimeoutFrequency();

	/**
	 * Unit of money transfers
	 */
	String TIMEOUT_UNIT = Configuration.getInstance().getTimeoutUnit();
}
