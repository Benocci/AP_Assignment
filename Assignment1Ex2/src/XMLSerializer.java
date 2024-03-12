import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class XMLSerializer {

    static void serialize(Object []arr, String fileName){

        ArrayList<String> result = new ArrayList<>();
        Map<String, IntrospectedClass> introspectedClasses = new HashMap<>();
        result.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        for(Object o: arr){ // Main loop
            if(o == null){
                continue;
            }

            // Get the class of the object and introspect the class if not already introspected
            Class<?> c = o.getClass();
            introspectedClasses.putIfAbsent(c.getName(), new IntrospectedClass(c));
            IntrospectedClass classToAnalize = introspectedClasses.get(c.getName());

            if (classToAnalize.isLabeled()) { // If class is labled for XML serialization
                result.add("<" + classToAnalize.getName() + ">");

                for (MyField f: classToAnalize.getFields()){ // Iterate through fields of the class
                    if(!f.isLabeled()){
                        continue;
                    }

                    try {
                        // Serialize the field with its type and its value
                        Field field = c.getDeclaredField(f.getName());
                        field.setAccessible(true);
                        String fieldName = f.getAlias().isEmpty() ? f.getName() : f.getAlias();
                        result.add("\t<" + fieldName + " type=\"" + f.getType() +"\">" + field.get(o) + "</" + fieldName + ">");
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        // Throw a runtime exception if unable to access the field
                        throw new RuntimeException(e);
                    }
                }

                result.add("</" + classToAnalize.getName() + ">");
            }
            else{ // If the class is not XMLable add the placeholder label
                result.add("<notXMLable />");
            }
        }

        // Create a Path object for the output file and join the serialized data with new line char
        Path filePath = Paths.get(fileName);
        String finalResult = String.join("\n", result);
        try {
            // Write the string to the file
            Files.write(filePath, finalResult.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // DEBUG: print serialized data
        for (String s: result) {
            System.out.println(s);
        }
    }

    // Inner class representing a field with its properties:
    private static class MyField {
        private final String name;
        private final String alias;
        private final String type;
        private final boolean labeled;

        public MyField(String name, String type, String alias, boolean labeled){
            this.name = name;
            this.alias = alias;
            this.type = type;
            this.labeled = labeled;
        }

        public boolean isLabeled() {
            return labeled;
        }

        public String getName() {
            return name;
        }

        public String getAlias() {
            return alias;
        }

        public String getType() {
            return type;
        }
    }

    // Inner class representing an introspected class with its fields:
    private static class IntrospectedClass {
        private final String name;
        private final boolean labeled;
        private final ArrayList<MyField> fields = new ArrayList<>();

        public IntrospectedClass(Class c){
            this.name = c.getName();

            // Check if the class is annotated for XML serialization
            if (c.isAnnotationPresent(XMLable.class)) {
                labeled = true;

                // Iterate through fields of the class
                for (Field field : c.getDeclaredFields()) {
                    // Check if the field is annotated for XML serialization
                    if (field.isAnnotationPresent(XMLfield.class)) {
                        XMLfield xmlfield = field.getAnnotation(XMLfield.class);
                        String fieldName = field.getName();
                        String fieldAlias = xmlfield.name();
                        String fieldType = xmlfield.type();
                        fields.add(new MyField(fieldName, fieldType, fieldAlias, true));
                    }
                    else{// If not annotated add the field with no label
                        fields.add(new MyField(field.getName(), "", "", false));
                    }
                }
            }
            else{ // If is not annotated with XMLable mark the class
                labeled = false;
            }
        }

        public boolean isLabeled() {
            return labeled;
        }

        public String getName() {
            return name;
        }

        public ArrayList<MyField> getFields() {
            return fields;
        }
    }
}
