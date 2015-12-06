/**
 * 
 */
package org.jdata.timeseries.processing;

import java.util.Objects;

/**
 * @author Hunter
 *
 */
public class Symbol {

	private int value;

	private Symbol prev;
	private Symbol next;

	public Symbol(int aValue) {

		this.value = aValue;
		
		this.prev = null;
		this.next = null;
	}

	public int getValue() {
        return value;
    }

	public void setPrev(Symbol prev) {
		this.prev = prev;
	}

	public void setNext(Symbol next) {
		this.next = next;
	}

	public Symbol getPrev() {
		return prev;
	}

	public Symbol getNext() {
		return next;
	}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Symbol)) {
            return false;
        }
        Symbol that = (Symbol) other;

        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

}
