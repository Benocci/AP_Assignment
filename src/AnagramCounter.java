import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;


public class AnagramCounter extends JobScheduler<String, String> {

    // Implement the abstract emit method to generate a stream of CounterJob
    @Override
    public Stream<AJob<String, String>> emit() throws IllegalArgumentException {

        // Define the absolute path to the directory containing text files
        String absPath = "/mnt/storage/Magistrale/AP/Assignment/Assignment2Ex1/Books";
        Path directory = Paths.get(absPath);

        // Check if the directory exists and is a directory
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Error, directory doesn't exists");
        }

        // Check if the directory path is absolute
        if (!directory.isAbsolute()) {
            throw new IllegalArgumentException("Error, directory doesn't absolute");
        }

        try {
            // List all .txt files in the directory and map each file to a CounterJob
            return Files.list(directory)
                    .filter(d -> d.toString().endsWith(".txt")) // Filter for .txt files
                    .map(path -> new CounterJob(path.toString())); // Create a new CounterJob for each file
        } catch (IOException e) {
            // Handle IOException if there's an error opening the directory
            System.out.println("IO error opening the directory: " + directory);
            e.printStackTrace();
            return Stream.empty(); // Return an empty stream in case of error
        }
    }

    // Implement the abstract output method to write the results to a file
    @Override
    public void output(Stream<Pair<String, List<String>>> pairStream) {
        String filePath = "output.txt"; // Define the output file path

        // Use BufferedWriter to write the results to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Process each pair, convert it to a string and write to the file
            pairStream.map(pair -> pair.getKey() + " " + pair.getValue().size()) // Convert each pair to "key size" string
                    .forEach(word -> {
                        try {
                            writer.write(word); // Write the string to the file
                            writer.newLine(); // Write a new line
                        } catch (IOException e) {
                            // Handle IOException during writing
                            e.printStackTrace();
                            System.out.println("IO error writing into file: " + filePath);
                        }
                    });
            System.out.println("Output wrote in " + filePath); // Print confirmation message
        } catch (IOException e) {
            // Handle IOException during file opening
            System.out.println("IO error opening the file: " + filePath);
            e.printStackTrace();
        }
    }
}
