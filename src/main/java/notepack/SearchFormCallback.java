/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

/**
 *
 * @author lg
 */
public interface SearchFormCallback {
    public void search(String string);
    public void replace(String from, String to, boolean replaceAll);
}
