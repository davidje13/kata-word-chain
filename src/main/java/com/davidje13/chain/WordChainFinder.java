package com.davidje13.chain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

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
		int size = knownWords.size();

		List<String> index2word = new ArrayList<>(knownWords);
		Map<String, Integer> word2index = new HashMap<>(size);
		int[][] connected = new int[size][];

		for (int index = 0; index < size; ++ index) {
			word2index.put(index2word.get(index), index);
		}
		IntStream.range(0, size).parallel().forEach((index) ->
				connected[index] = getConnectedWords(index2word.get(index)).stream()
						.mapToInt(word2index::get)
						.toArray()
		);

		ThreadLocal<int[]> commonLinkBack = ThreadLocal.withInitial(() -> new int[size]);
		ThreadLocal<int[]> commonQueue = ThreadLocal.withInitial(() -> new int[size]);

		return IntStream.range(0, size).parallel()
				.mapToObj((fromIndex) -> {
					int[] linkBack = commonLinkBack.get();
					Arrays.fill(linkBack, -1);

					int[] queue = commonQueue.get();
					int queueHead = 0;
					int queueTail = 0;

					linkBack[fromIndex] = fromIndex;
					queue[queueHead] = fromIndex;
					++ queueHead;

					int curIndex = fromIndex;
					while (queueTail < queueHead) {
						curIndex = queue[queueTail];
						++ queueTail;
						for (int nextIndex : connected[curIndex]) {
							if (linkBack[nextIndex] == -1) {
								linkBack[nextIndex] = curIndex;
								queue[queueHead] = nextIndex;
								++ queueHead;
							}
						}
					}

					return followPathReverse(fromIndex, linkBack, curIndex);
				})
				.max(comparingInt(List::size))
				.map(this::reverseList)
				.orElse(emptyList())
				.stream()
				.map(index2word::get)
				.collect(toList());
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

	private <T> List<T> followPath(
			T first,
			Map<T, T> linkBack,
			T mid,
			Map<T, T> linkNext,
			T last
	) {
		Deque<T> result = new ArrayDeque<>();

		T cur;
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

	private List<Integer> followPathReverse(
			int first,
			int[] linkBack,
			int last
	) {
		List<Integer> result = new ArrayList<>();

		int cur = last;
		result.add(cur);
		while (cur != first) {
			cur = linkBack[cur];
			result.add(cur);
		}

		return result;
	}

	private <T> List<T> reverseList(List<T> list) {
		List<T> result = new ArrayList<>(list);
		Collections.reverse(result);
		return result;
	}
}
