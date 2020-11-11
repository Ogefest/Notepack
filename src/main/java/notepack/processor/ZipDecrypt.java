package notepack.processor;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import notepack.app.domain.exception.MessageError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ZipDecrypt implements NoteProcessor {

    private String pwd;

    public ZipDecrypt(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public byte[] run(byte[] input) throws MessageError {

        ByteArrayInputStream bin = new ByteArrayInputStream(input);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ZipInputStream zipInput = new ZipInputStream(bin, pwd.toCharArray())) {
            int readLen;

            LocalFileHeader entryHeader = zipInput.getNextEntry();
            /*
            Probably note is not zip file
             */
            if (entryHeader == null) {
                return input;
            }

            byte[] readBuffer = new byte[4096];
            while ((readLen = zipInput.read(readBuffer)) != -1) {
                output.write(readBuffer, 0, readLen);
            }
            bin.close();
            output.close();
            zipInput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toByteArray();
    }

}
