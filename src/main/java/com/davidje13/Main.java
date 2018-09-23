package com.davidje13;

import com.davidje13.chain.WordChainFinder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		Charset utf8 = StandardCharsets.UTF_8;

		try {
			Stream<String> words = Files.lines(new File(args[0]).toPath(), utf8);
			WordChainFinder finder = new WordChainFinder(words);
			Optional<List<String>> path = finder.traverse(args[1], args[2]);
			if (!path.isPresent()) {
				System.err.println("No word chain found!");
			} else {
				for (String word : path.get()) {
					System.out.println(word);
				}
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
