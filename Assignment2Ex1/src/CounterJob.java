import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CounterJob extends AJob<String, String>{
    private String file;

    public CounterJob(String file){
        this.file = file;
    }

    @Override
    public Stream<Pair<String, String>> execute() {
        BufferedReader bufferedReader = null;

        try{
            bufferedReader = new BufferedReader(new FileReader(this.file));
        } catch (FileNotFoundException e){
            System.out.println("File " + this.file + " not found");
            return Stream.empty();
        }

        return bufferedReader
                .lines()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter( s -> s.matches("^[a-zA-Z]{4,}$"))
                .map(String::toLowerCase)
                .map(s -> new Pair<>(ciao(s), s));
    }

    private String ciao(String s){
        return Stream.of(s.split("")).sorted().collect(Collectors.joining());
    }
}
