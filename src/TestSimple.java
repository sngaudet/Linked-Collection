import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Supplier;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.LinkedCollection;

public class TestSimple extends LockedTestCase {

	// This class has some simple tests of partioning and sorting
	
	LinkedCollection.Spy spy = new LinkedCollection.Spy(); 
	LinkedCollection<String> self;
	Iterator<String> it;
	
	protected String string(Supplier<?> supp) {
		try {
			Object obj = supp.get();
			if (obj == null) return "null";
			if (obj instanceof LinkedCollection<?>) {
				return new ArrayList<>((LinkedCollection<?>)obj).toString();
			}
			return obj.toString();
		} catch(RuntimeException ex) {
			return ex.getClass().getSimpleName();
		}
	}
	
	@Override // implementation
	protected void setUp() {
		self = new LinkedCollection<>();
	}
	
	
	/// Locked tests
	
	public void test() {
		testSort(false);
		testPartition(false);
	}
	
	private void testSort(boolean ignored) {
		LinkedCollection<Integer> lc = new LinkedCollection<>();
		lc.addAll(Arrays.asList(7,3,0,9,2));
		// how does the comparator sort?  How can you figure out?
		lc.sort((i1,i2) -> i2 - i1);
		// ArrayList prints as [v1, v2, v3] where there's a single space after each comma
		assertEquals(Ts(2034851131), new ArrayList<>(lc).toString());
	}
	
	private void testPartition(boolean ignored) {
		LinkedCollection<Integer> lc = new LinkedCollection<>();
		lc.addAll(Arrays.asList(7,3,0,9,2,7,8));
		// how does the comparator sort?  How can you figure out?
		spy.testPartition(lc, (i1,i2) -> i1 - i2);
		// NB: The collection has been partitioned, not sorted!
		// ArrayList prints as [v1, v2, v3] where there's a single space after each comma
		assertEquals(Ts(1180539688), new ArrayList<>(lc).toString());
	}
	
	
	protected final Comparator<String> normal = (s1,s2) -> s1.compareTo(s2);
	protected final Comparator<String> reverse = (s1,s2) -> s2.compareTo(s1);
	protected final Comparator<String> nondiscrimination = (s1,s2) -> 0;
	
	
	public void test0() {
		self.sort(String.CASE_INSENSITIVE_ORDER);
		assertTrue(self.isEmpty());
	}
	
	public void test1() {
		self.add("banana");
		self.sort(reverse);
		assertEquals(1, self.size());
		assertEquals("banana", self.iterator().next());
	}
	
	public void test2() {
		LinkedCollection.setMinQuicksortSize(10);
		self.add("hello");
		self.add("world");
		self.sort(reverse);
		it = self.iterator();
		assertEquals("world", it.next());
		assertEquals("hello", it.next());
		assertFalse(it.hasNext());
	}
	
	public void test3() {
		LinkedCollection.setMinQuicksortSize(100);
		self.add("Four");
		self.add("score");
		self.add("and");
		self.add("seven");
		self.add("years");
		self.add("ago");
		self.add("our");
		self.add("fathers");
		self.add("brought");
		self.add("forth");
		self.add("upon");
		self.add("this");
		self.add("continent");
		self.add("a");
		self.add("new");
		self.add("nation");
		self.add("conceived");
		self.add("in");
		self.add("liberty");
		self.add("and");
		self.add("dedicated");
		self.add("to");
		self.add("the");
		self.add("proposition");
		self.add("that");
		self.add("all");
		self.add("men");
		self.add("are");
		self.add("created");
		self.add("equal");
		self.sort(String.CASE_INSENSITIVE_ORDER);
		it = self.iterator();
		assertEquals("a",it.next());
		assertEquals("ago",it.next());
		assertEquals("all",it.next());
		assertEquals("and",it.next());
		assertEquals("and",it.next());
		assertEquals("are",it.next());
		assertEquals("brought",it.next());
		assertEquals("conceived",it.next());
		assertEquals("continent",it.next());
		assertEquals("created",it.next());
		assertEquals("dedicated",it.next());
		assertEquals("equal",it.next());
		assertEquals("fathers",it.next());
		assertEquals("forth",it.next());
		assertEquals("Four",it.next());
		assertEquals("in",it.next());
		assertEquals("liberty",it.next());
		assertEquals("men",it.next());
		assertEquals("nation",it.next());
		assertEquals("new",it.next());
		assertEquals("our",it.next());
		assertEquals("proposition",it.next());
		assertEquals("score",it.next());
		assertEquals("seven",it.next());
		assertEquals("that",it.next());
		assertEquals("the",it.next());
		assertEquals("this",it.next());
		assertEquals("to",it.next());
		assertEquals("upon",it.next());
		assertEquals("years",it.next());
		assertFalse(it.hasNext());
	}
	
	public void test4() {
		self.add("Hello");
		self.add("hello");
		spy.testPartition(self, reverse);
		it = self.iterator();
		assertEquals("hello", it.next());
		assertEquals("Hello", it.next());
		assertFalse(it.hasNext());
	}
	
