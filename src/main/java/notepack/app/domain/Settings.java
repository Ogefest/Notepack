/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

/**
 *
 * @author lg
 */
public interface Settings {
    
    public void set(String name, String value);
    public String get(String name);
    public String get(String name, String defaultValue);
    
}
