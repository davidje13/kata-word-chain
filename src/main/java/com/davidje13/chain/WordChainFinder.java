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

import static java.util.Collections.singletonList;

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

		if (from.equals(to)) {
			return Optional.of(singletonList(from));
		}

		Map<String, String> linkBack = new HashMap<>(knownWords.size() / 2);
		Map<String, String> linkNext = new HashMap<>(knownWords.size() / 2);
		Deque<String> queue = new ArrayDeque<>(knownWords.size());
		linkBack.put(from, from);
		linkNext.put(to, to);
		queue.addLast(from);
		queue.addLast(to);

		while (!queue.isEmpty()) {
			String cur = queue.removeFirst();
			boolean forward = linkBack.containsKey(cur);
			Map<String, String> linker = forward ? linkBack : linkNext;
			Map<String, String> other = forward ? linkNext : linkBack;
			for (String next : getConnectedWords(cur)) {
				if (linker.putIfAbsent(next, cur) != null) {
					continue;
				}
				if (other.containsKey(next)) {
					return Optional.of(followPath(
							from,
							linkBack,
							next,
							linkNext,
							to
					));
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

	private List<String> followPath(
			String first,
			Map<String, String> linkBack,
			String mid,
			Map<String, String> linkNext,
			String last
	) {
		Deque<String> result = new ArrayDeque<>();

		String cur;
		cur = mid;
		while (!cur.equals(first)) {
			cur = linkBack.get(cur);
			result.addFirst(cur);
		}

		result.addLast(mid);

		cur = mid;
		while (!cur.equals(last)) {
			cur = linkNext.get(cur);
			result.addLast(cur);
		}

		return new ArrayList<>(result);
	}
}
