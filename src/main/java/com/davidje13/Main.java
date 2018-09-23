package com.davidje13;

import com.davidje13.chain.WordChainFinder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			printInfo();
			return;
		}

		String wordListFile = args[0];

		WordChainFinder finder = new WordChainFinder();
		try {
			Files.lines(new File(wordListFile).toPath(), StandardCharsets.UTF_8)
					.map(String::toLowerCase)
					.forEach(finder::registerWord);
		} catch(IOException e) {
			System.err.println("Failed to load word list from " + wordListFile);
			return;
		}

		Optional<List<String>> path = finder.traverse(
				args[1].toLowerCase(),
				args[2].toLowerCase()
		);

		if (!path.isPresent()) {
			System.err.println("No word chain found!");
			return;
		}

		for (String word : path.get()) {
			System.out.println(word);
		}
	}

	private static void printInfo() {
		System.err.println("Finds minimal word chains for the given words.");
		System.err.println();
		System.err.println("Usage:");
		System.err.println("  ./program <word_list_file> <word1> <word2>");
	}
}
