import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class JobScheduler<K, V> {

    public static void main(String[] args) {
        JobScheduler js = null; // Variable to hold JobScheduler instance
        Class c = null; // Variable to hold Class object
        String className = "AnagramCounter"; // Name of the class to be loaded dynamically

        try {
            c = Class.forName(className); // Load the class dynamically by name

            // Check if the loaded class extends JobScheduler
            if (!JobScheduler.class.isAssignableFrom(c)) {
                throw new IllegalArgumentException(c.getName() + " doesn't extend JobScheduler.");
            }

            // Instantiate the JobScheduler subclass
            js = (JobScheduler) c.getDeclaredConstructor().newInstance();
        } catch (IllegalArgumentException iae) {
            // Handle case where the class doesn't extend JobScheduler
            System.out.println(iae.getMessage());
            return;
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            // Handle various exceptions related to class loading and instantiation
            System.out.println("An error occurs during the instantiation of " + className);
            e.printStackTrace();
            return;
        }

        // Print the name of the class being executed
        System.out.println(c.getName() + " in execution.");

        // Call the workflow methods: emit, compute, collect, and output
        js.output(js.collect(js.compute(js.emit())));
    }

    // Abstract method to be implemented by subclasses to emit jobs
    abstract public Stream<AJob<K, V>> emit();

    // Method to process the stream of jobs and return a stream of key-value pairs
    private Stream<Pair<K, V>> compute(Stream<AJob<K, V>> jobs) {
        return jobs.flatMap(job -> job.execute()); // Execute each job and flatten the result
    }

    // Method to collect and group the key-value pairs by key
    private Stream<Pair<K, List<V>>> collect(Stream<Pair<K, V>> pairStream) {
        return pairStream
                .collect(
                        Collectors.groupingBy(
                                Pair::getKey, // Group by key
                                Collectors.mapping(
                                        Pair::getValue, // Collect values into a list
                                        Collectors.toList()
                                )
                        )
                )
                .entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue())); // Convert Map entries back to a stream of pairs
    }

    // Abstract method to be implemented by subclasses to output the result
    abstract public void output(Stream<Pair<K, List<V>>> pairStream);
}
