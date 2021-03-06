package com.ashishp.dc.assignment.cl.entity;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 *  @author <a href="http://www.linkedin.com/in/aashishpanchal">Aashish</a>
 *
 */
public final class Item implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Current amount of money at the bank
     */
    private int balance;

    /**
     * Temp variable for holding withdraw money amount with possibility to restore
     * holds under assumption that only one money transfer is done at a time
     */
    private int withdrawAmount;

    public Item(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void incrementBalance(int amount) {
        balance += amount;
    }

    public void restoreBalance() {
        balance += withdrawAmount;
        withdrawAmount = 0;
    }

    /**
     * Checks if current balance is over or equal the amount to be deducted
     * if it is -> deducts the money, if not -> balance stay untouched
     *
     * @param amount to be deducted
     * @return whether operation succeed or not
     */
    public boolean decrementBalance(int amount) {
        if (balance >= amount) {
            balance -= amount;
            withdrawAmount = amount;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (o instanceof Item) {
            Item object = (Item) o;

            return Objects.equals(balance, object.balance);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("balance", balance)
                .toString();
    }
}
