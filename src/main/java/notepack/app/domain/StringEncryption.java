package notepack.app.domain;

public interface StringEncryption {
    public String encrypt(String key, String input);
    public String decrypt(String key, String input);
}
