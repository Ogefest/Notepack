/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.engine;

/**
 *
 * @author lg
 */
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
