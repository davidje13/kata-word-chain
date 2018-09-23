package com.davidje13.testutil;

public class TestUtils {
	public static double averageTimeTakenMillis(
			int repetitions,
			Runnable runnable
	) {
		long begin = System.currentTimeMillis();
		for (int rep = 0; rep < repetitions; ++ rep) {
			runnable.run();
		}
		long end = System.currentTimeMillis();

		return (end - begin) / (double) repetitions;
	}
}
