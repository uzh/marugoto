package ch.uzh.marugoto.core.test.helpers;

import org.junit.Test;

import ch.uzh.marugoto.core.helpers.StringHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;

public class StringHelperTest {

    @Test
    public void testGenerateRandomString() {
        int length = 10;
        var testGenerated = StringHelper.generateRandomString(length);
        assertThat(testGenerated.length(), is(length));

        // test that there won't be duplicates
        int count = 0;
        while (count < 1000000) {
            count++;
            assertNotEquals(testGenerated, StringHelper.generateRandomString(length));
        }
    }
}