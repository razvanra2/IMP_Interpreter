public class Variable {
    public Object value;
    public varType type;
    public String name;
    public Variable(varType newType, String newName) {
        this.type = newType;
        this.name = newName;
    }

    public enum varType {
        boolvar,
        intvar
    }
}