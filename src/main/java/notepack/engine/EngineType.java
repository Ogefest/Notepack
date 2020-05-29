package notepack.engine;

public class EngineType {
    private String name;
    private String fxml;
    
    public EngineType(String name, String fxml) {
        this.name = name;
        this.fxml = fxml;
    }
    
    public String toString() {
        return name;
    }
    
    public String getFxml() {
        return fxml;
    }
}
