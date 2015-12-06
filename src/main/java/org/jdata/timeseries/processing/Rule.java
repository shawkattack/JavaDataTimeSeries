/**
 * 
 */
package org.jdata.timeseries.processing;

/**
 * @author Hunter
 *
 */
public class Rule {

	int id;
	int refCount;
	Symbol head;

	public Rule(int aId) {

		this.id = aId;
		refCount = 0;
		head = new Symbol(0);
		head.setNext(head);
		head.setPrev(head);

	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public int getId() {
		return id;
	}

}
