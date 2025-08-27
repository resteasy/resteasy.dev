///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS info.picocli:picocli:4.6.3


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "fixdate", mixinStandardHelpOptions = true, version = "fixdate 0.1",
        description = "fixdate made with jbang")
class fixdate implements Callable<Integer> {

    @Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    private String greeting;

    public static void main(String... args) {
        int exitCode = new CommandLine(new fixdate()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        try {
            Files.walk(Paths.get("../content/posts"))
                    .filter(Files::isRegularFile)
//                    .filter(Files::)// Filter to process only regular files
                    .forEach(this::fixDate); // Print the path of each file
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void fixDate(Path path) {
        Pattern pattern = Pattern.compile("date:\\s+([a-zA-Z].*)");

        try {
            String contents = Files.readString(path);
//            blargh(contents);
            Matcher matcher = pattern.matcher(contents);
            if (matcher.find()) {
                String dateLine = matcher.group(1);
                System.out.println(dateLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void blargh(String args) {
        // The pattern to match strings like "date:  2018-05-29 09:13:19".
        // Let's break down the regex:
        // "date:"           - Matches the literal string "date:".
        // "\\s+"            - Matches one or more whitespace characters.
        // "([A-Z][a-z]+)"   - Matches a month name. The parentheses create a capturing group.
        //                     [A-Z] for the first capital letter, [a-z]+ for the rest.
        // "\\s+"            - Matches one or more whitespace.
        // "(\\d{1,2})"      - Matches the day (1 or 2 digits). Capturing group.
        // ","               - Matches the literal comma.
        // "\\s+"            - Matches one or more whitespace.
        // "(\\d{4})"        - Matches the year (exactly 4 digits). Capturing group.
        // "\\s+"            - Matches one or more whitespace.
        // "(\\d{1,2}:\\d{2}:\\d{2})" - Matches the time in HH:mm:ss format. Capturing group.
        // "\\s+"            - Matches one or more whitespace.
        // "(AM|PM)"         - Matches either "AM" or "PM". Capturing group.
        String regex = "date:\\s+([A-Z][a-z]+)\\s+(\\d{1,2}),\\s+(\\d{4})\\s+(\\d{1,2}:\\d{2}:\\d{2})\\s+(|PM)";

        // Create a Pattern object from the regex string.
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object to find the pattern in the text.
        Matcher matcher = pattern.matcher(args);

        // Use matcher.find() to search for the pattern.
        if (matcher.find()) {
            // If a match is found, print the entire matched string.
            System.out.println("Match found!");
            System.out.println("Full matched string: " + matcher.group(0));

            // You can also access the capturing groups to get specific parts of the date.
            System.out.println("Month: " + matcher.group(1));
            System.out.println("Day: " + matcher.group(2));
            System.out.println("Year: " + matcher.group(3));
            System.out.println("Time: " + matcher.group(4));
            System.out.println("AM/PM: " + matcher.group(5));
        } else {
            System.out.println("No match found.");
        }
    }
}
