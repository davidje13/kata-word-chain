package com.davidje13.chain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;

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
		if (from.isEmpty() || from.length() != to.length()) {
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

	public List<String> findFurthest(String from) {
		if (!knownWords.contains(from)) {
			return singletonList(from);
		}

		Map<String, String> linkBack = new HashMap<>(knownWords.size());
		Deque<String> queue = new ArrayDeque<>(knownWords.size());
		linkBack.put(from, from);
		queue.addLast(from);

		String cur = from;
		while (!queue.isEmpty()) {
			cur = queue.removeFirst();
			for (String next : getConnectedWords(cur)) {
				if (linkBack.putIfAbsent(next, cur) == null) {
					queue.addLast(next);
				}
			}
		}

		return followPath(
				from,
				linkBack,
				cur,
				null,
				cur
		);
	}

	public List<String> findGlobalFurthest() {
		return findGlobalFurthest((progress) -> {});
	}

	public List<String> findGlobalFurthest(Consumer<Double> progressCallback) {
		int size = knownWords.size();
		Map<String, Boolean> observed = new ConcurrentHashMap<>(size);
		AtomicInteger progress = new AtomicInteger(0);

		return knownWords.parallelStream()
				.map((from) -> {
					if (observed.containsKey(from)) {
						return Collections.<String>emptyList();
					}
					List<String> path = findFurthest(from);
					observed.putAll(path.stream().collect(toMap(
							(word) -> word,
							(word) -> Boolean.TRUE
					)));
					return path;
				})
				.filter((list) -> !list.isEmpty())
				.peek((list) -> progressCallback.accept(
						progress.incrementAndGet() / (double) size
				))
				.max(comparingInt(List::size))
				.orElse(emptyList());
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
