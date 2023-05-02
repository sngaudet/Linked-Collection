import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.uwm.cs.util.PermutationComparator;
import edu.uwm.cs.util.Statistic;
import edu.uwm.cs351.LinkedCollection;

public class CompareSorts {
	private static final int BASE = 1_000_000;
	private static final int REPEATS = 10; // must be at least 2
	
	private Integer[] permutation;
	private LinkedCollection<Integer> coll;
	private Random random;
	
	public CompareSorts(int n) {
		permutation = new Integer[n];
		coll = new LinkedCollection<>();
		for (int i=0; i < n; ++i) {
			permutation[i] = i;
			coll.add(i);
		}
		random = new Random();
	}
	
	public double[] run() {
		int runs = BASE / permutation.length;
		Statistic insertionSort = new Statistic();
		Statistic quickSort = new Statistic();
		@SuppressWarnings("unchecked")
		Comparator<Integer>[] comps = (Comparator<Integer>[]) new Comparator<?>[REPEATS];
		for (int j = 0; j < REPEATS; ++j) {
			Collections.shuffle(Arrays.asList(permutation));
			comps[j] = new PermutationComparator<>(permutation);
		}
		for (int i=0; i < runs; ++i) {
			boolean usingQuicksort;
			if (random.nextBoolean()) {
				LinkedCollection.setMinQuicksortSize(2);
				usingQuicksort = true;
			} else {
				LinkedCollection.setMinQuicksortSize(permutation.length+1);
				usingQuicksort = false;
			}
			// System.out.println("using quicksort = " + usingQuicksort);
			long nanos = System.nanoTime();
			for (int j=0; j < REPEATS; ++j) {
				coll.sort(comps[j]);
			}
			long diff = System.nanoTime() - nanos;
			// System.out.println(diff);
			if (diff/REPEATS > Integer.MAX_VALUE) {
				System.out.println("Overflow!");
				break;
			}
			if (usingQuicksort) {
				quickSort.record((int)(diff/REPEATS));
			} else {
				insertionSort.record((int)(diff/REPEATS));
			}
		}
		return new double[] {
				insertionSort.getAverage()/1000,
				quickSort.getAverage()/1000
		};
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" };
		}
		System.out.println("  n   insertionsort     quicksort");
		System.out.println("---------------------------------");
		for (String arg : args) {
			int n = Integer.parseInt(arg);
			CompareSorts cs = new CompareSorts(n);
			double[] stats = cs.run();
			System.out.format("%5d %12f   %12f\n", n, stats[0], stats[1]);
		}
	}
}
