//Samuel Gaudet, Homework #13, CS 351
//went to tutoring on 5/2
//went to Boyland office hours on 5/4
//went to tutoring on 5/4
//discussed insertionsort with Janathul on 5/4
//discussed count with Mustafa Khan on 5/4
//went to tutoring on 5/5
//talked with Alex Plyer about partition on 5/5
//talked with Mustafa Khan and Zuhaib Khan about insertionsort on 5/5
//talked with Alex Plyer about quicksort on 5/6
//talked with Lakshmi Surabhi about insertionsort and partition on 5/6
//talked with Lakshmi Surabhi about partition and quicksort on 5/7
//talked with Lakshmi Surabhi about quicksort on 5/8
//talked with Mustafa Khan about quicksort on 5/8

package edu.uwm.cs351;


import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

// This is a Homework Assignment for CS 351 at UWM

/**
 * A cyclic doubly-linked list implementation of the Java Collection interface.
 * The iterators returned are strictly fail fast.
 * @param E element type of the collection
 */
public class LinkedCollection<E> extends AbstractCollection<E> 
{
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}

	private static class Node<T> 
	{
		T data;
		Node<T> next, prev;

		@SuppressWarnings("unchecked")
		public Node() 
		{
			next = prev = this;
			data = (T)this;
		}

		public Node(T data, Node<T> prev, Node<T> next) 
		{
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}

	private Node<E> dummy;
	private int count;
	private int version;

	private LinkedCollection(boolean ignored) {} // DO NOT CHANGE THIS

	private boolean wellFormed() 
	{
		// Invariant:
		// 1. dummy node is not null.  Its data should be itself, cast (unsafely) data = (T)this;.
		// 2. each link must be correctly double linked.
		// 3. size is number of nodes in list, other than the dummy.
		// 4. the list must cycle back to the dummy node.
		// 5. none of the nodes inside the list has a data pointing to itself
		if (dummy == null) return report("dummy node is null");
		if (dummy.data != dummy) return report("dummy's data is wrong");

		int count_elements = 0;
		Node<E> prev = dummy;
		for (Node<E> p = dummy.next; p != dummy; p = p.next) 
		{
			if (p == null) return report("found null in list after " + count_elements + " nodes");
			if (p.data == p) return report("Found dummy node inside of the list");
			if (p.prev != prev) return report("prev link bad after " + count_elements + " nodes");
			++count_elements;
			prev = p;
		}
		if (dummy.prev != prev) return report("dummy's prev link is wrong");
		if (count != count_elements) return report("size is " + count + " when it should be " + count_elements);

		// If no problems found, then return true:
		return true;
	}

	/**
	 * Create an empty linked collection.
	 */
	public LinkedCollection() 
	{
		dummy = new Node<E>();
		count = version = 0;
		assert wellFormed() : "invariant failed in constructor";
	}

	@Override // implementation
	public boolean add(E x) 
	{
		assert wellFormed() : "invariant broken at start of add()";
		Node<E> n = new Node<E>(x,dummy.prev,dummy);
		dummy.prev.next = n;
		dummy.prev = n;
		++count;
		++version;
		assert wellFormed() : "invariant broken at end of add()";
		return true;
	}

	@Override // efficiency (optional!)
	public void clear() 
	{
		assert wellFormed() : "invariant broken at start of clear()";
		if (isEmpty()) return;
		dummy.next = dummy.prev = dummy;
		count = 0;
		++version;
		assert wellFormed(): "invariant broken at end of clear()";
	}

	@Override // required
	public int size() 
	{
		assert wellFormed() : "invariant broken at start of size()";
		return count;
	}
	
	@Override // implementation (optional)
	public String toString() 
	{
		assert wellFormed() : "invariant broken at start of toString";
		return "{Collection of size: " + Integer.toString(size()) + "}";
	}

	@Override // decorate
	public boolean addAll(Collection<? extends E> c) 
	{
		assert wellFormed() : "invariant broken at start of addAll";
		if (c == this && count > 0) 
		{
			Node<E> last = dummy.prev;
			for (Node<E> p = dummy.next; p.prev != last; p = p.next) 
			{
				add(p.data);
			}
			assert wellFormed() : "invariant broken by addAll";
			return true;
		} else return super.addAll(c);
	}

	@Override // required
	public Iterator<E> iterator() 
	{
		assert wellFormed() : "invariant broken at start of iterator()";
		return new MyIterator();
	}

	private class MyIterator implements Iterator<E> 
	{
		int colVersion = version;
		boolean isCurrent;
		Node<E> cursor;

		private boolean wellFormed() 
		{
			// Invariant for recommended fields:

			// 0. The outer invariant holds, and versions match
			// 1. cursor is never null (optional)
			// 2. cursor is in the list (implies 1)
			// 3. if cursor is the dummy, hasCurrent must be false

			// NB: We don't check 1,2,3 unless the version matches.

			// 0.
			if (!LinkedCollection.this.wellFormed()) return false;
			if (colVersion == version) 
			{
				// 1. (optional)
				if (cursor == null) return report("curdor is null");

				// 2.
				Node<E> p;
				for (p = dummy; p != dummy.prev && p != cursor; p = p.next) 
				{
					// nothing
				}
				if (p != cursor) return report("cursor not in list");
				
				//3.
				if (cursor == dummy && isCurrent)
					return report("dummy is current node and hasCurrent true");
			}
			return true;
		}

		MyIterator() 
		{
			cursor = dummy;
			isCurrent = false;
			assert wellFormed() : "invariant fails in iterator constructor";
		}


		private void checkVersion() 
		{
			if (version != colVersion) 
			{
				throw new ConcurrentModificationException("iterator stale");
			}
		}

		@Override // required
		public boolean hasNext() 
		{
			assert wellFormed() : "invariant fails at start of hasNext()";
			checkVersion();
			return cursor.next != dummy;
		}

		@Override // required
		public E next() 
		{
			assert wellFormed() : "invariant fails at start of next()";
			checkVersion();
			if (!hasNext()) 
			{
				throw new NoSuchElementException("no more elements");
			}
			cursor = cursor.next;
			isCurrent = true;
			assert wellFormed() : "invariant fails at end of next()";
			return cursor.data;
		}

		@Override // required or implementation
		public void remove() 
		{
			assert wellFormed() : "invariant fails at start of remove()";
			checkVersion();
			if (!isCurrent) 
			{
				throw new IllegalStateException("cannot remove until next() is called (again)");
			}
			cursor.prev.next = cursor.next;
			cursor.next.prev = cursor.prev;
			cursor = cursor.prev; // back up
			isCurrent = false;
			--count;
			colVersion = ++version;
			assert wellFormed() : "invariant fails at end of remove()";
		}
	}

	/// Sorting
	
	private static int MIN_QUICKSORT_SIZE = 2; // never smaller than 2
	
	public String toDebugString() 
	{
		Map<Object, Integer> m = new IdentityHashMap<>();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Node<E> p = dummy;
		int i = 0;
		for (Node<E> n = p.next; n != dummy; n = n.next) 
		{
			if (n == null) 
			{
				sb.append(" <null>");
				break;
			}
			if (n.prev == p) 
			{
				if (m.size() > 0) sb.append(", ");
			}
			else sb.append("! ");
			Integer j = m.put(n, i);
			if (j != null) 
			{
				sb.append("Node #" + j);
				break;
			}
			sb.append(n.data);
			p = n;
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Set the threshhold below which we use insertion sort.
	 * @param n number to use, must be greater than 1.
	 */
	public static void setMinQuicksortSize(int n) 
	{
		if (n < 2) throw new IllegalArgumentException("too small");
		MIN_QUICKSORT_SIZE = n;
	}
	
	private int count() 
	{
		int c = 0;
		for (Node<E> p = dummy.next; p != dummy; p = p.next) ++c;
		return c;
	}
	
	private void removeNode(Node<E> p) 
	{
		// TODO: Simple: remove this node from the DLL
		// by bypassing it.  No need to change this node's links.
		p.prev.next = p.next;
		p.next.prev = p.prev;
	}
	
	private void addNodeAfter(Node<E> p, Node<E> n) 
	{
		// TODO: Simple: place n in DLL after p.
		// No null pointers.
		Node<E> temp = p.next;
		p.next = n;
		n.prev = p;
		n.next = temp;
		temp.prev = n;
	}
	
	private void insertionsort(Comparator<E> comp) 
	{
		// Do not assert invariant.
		for (Node<E> walkForward = dummy.next.next; walkForward != dummy; walkForward = walkForward.next)
		{
			Node<E> temp = walkForward.prev;
			Node<E> walkBack;
			for (walkBack = walkForward; walkBack.prev != dummy && comp.compare(walkForward.data, walkBack.prev.data) < 0; walkBack = walkBack.prev)
			{}
			if (walkBack != walkForward) 
			{
				removeNode(walkForward);
				addNodeAfter(walkBack.prev, walkForward);
				walkForward = temp;
			}
		}
	}
	
	/**
	 * @param comp
	 * @return lastPivot - the last element that was equal to the pivot
	 */
	private Node<E> partition(Comparator<E> comp) 
	{
		Node<E> pivot = dummy.next;
		Node<E> lastPivot = pivot;
		assert pivot != null;
		assert pivot != dummy;
		assert pivot.next != dummy;
		// Do not assert invariant.
		// TODO partition all the nodes in the null-terminated list starting at first.
		// See homework description
		
		Node<E> n = pivot.next;
		Node<E> lastGreater = null;
		
		while (n != dummy)
		{
			int result = comp.compare(n.data, pivot.data);
			if (result < 0)
			{
				removeNode(n);
				addNodeAfter(pivot.prev, n);
				if (lastGreater == null) n = lastPivot.next;
				else n = lastGreater.next;
			}
			else if (result > 0)
			{
				lastGreater = n;
				n = n.next;
			}
			else
			{
				removeNode(n);
				addNodeAfter(lastPivot, n);
				lastPivot = n;
				n = n.next;
			}
		}
		
		return lastPivot;
	}
	
	private void quicksort(Comparator<E> comp) 
	{
		if (count() < MIN_QUICKSORT_SIZE) 
		{ // the only "if" permitted
			insertionsort(comp);
			return;
		}
		// TODO: Two recursive calls.  No loops.  No conditions at all.
		Node<E> pivot = dummy.next;
		Node<E> lastPivot = partition(comp);
		Node<E> tail = dummy.prev;
		
		dummy.prev = pivot.prev;
		dummy.prev.next = dummy;
		quicksort(comp);
		pivot.prev = dummy.prev;
		dummy.prev.next = pivot;
		dummy.prev = tail;
		Node<E> head = dummy.next;
		
		dummy.next = lastPivot.next;
		dummy.next.prev = dummy;
		quicksort(comp);
		lastPivot.next = dummy.next;
		dummy.next.prev = lastPivot;
		dummy.next = head;
	}
	
	/**
	 * Sort the elements in the collection using the given comparator.
	 * The sorting algorithm is "stable".
	 * @param comp comparator, must not be null
	 */
	public void sort(Comparator<E> comp) 
	{
		assert wellFormed() : "invariant failed in sort";
		if (comp == null) throw new NullPointerException();
		quicksort(comp);
		++version; // always increment
		assert wellFormed() : "invariant broken by sort";
	}
	
	// do not change this class -- it's used for internal testing:
	public static class Spy {
		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}
		
		/**
		 * A debugging node class for use in testing only, not in client code.
		 * @param E data type
		 */
		public static class Node<E> extends LinkedCollection.Node<E> {
			public Node(E d) {
				super();
				this.data = d;
				this.prev = null;
				this.next = null;
			}
			public E getData() {
				return data;
			}
			public Node<E> getPrev() {
				return (Node<E>)prev;
			}
			public Node<E> getNext() {
				return (Node<E>)next;
			}
		}
		
		/**
		 * Make a debugging node with the given data element
		 * @param d data from the node
		 * @return new debugging node
		 */
		public <E> Node<E> makeNode(E d) {
			return new Node<>(d);
		}
		
		/**
		 * Return a debugging node whose data refers to itself
		 * @return new debugging node
		 */
		@SuppressWarnings("unchecked")
		public <E> Node<E> makeSelfRef() {
			Node<E> result = new Node<>(null);
			result.data = (E)result;
			return result;
		}
		
		/**
		 * Change the data field of a debugging node
		 * @param n node to change
		 * @param x new value of the data
		 */
		@SuppressWarnings("unchecked")
		public <E> void assignData(Node<E> n, Object x) {
			n.data = (E)x;
		}
		
		/**
		 * Link the nodes in the forward direction.
		 * @param first node to point forward to the first of the rest
		 * @param rest remaining nodes to be linked
		 */
		public <E> void linkForward(Node<E> first, @SuppressWarnings("unchecked") Node<E>... rest) {
			Node<E> last = first;
			for (Node<E> n : rest) {
				last.next = n;
				last = n;
			}
		}
		
		/**
		 * Link the nodes in the reverse direction.
		 * @param first node to point the first of the rest nodes back to
		 * @param rest remaining node to link
		 */
		public <E> void linkBackward(Node<E> first, @SuppressWarnings("unchecked") Node<E>... rest) {
			Node<E> last = first;
			for (Node<E> n : rest) {
				n.prev = last;
				last = n;
			}
		}
		
		/**
		 * Create an instance of LinkedCollection so that we can test the invariant checker.
		 * @param d dummy node
		 * @param c count
		 * @param v version
		 * @return instance of LinkedCollection that has not been checked.
		 */
		public <E> LinkedCollection<E> createCol(Node<E> d, int c, int v) {
			LinkedCollection<E> result = new LinkedCollection<>(false);
			result.dummy = d;
			result.count = c;
			result.version = v;
			return result;
		}
					
		
		/** Run the private {@link LinkedCollection#count()} method. 
		 * @param lc the linked collection to coutn for, must not be null
		 * @return number of elements (not counting the dummy)
		 */
		public int testCount(LinkedCollection<?> lc) {
			return lc.count();
		}
		
		/**
		 * Run the private {@link LinkedCollection#addNodeAfter(edu.uwm.cs351.LinkedCollection.Node, edu.uwm.cs351.LinkedCollection.Node)} method.
		 * @param <E> element type
		 * @param lc collection to use, must not be null
		 * @param n1 node after which to add, must not be null
		 * @param n2 node to add after n1, must not be null
		 */
		public <E> void testAddNodeAfter(LinkedCollection<E> lc, Node<E> n1, Node<E> n2) {
			lc.addNodeAfter(n1, n2);
		}
		
		/**
		 * @param <E>
		 * @param lc
		 * @param n
		 */
		public <E> void testRemoveNode(LinkedCollection<E> lc, Node<E> n) {
			lc.removeNode(n);
		}
		
		/**
		 * Test the partition method on the current state of a linked collection.
		 * @param E type of elements
		 * @param lc thel inked collection to operate on.  Must be valid.
		 * @param comp comparator to use, must not be null
		 * @return the (zero-based) index of the result of partition in the list.
		 * If the result is -1, a report will have been generated.
		 */
		public <E> int testPartition(LinkedCollection<E> lc, Comparator<E> comp) {
			int n = lc.size(); // and check invariant
			Map<Object,E> nodes = new IdentityHashMap<>();
			for (LinkedCollection.Node<E> p = lc.dummy.next; p != lc.dummy; p = p.next) {
				nodes.put(p, p.data);
			}
			LinkedCollection.Node<E> lp = lc.partition(comp);
			if (lp == lc.dummy) {
				lc.report("partition returned the dummy node");
				return -1;
			}
			LinkedCollection.Node<E> fast = lc.dummy.next;
			if (fast == null) {
				lc.report("partition lost all the nodes");
				return -1;
			}
			fast = fast.next;
			int i = 0;
			int foundlp = -1;
			for (LinkedCollection.Node<E> p = lc.dummy.next; p != lc.dummy; p = p.next) {
				if (p == fast) {
					lc.report("partition created a bad cycle");
					return -1;
				}
				if (fast == null || fast.next == null) {
					lc.report("partition made list null terminated");
					return -1;
				}
				if (!nodes.containsKey(p)) {
					lc.report("partition created new nodes");
					return -1;
				}
				if (!Objects.equals(p.data,  nodes.get(p))) {
					lc.report("partition changed the data in a node");
					return -1;
				}
				if (p == lp) {
					foundlp = i; 
				}
				++i;
				if (fast != lc.dummy) fast = fast.next;
				if (fast != lc.dummy) fast = fast.next;
			}
			if (foundlp == -1) {
				lc.report("partition returns a node not in list (any more?)");
			}
			if (i != n) {
				lc.report("Partition started with " + n + " nodes but afterwards has only " + i);
			}
			return foundlp;
		}
	}
}
