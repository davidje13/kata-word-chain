package com.davidje13.chain;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class WordChainFinderTest {
	private final WordChainFinder finder = new WordChainFinder();

	@Before
	public void registerWords() {
		Stream.of(
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
		).forEach(finder::registerWord);
	}

	@Test
	public void traverse_canStepOneLetterAtATime() {
		List<String> path = finder.traverse("aaa", "aab").get();
		assertThat(path, contains("aaa", "aab"));
	}

	@Test
	public void traverse_canRouteFromWordToItself() {
		List<String> path = finder.traverse("aaa", "aaa").get();
		assertThat(path, contains("aaa"));
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

	@Test
	public void traverse_returnsNothing_ifStartWordIsUnknown() {
		Optional<List<String>> optionalPath = finder.traverse("nope", "aaaa");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}

	@Test
	public void traverse_returnsNothing_ifEndWordIsUnknown() {
		Optional<List<String>> optionalPath = finder.traverse("aaaa", "nope");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}

	@Test
	public void traverse_returnsNothing_ifNoPathIsFound() {
		Optional<List<String>> optionalPath = finder.traverse("aaa", "ccc");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}

	@Test
	public void traverse_returnsNothing_ifWordsAreEmpty() {
		Optional<List<String>> optionalPath = finder.traverse("", "");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}

	@Test
	public void traverse_cannotAddOrRemoveLetters() {
		Optional<List<String>> optionalPath = finder.traverse("aaa", "aaaa");
		assertThat(optionalPath.isPresent(), equalTo(false));
	}
}
