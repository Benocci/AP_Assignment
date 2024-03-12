
public class Main {
    public static void main(String[] args) throws Exception {
        int numElement = 5;
        Object[] objects = new Object[numElement];

        objects[0] = new Student("Francesco", "Benocci", 23);
        objects[1] = new Student("Jane", "Doe", 42);
        objects[2] = new Object();
        objects[3] = new Professor("Mario", "Rossi", 'c', true);

        XMLSerializer.serialize(objects, "src/test.xml");
    }
}
