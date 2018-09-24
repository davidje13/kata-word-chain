package com.davidje13;

import com.davidje13.chain.WordChainFinder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {
	private final WordChainFinder finder = new WordChainFinder();

	public static void main(String[] args) {
		Main main = new Main();

		if (args.length == 0) {
			main.printInfo();
			return;
		}

		String wordListFile = args[0];

		try {
			main.loadWords(new File(wordListFile));
		} catch(IOException e) {
			System.err.println("Failed to load word list from " + wordListFile);
			return;
		}

		List<String> inputs = Stream.of(args)
				.skip(1)
				.map(String::toLowerCase)
				.collect(toList());

		if (inputs.isEmpty()) {
			main.reportFurthestWord();
		} else if (inputs.size() == 1) {
			main.reportFurthestWord(inputs.get(0));
		} else {
			main.reportWordChain(inputs.get(0), inputs.get(1));
		}
	}

	private void loadWords(File file) throws IOException {
		Files.lines(file.toPath(), StandardCharsets.UTF_8)
				.map(String::toLowerCase)
				.forEach(finder::registerWord);
	}

	private void reportFurthestWord() {
		List<String> path = finder.findGlobalFurthest();

		printPath(path);
	}

	private void reportFurthestWord(String from) {
		printPath(finder.findFurthest(from));
	}

	private void reportWordChain(String from, String to) {
		Optional<List<String>> path = finder.traverse(from, to);

		if (!path.isPresent()) {
			System.err.println("No word chain found!");
			return;
		}

		printPath(path.get());
	}

	private void printPath(Iterable<String> path) {
		for(String word : path) {
			System.out.println(word);
		}
	}

	private void printInfo() {
		System.err.println("Finds minimal word chains for the given words.");
		System.err.println();
		System.err.println("Usage:");
		System.err.println();
		System.err.println("  Find word chain:");
		System.err.println("    ./program <word_list_file> <word1> <word2>");
		System.err.println();
		System.err.println("  Find furthest reachable word:");
		System.err.println("    ./program <word_list_file> <word>");
		System.err.println();
		System.err.println("  Find longest word chain in dictionary (slow!):");
		System.err.println("    ./program <word_list_file>");
	}
}
