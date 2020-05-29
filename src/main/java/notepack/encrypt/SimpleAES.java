/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.encrypt;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import notepack.app.domain.Settings;
import notepack.app.domain.StringEncryption;

/**
 *
 * @author lg
 */
public class SimpleAES implements StringEncryption {

    private String randomVector = "5b8sa7d5758dlbp4";

    @Override
    public String encrypt(String key, String input) {

        try {
            IvParameterSpec iv = new IvParameterSpec(randomVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.substring(0, 32).getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(input.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String decrypt(String key, String input) {

        try {

            IvParameterSpec iv = new IvParameterSpec(randomVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.substring(0, 32).getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] originalText = cipher.doFinal(Base64.getDecoder().decode(input));

            return new String(originalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
