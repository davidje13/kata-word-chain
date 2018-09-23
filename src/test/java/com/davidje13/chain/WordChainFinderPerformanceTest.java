package com.davidje13.chain;

import com.davidje13.testutil.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

public class WordChainFinderPerformanceTest {
	private final List<String> words = new ArrayList<>();

	@Before
	public void populateWords() {
		int size = 100000;
		for (int i = size / 10; i < size; ++ i) {
			words.add(Integer.toString(i));
		}
	}

	@Test
	public void constructor_returnsQuickly() {
		double millis = TestUtils.averageTimeTakenMillis(2, () ->
				new WordChainFinder(words)
		);

		assertThat(millis, lessThan(1000.0));
	}

	@Test
	public void traverse_routesQuickly() {
		WordChainFinder finder = new WordChainFinder(words);

		String start = words.get(0);
		String finish = words.get(words.size() - 1);
		assertThat(finder.traverse(start, finish), not(equalTo(null)));

		double millis = TestUtils.averageTimeTakenMillis(20, () ->
				finder.traverse(start, finish)
		);

		assertThat(millis, lessThan(50.0));
	}
}
