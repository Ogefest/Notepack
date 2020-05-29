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
public interface StringEncryption {
    public String encrypt(String key, String input);
    public String decrypt(String key, String input);
}