	public void test5() {
		self.add("A");
		self.add("rose");
		self.add("is");
		self.add("a");
		self.add("Rose");
		self.add("IS");
		self.add("A");
		self.add("ROSE");
		spy.testPartition(self, String.CASE_INSENSITIVE_ORDER);
		it = self.iterator();
		assertEquals("A", it.next());
		assertEquals("a", it.next());
		assertEquals("A", it.next());
		assertEquals("rose", it.next());
		assertEquals("is", it.next());
		assertEquals("Rose", it.next());
		assertEquals("IS", it.next());
		assertEquals("ROSE", it.next());
		assertFalse(it.hasNext());
	}
	
	public void test6() {
		self.add("Four");
		self.add("score");
		self.add("and");
		self.add("seven");
		self.add("years");
		self.add("ago");
		self.add("our");
		self.add("fathers");
		self.add("brought");
		self.add("forth");
		self.add("upon");
		self.add("this");
		self.add("continent");
		self.add("a");
		self.add("new");
		self.add("nation");
		self.add("conceived");
		self.add("in");
		self.add("liberty");
		self.add("and");
		self.add("dedicated");
		self.add("to");
		self.add("the");
		self.add("proposition");
		self.add("that");
		self.add("all");
		self.add("men");
		self.add("are");
		self.add("created");
		self.add("equal");
		spy.testPartition(self, String.CASE_INSENSITIVE_ORDER);
		it = self.iterator();
		assertEquals("and", it.next());
		assertEquals("ago", it.next());
		assertEquals("fathers", it.next());
		assertEquals("brought", it.next());
		assertEquals("forth", it.next());
		assertEquals("continent", it.next());
		assertEquals("a", it.next());
		assertEquals("conceived", it.next());
		assertEquals("and", it.next());
		assertEquals("dedicated", it.next());
		assertEquals("all", it.next());
		assertEquals("are", it.next());
		assertEquals("created", it.next());
		assertEquals("equal", it.next());
		assertEquals("Four", it.next());
		assertEquals("score", it.next());
		assertEquals("seven", it.next());
		assertEquals("years", it.next());
		assertEquals("our", it.next());
		assertEquals("upon", it.next());
		assertEquals("this", it.next());
		assertEquals("new", it.next());
		assertEquals("nation", it.next());
		assertEquals("in", it.next());
		assertEquals("liberty", it.next());
		assertEquals("to", it.next());
		assertEquals("the", it.next());
		assertEquals("proposition", it.next());
		assertEquals("that", it.next());
		assertEquals("men", it.next());
		assertFalse(it.hasNext());
	}
	
	public void test7() {
		LinkedCollection.setMinQuicksortSize(2);
		self.add("hello");
		self.add("WORLD");
		self.sort(normal);
		it = self.iterator();
		assertEquals("WORLD",it.next());
		assertEquals("hello",it.next());
		assertFalse(it.hasNext());
	}
	
	public void test8() {
		LinkedCollection.setMinQuicksortSize(2);
		self.add("A");
		self.add("rose");
		self.add("is");
		self.add("a");
		self.add("Rose");
		self.add("IS");
		self.add("A");
		self.add("ROSE");
		self.sort(normal);
		it = self.iterator();
		assertEquals("A", it.next());
		assertEquals("A", it.next());
		assertEquals("IS", it.next());
		assertEquals("ROSE", it.next());
		assertEquals("Rose", it.next());
		assertEquals("a", it.next());
		assertEquals("is", it.next());
		assertEquals("rose", it.next());
		assertFalse(it.hasNext());
	}
	
	public void test9() {
		LinkedCollection.setMinQuicksortSize(2);
		self.add("Four");
		self.add("score");
		self.add("and");
		self.add("seven");
		self.add("years");
		self.add("ago");
		self.add("our");
		self.add("fathers");
		self.add("brought");
		self.add("forth");
		self.add("upon");
		self.add("this");
		self.add("continent");
		self.add("a");
		self.add("new");
		self.add("nation");
		self.add("conceived");
		self.add("in");
		self.add("liberty");
		self.add("and");
		self.add("dedicated");
		self.add("to");
		self.add("the");
		self.add("proposition");
		self.add("that");
		self.add("all");
		self.add("men");
		self.add("are");
		self.add("created");
		self.add("equal");
		self.sort(String.CASE_INSENSITIVE_ORDER);
		it = self.iterator();
		assertEquals("a",it.next());
		assertEquals("ago",it.next());
		assertEquals("all",it.next());
		assertEquals("and",it.next());
		assertEquals("and",it.next());
		assertEquals("are",it.next());
		assertEquals("brought",it.next());
		assertEquals("conceived",it.next());
		assertEquals("continent",it.next());
		assertEquals("created",it.next());
		assertEquals("dedicated",it.next());
		assertEquals("equal",it.next());
		assertEquals("fathers",it.next());
		assertEquals("forth",it.next());
		assertEquals("Four",it.next());
		assertEquals("in",it.next());
		assertEquals("liberty",it.next());
		assertEquals("men",it.next());
		assertEquals("nation",it.next());
		assertEquals("new",it.next());
		assertEquals("our",it.next());
		assertEquals("proposition",it.next());
		assertEquals("score",it.next());
		assertEquals("seven",it.next());
		assertEquals("that",it.next());
		assertEquals("the",it.next());
		assertEquals("this",it.next());
		assertEquals("to",it.next());
		assertEquals("upon",it.next());
		assertEquals("years",it.next());
		assertFalse(it.hasNext());
	}
}
