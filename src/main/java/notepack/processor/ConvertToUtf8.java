package notepack.processor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ConvertToUtf8 implements NoteProcessor {
    @Override
    public byte[] run(byte[] input) {

        Charset systemCharset = Charset.defaultCharset();
        Charset utf8Charset = StandardCharsets.UTF_8;

        String str = new String(input, systemCharset);

        return str.getBytes(utf8Charset);
    }
}
