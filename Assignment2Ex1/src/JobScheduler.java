import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JobScheduler<K,V> {

    //Frozen Spot
    public static void main(String[] args) {
        JobScheduler js = null;
        Class c = null;
        String className = "AnagramCounter";

        try {
            c = Class.forName(className);

            if(!JobScheduler.class.isAssignableFrom(c)){
                throw new IllegalArgumentException(c.getName() + " doesn't extend JobScheduler.");
            }

            js = (JobScheduler) c.getDeclaredConstructor().newInstance();
        } catch (IllegalArgumentException iae){
            System.out.println(iae.getMessage());
            return;
        } catch(ClassNotFoundException | InvocationTargetException | InstantiationException |
                IllegalAccessException | NoSuchMethodException e) {
            System.out.println("An error occurs during the instantiation of "+className);
            e.printStackTrace();
            return;
        }

        System.out.println(c.getName()+" in execution.");
        js.output(js.collect(js.compute(js.emit())));
    }

    // Hot Spot
    abstract public Stream<AJob<K,V>> emit();


    //Frozen Spot
    private Stream<Pair<K,V>> compute (Stream<AJob<K,V>> jobs){
        //System.out.println("DEBUG: compute start!");
        return jobs.flatMap(job -> job.execute());
    }

    //Frozen Spot
    private Stream<Pair<K, List<V>>> collect (Stream<Pair<K,V>> pairStream){
        //System.out.println("DEBUG: collect start!");

        return pairStream
                .collect(
                Collectors.groupingBy(
                    Pair::getKey,
                    Collectors.mapping(
                        Pair::getValue,
                        Collectors.toList()
                    )
                )
        )
        .entrySet()
        .stream()
        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));

    }

    //Hot Spot
    abstract public void output(Stream<Pair<K, List<V>>> pairStream);
}
