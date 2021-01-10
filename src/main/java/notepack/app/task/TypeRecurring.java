package notepack.app.task;

public interface TypeRecurring {
    public int getInterval();
    public void startTaskAfterSecondsFromNow(int seconds);
}
