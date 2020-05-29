package notepack.encrypt;

import notepack.app.domain.StringEncryption;

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
