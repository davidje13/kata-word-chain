package com.davidje13;

import org.junit.Test;

import static com.davidje13.testutil.IntegrationTestUtils.getResource;
import static com.davidje13.testutil.IntegrationTestUtils.getStdErrFrom;
import static com.davidje13.testutil.IntegrationTestUtils.getStdOutFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MainIntegrationTest {
	@Test
	public void main_reportsTheShortestWordListToStdOut() {
		String out = getStdOutFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"cat",
				"dog"
		}));

		assertThat(out, equalTo("cat\ncot\ncog\ndog\n"));
	}

	@Test
	public void main_reportsIfNoChainCanBeFound() {
		String out = getStdOutFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"do",
				"at"
		}));

		assertThat(out, equalTo(""));

		String err = getStdErrFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"do",
				"at"
		}));

		assertThat(err, equalTo("No word chain found!\n"));
	}
}
