
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.uwm.cs351.LinkedCollection;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {
	
	private static final int POWER = 20;
	private static final int BIG = (1<<POWER);
	private static final int RANDOM_MAX = 1<<(POWER-2); // will have duplicates!

	private List<Integer> unsorted;
	private LinkedCollection<Integer> big;
	private LinkedCollection.Spy spy = new LinkedCollection.Spy();
	private Random r = new Random();
	

	public void setUp() {
		try {
			assert false;
			assertTrue(true);
		} catch (AssertionError ex) {
			System.out.println("Assertions enabled. Turn them off for efficiency tests efficiency tests only.");
			System.out.println("  (To run other tests, add -ea to VM Args Pane in Arguments tab of Run Configuration)");
			assertTrue(false);
		}
		big = new LinkedCollection<Integer>();
		unsorted = new ArrayList<>();
		for(int j = 0; j < BIG; ++j){
			unsorted.add(r.nextInt(RANDOM_MAX));
		}				
	}

	static Comparator<Integer> intComparator = (i1,i2) -> i1 - i2;
	static Comparator<Integer> reverseComparator = (i1,i2) -> i2 - i1;
	static Comparator<Integer> ignoreOneComparator = (i1,i2) -> i1/10 - i2/10;
	
	private <T> void assertIsSorted(Iterable<T> itb, Comparator<T> comp){
		boolean first = true;
		T prev, curr = null;
		Iterator<T> it = itb.iterator();
		while(it.hasNext()){
			prev = curr;
			curr = it.next();
			if(!first) {
				boolean condition = comp.compare(prev, curr) <= 0;
				if (!condition) {
					assertTrue(prev + " should precede " + curr, condition);
				}
			} else first = false;
		}
	}
	
	private <T> void assertIsPartitioned(Iterable<T> col, T pivot, int lpIndex, Comparator<T> comp) {
		int i = 0;
		boolean foundPivot = false;
		Iterator<T> it = col.iterator();
		while(it.hasNext()){
			T elem = it.next();
			int c = comp.compare(elem, pivot);
			if (c == 0) {
				if (!foundPivot) foundPivot = true;
				assertTrue ("Found pivot equality after last pivot: " + i + " > " + lpIndex, i <= lpIndex);
			} else if (c < 0) {
				assertTrue("Found lesser element after pivot: " + elem + " < " + pivot, !foundPivot);
				assertTrue("Found lesser element after last pivot: " + elem + " < " + pivot + ", lpIndex = " + lpIndex, i < lpIndex);
			} else {
				assertTrue("Found greater element before pivot: " + elem + " > " + pivot, foundPivot);
				assertTrue("Found greater element before last pivot: " + elem + " > " + pivot + ", lpIndex = " + lpIndex, i > lpIndex);
			}
			++i;
		}
	}
	
	public void testI(){
		LinkedCollection.setMinQuicksortSize(BIG+1);
		for (int p=10; p <= POWER; ++p) {
			int size = 1 << p;
			big.clear();
			for (int i=0; i < size; ++i) {
				big.add(i);
			}
			long startTime = System.currentTimeMillis();
			big.sort(ignoreOneComparator);
			long diff = System.currentTimeMillis()-startTime;
			assertIsSorted(big, intComparator);
			System.out.println("Time to insertion sort 2^" + p + " elements that are already sorted = " + diff);
		}
	}
	
	public void testP() {
		for (int p=10; p <= POWER; ++p) {
			int size = 1 << p;
			big.clear();
			Integer pivot = unsorted.get(0);
			big.addAll(unsorted.subList(0, size));
			long startTime = System.currentTimeMillis();
			int index = spy.testPartition(big, reverseComparator);
			long diff = System.currentTimeMillis()-startTime;
			assertIsPartitioned(big, pivot, index, reverseComparator);
			System.out.println("Time to partition 2^" + p + " random elements = " + diff);
		}
		
	}
	public void testQ(){
		LinkedCollection.setMinQuicksortSize(2);
		for (int p=10; p <= POWER; ++p) {
			int size = 1 << p;
			big.clear();
			big.addAll(unsorted.subList(0, size));
			long startTime = System.currentTimeMillis();
			big.sort(reverseComparator);
			long diff = System.currentTimeMillis()-startTime;
			assertIsSorted(big, reverseComparator);
			System.out.println("Time to quicksort 2^" + p + " random elements = " + diff);
		}
	}
	
}
