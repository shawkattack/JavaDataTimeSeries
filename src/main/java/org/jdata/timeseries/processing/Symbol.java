/**
 * 
 */
package org.jdata.timeseries.processing;

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

}
