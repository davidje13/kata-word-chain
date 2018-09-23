package com.davidje13.chain;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class WordChainFinderTest {
	private final List<String> words = asList(
			"aaa",
			"aab",
			"abb",
			"bbb",
			"bbd",
			"bad",
			"ccc",

			"aaaa",
			"aaac",
			"aabc",
			"aabb",
			"aaba"
	);

	private final WordChainFinder finder = new WordChainFinder(words);

	@Test
	public void traverse_canStepOneLetterAtATime() {
		List<String> path = finder.traverse("aaa", "aab").get();
		assertThat(path, contains("aaa", "aab"));
	}

	@Test
	public void traverse_returnsStepByStepTransformations() {
		List<String> path = finder.traverse("aaa", "bbb").get();
		assertThat(path, contains("aaa", "aab", "abb", "bbb"));
	}

	@Test
	public void traverse_findsLongChains() {
		List<String> path = finder.traverse("aaa", "bad").get();
		assertThat(path, contains("aaa", "aab", "abb", "bbb", "bbd", "bad"));
	}

	@Test
	public void traverse_returnsTheShortestRoute() {
		List<String> path = finder.traverse("aaaa", "aabb").get();
		assertThat(path, contains("aaaa", "aaba", "aabb"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void traverse_rejectsUnknownStartWords() {
		finder.traverse("nope", "aaaa");
	}

	@Test(expected = IllegalArgumentException.class)
	public void traverse_rejectsUnknownFinishWords() {
		finder.traverse("aaaa", "nope");
	}

	@Test
	public void traverse_returnsNothingIfNoPathIsFound() {
		Optional<List<String>> optionalPath = finder.traverse("aaa", "ccc");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}

	@Test
	public void traverse_cannotAddOrRemoveLetters() {
		Optional<List<String>> optionalPath = finder.traverse("aaa", "aaaa");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}
}
