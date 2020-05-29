/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.encrypt;

import notepack.app.domain.StringEncryption;

/**
 *
 * @author lg
 */
public class Fake implements StringEncryption {

    @Override
    public String encrypt(String key, String input) {
        return input;
    }

    @Override
    public String decrypt(String key, String input) {
        return input;
    }
    
}
