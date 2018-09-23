package com.davidje13;

import com.davidje13.testutil.IntegrationTestUtils.Output;
import org.junit.Test;

import static com.davidje13.testutil.IntegrationTestUtils.getOutputFrom;
import static com.davidje13.testutil.IntegrationTestUtils.getResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MainIntegrationTest {
	@Test
	public void main_reportsTheShortestWordListToStdOut() {
		Output output = getOutputFrom(() -> Main.main(new String[]{
				getResource("word-list.txt").getPath(),
				"cat",
				"dog"
		}));

		assertThat(output.out, equalTo("cat\ncot\ncog\ndog\n"));
		assertThat(output.err, equalTo(""));
	}

	@Test
	public void main_isCaseInsensitiveForInputWords() {
		Output output = getOutputFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"CAT",
				"DOG"
		}));

		assertThat(output.out, equalTo("cat\ncot\ncog\ndog\n"));
		assertThat(output.err, equalTo(""));
	}

	@Test
	public void main_loadsWordsFromWordListInLowercase() {
		Output output = getOutputFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"gold",
				"gold"
		}));

		assertThat(output.out, equalTo("gold\n"));
		assertThat(output.err, equalTo(""));
	}

	@Test
	public void main_reportsIfNoChainCanBeFound() {
		Output output = getOutputFrom(() -> Main.main(new String[] {
				getResource("word-list.txt").getPath(),
				"do",
				"at"
		}));

		assertThat(output.out, equalTo(""));
		assertThat(output.err, equalTo("No word chain found!\n"));
	}

	@Test
	public void main_reportsIfTheWordListCannotBeLoaded() {
		Output output = getOutputFrom(() -> Main.main(new String[] {"nope"}));

		assertThat(output.out, equalTo(""));
		assertThat(output.err, equalTo(
				"Failed to load word list from nope\n"
		));
	}

	@Test
	public void main_showsUsageInformationIfNoParametersGiven() {
		Output output = getOutputFrom(() -> Main.main(new String[] {}));

		assertThat(output.out, equalTo(""));
		assertThat(output.err, equalTo(
				"Finds minimal word chains for the given words.\n" +
				"\n" +
				"Usage:\n" +
				"  ./program <word_list_file> <word1> <word2>\n"
		));
	}
}
