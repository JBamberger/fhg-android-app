package de.jbamberger.api;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public class VPlanParserTest {

    @Test
    public void matcher() throws Exception {
        Matcher matcher = Pattern.compile("\\s*([^;]+)(;\\s*)?")
                .matcher("text/html; charset=iso-8859-1");
        Pattern charsetPattern = Pattern.compile("^charset=([^\\s]*)$");

        while (matcher.find()) {
            System.out.println("found: " + matcher.start() + " - " + matcher.end());
            System.out.println(matcher.group(1));
            Matcher m = charsetPattern.matcher(matcher.group(1));
            while (m.find()) {
                System.out.println("found: " + m.start() + " - " + m.end());
                System.out.println(m.group(1));

            }
        }
    }

    static class TestA {
        public String name;

        @Override
        public String toString() {
            return "TestA{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    static class TestB {
        public List<TestA> tests;
    }

    @Test
    public void gson() throws Exception {
        Gson gson = new Gson();

        TestB t = gson.fromJson("[{name: \"hello\", id: 1}]", TestB.class);

        System.out.println(t.tests);
    }
}