package com.davidje13.testutil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;

public class IntegrationTestUtils {
	public static URL getResource(String name) {
		ClassLoader loader = IntegrationTestUtils.class.getClassLoader();
		return loader.getResource(name);
	}

	public static String getStdOutFrom(Runnable runnable) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stdout = System.out;

		try {
			System.setOut(new PrintStream(out));
			runnable.run();
			return out.toString();
		} finally {
			System.setOut(stdout);
		}
	}

	public static String getStdErrFrom(Runnable runnable) {
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		PrintStream stderr = System.err;

		try {
			System.setErr(new PrintStream(err));
			runnable.run();
			return err.toString();
		} finally {
			System.setErr(stderr);
		}
	}
}
