import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class AnagramCounter extends JobScheduler<String, String>{

    @Override
    public Stream<AJob<String, String>> emit() throws IllegalArgumentException{

        String absPath = "/mnt/storage/Magistrale/AP/Assignment/Assignment2Ex1/Books";

        Path directory = Paths.get(absPath);

        if(!Files.exists(directory) || !Files.isDirectory(directory)){
            throw new IllegalArgumentException("Error, directory doesn't exists");
        }

        if(!directory.isAbsolute()){
            throw new IllegalArgumentException("Error, directory doesn't absolute");
        }

        try{
            return Files.list(directory)
                    .filter(d -> d.toString().endsWith(".txt"))
                    .map(path -> new CounterJob(path.toString()));
        }
        catch (IOException e){
            System.out.println("IO error opening the directory: " + directory);
            e.printStackTrace();
            return Stream.empty();
        }
    }

    @Override
    public void output(Stream<Pair<String, List<String>>> pairStream) {
        String filePath = "output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            pairStream.map(pair -> pair.getKey()+" "+pair.getValue().size())
            .forEach(word -> {
                try {
                    writer.write(word);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("IO error writing in to file: " + filePath);
                }
            });
            System.out.println("Output wrote in " + filePath);
        } catch (IOException e) {
            System.out.println("IO error opening the file: " + filePath);
            e.printStackTrace();
        }
    }
}
