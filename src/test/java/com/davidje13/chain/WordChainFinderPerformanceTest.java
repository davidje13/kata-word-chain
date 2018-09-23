package com.davidje13.chain;

import com.davidje13.testutil.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class WordChainFinderPerformanceTest {
	private final List<String> words = new ArrayList<>();

	@Before
	public void populateWords() {
		int letters = 7;
		int radix = 4;
		int size = (int) Math.pow(radix, letters);
		for (int i = size / radix; i < size; ++ i) {
			words.add(Integer.toString(i, radix));
		}
	}

	@Test
	public void constructingAndTraversal_returnsQuickly() {
		String start = words.get(0);
		String finish = words.get(words.size() - 1);

		double millis = TestUtils.averageTimeTakenMillis(5, () -> {
			WordChainFinder finder = new WordChainFinder();
			words.forEach(finder::registerWord);
			Optional<List<String>> path = finder.traverse(start, finish);
			assertThat(path.isPresent(), equalTo(true));
		});

		assertThat(millis, lessThan(300.0));
	}
}
