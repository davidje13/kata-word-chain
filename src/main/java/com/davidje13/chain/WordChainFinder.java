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

public class WordChainFinder {
	private final Collection<String> knownWords = new HashSet<>(1048576);
	private final Collection<Character> knownChars = new HashSet<>(256);

	public void registerWord(String word) {
		knownWords.add(word);

		for (char c : word.toCharArray()) {
			knownChars.add(c);
		}
	}

	public Optional<List<String>> traverse(String from, String to) {
		int size = from.length();
		if (size == 0 || to.length() != size) {
			return Optional.empty();
		}

		if (!knownWords.contains(from) || !knownWords.contains(to)) {
			return Optional.empty();
		}

		Map<String, String> parents = new HashMap<>(knownWords.size());
		Deque<String> queue = new ArrayDeque<>(knownWords.size());
		parents.put(from, from);
		queue.addLast(from);

		while (!queue.isEmpty()) {
			String cur = queue.removeFirst();
			for (String next : getConnectedWords(cur)) {
				if (parents.putIfAbsent(next, cur) != null) {
					continue;
				}
				if (next.equals(to)) {
					return Optional.of(followReversePath(parents, to));
				}
				queue.addLast(next);
			}
		}
		return Optional.empty();
	}

	private List<String> getConnectedWords(String word) {
		List<String> result = new ArrayList<>(128);
		char[] wordChars = word.toCharArray();

		for (int i = 0; i < wordChars.length; ++i) {
			char original = wordChars[i];
			for (char c : knownChars) {
				if (c == original) {
					continue;
				}
				wordChars[i] = c;
				String next = new String(wordChars);
				if (knownWords.contains(next)) {
					result.add(next);
				}
			}
			wordChars[i] = original;
		}

		return result;
	}

	private List<String> followReversePath(
			Map<String, String> parents,
			String last
	) {
		Deque<String> result = new ArrayDeque<>();

		String cur = last;
		while (true) {
			result.addFirst(cur);
			String prev = parents.get(cur);
			if (prev.equals(cur)) {
				return new ArrayList<>(result);
			}
			cur = prev;
		}
	}
}
