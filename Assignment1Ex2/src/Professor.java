@XMLable
public class Professor {
    @XMLfield(type="String")
    public String firstName;
    @XMLfield(type = "String", name = "surname")
    public String lastName;
    @XMLfield(type = "char", name = "id")
    public char sigla;
    @XMLfield(type = "boolean")
    private boolean isAbsent;

    public Professor(){}
    public Professor(String fn, String ln, char sigla, boolean isAbsent) {
        this.firstName = fn;
        this.lastName = ln;
        this.sigla = sigla;
        this.isAbsent = isAbsent;
    }
}
