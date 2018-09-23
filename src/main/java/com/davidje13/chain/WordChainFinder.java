package com.davidje13.chain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class WordChainFinder {
	private final Map<String, Integer> wordLookup = new HashMap<>();
	private final List<String> reverseWordLookup = new ArrayList<>();
	private final List<List<Integer>> connectivity = new ArrayList<>();
	private final Collection<Character> knownCharacters = new HashSet<>();

	public WordChainFinder(Iterable<String> words) {
		for (String word : words) {
			registerWord(word);
		}
	}

	public WordChainFinder(Stream<String> words) {
		words.forEach(this::registerWord);
	}

	private void registerWord(String word) {
		int index = connectivity.size();
		if (wordLookup.putIfAbsent(word, index) != null) {
			return;
		}

		connectivity.add(new ArrayList<>());
		reverseWordLookup.add(word);

		addConnectivity(word);
	}

	private void addConnectivity(String word) {
		int index = wordLookup.get(word);
		List<Integer> connections = connectivity.get(index);

		char[] wordChars = word.toCharArray();
		for (int i = 0; i < wordChars.length; ++ i) {
			char original = wordChars[i];
			for (char c : knownCharacters) {
				if (c == original) {
					continue;
				}
				wordChars[i] = c;
				String variant = new String(wordChars);
				Integer varIndex = wordLookup.get(variant);
				if (varIndex != null) {
					connections.add(varIndex);
					connectivity.get(varIndex).add(index);
				}
			}
			wordChars[i] = original;
			knownCharacters.add(original);
		}
	}

	public Optional<List<String>> traverse(String from, String to) {
		Integer fromIndex = wordLookup.get(from);
		Integer toIndex = wordLookup.get(to);
		if (fromIndex == null || toIndex == null) {
			throw new IllegalArgumentException("Unknown word");
		}

		List<Integer> path = traverse(fromIndex, toIndex);
		if (path == null) {
			return Optional.empty();
		}

		return Optional.of(path.stream()
				.map(reverseWordLookup::get)
				.collect(toList()));
	}

	private List<Integer> traverse(int fromIndex, int toIndex) {
		int size = connectivity.size();
		List<Integer> parents = new ArrayList<>();
		Deque<Integer> queue = new ArrayDeque<>();
		for (int i = 0; i < size; ++ i) {
			parents.add(-1);
		}
		parents.set(fromIndex, fromIndex);
		queue.addLast(fromIndex);

		all:
		while (true) {
			if (queue.isEmpty()) {
				return null;
			}
			int curIndex = queue.removeFirst();
			for (int nextIndex : connectivity.get(curIndex)) {
				if (parents.get(nextIndex) == -1) {
					parents.set(nextIndex, curIndex);
					if (nextIndex == toIndex) {
						break all;
					}
					queue.addLast(nextIndex);
				}
			}
		}
		return followReversePath(parents, toIndex);
	}

	private List<Integer> followReversePath(List<Integer> parents, int index) {
		Deque<Integer> result = new ArrayDeque<>();

		int curIndex = index;
		while (true) {
			result.addFirst(curIndex);
			int prevIndex = parents.get(curIndex);
			if (prevIndex == curIndex) {
				return new ArrayList<>(result);
			}
			curIndex = prevIndex;
		}
	}
}
