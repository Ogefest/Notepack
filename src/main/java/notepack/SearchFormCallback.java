package notepack;

public interface SearchFormCallback {
    public void search(String string);
    public void replace(String from, String to, boolean replaceAll);
}
