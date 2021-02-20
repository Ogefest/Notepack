package notepack.app.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
    public static String app() {
        InputStream in = Version.class.getClassLoader().getResourceAsStream("build-info.properties");
        Properties props = new Properties();
        String version = "unknown";
        try {
            props.load(in);
            version = (String) props.get("version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}
