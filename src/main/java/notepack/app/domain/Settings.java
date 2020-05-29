package notepack.app.domain;

public interface Settings {
    
    public void set(String name, String value);
    public String get(String name);
    public String get(String name, String defaultValue);
    
}
