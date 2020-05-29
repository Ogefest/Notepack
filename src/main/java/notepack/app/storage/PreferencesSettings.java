package notepack.app.storage;

import java.util.prefs.Preferences;
import notepack.app.domain.Settings;

public class PreferencesSettings implements Settings {
    
    private Preferences prefs;
    
    public PreferencesSettings() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    @Override
    public void set(String name, String value) {
        prefs.put(name, value);
    }

    @Override
    public String get(String name) {
        return get(name, "");
    }

    @Override
    public String get(String name, String defaultValue) {
        return prefs.get(name, defaultValue);
    }
    
}
