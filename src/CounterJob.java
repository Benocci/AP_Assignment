import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CounterJob extends AJob<String, String> {
    private String file; // File path to be processed

    // Constructor to initialize the file path
    public CounterJob(String file) {
        this.file = file;
    }

    // Override the execute method to process the file and return a stream of key-value pairs
    @Override
    public Stream<Pair<String, String>> execute() {
        BufferedReader bufferedReader = null;

        try {
            // Attempt to open the file using BufferedReader
            bufferedReader = new BufferedReader(new FileReader(this.file));
        } catch (FileNotFoundException e) {
            // Handle case where the file is not found
            System.out.println("File " + this.file + " not found");
            return Stream.empty(); // Return an empty stream in case of error
        }

        // Process the lines in the file
        return bufferedReader
                .lines() // Read lines from the file
                .flatMap(s -> Arrays.stream(s.split(" "))) // Split lines into words
                .filter(s -> s.matches("^[a-zA-Z]{4,}$")) // Filter words with 4 or more letters
                .map(String::toLowerCase) // Convert words to lowercase
                .map(s -> new Pair<>(ciao(s), s)); // Map words to key-value pairs where key is the sorted word
    }

    // Private method to sort characters of a string alphabetically
    private String ciao(String s) {
        return Stream.of(s.split("")) // Split the string into individual characters
                .sorted() // Sort the characters
                .collect(Collectors.joining()); // Join the sorted characters back into a string
    }
}
