package notepack.processor;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import notepack.app.domain.exception.MessageError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ZipEncrypt extends NoteProcessor implements BinaryProcessor {

    private String pwd;

    public ZipEncrypt(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public byte[] run(byte[] input) throws MessageError {

        ZipParameters p = new ZipParameters();
        p.setEncryptFiles(true);
        p.setEncryptionMethod(EncryptionMethod.AES);

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(fos, pwd.toCharArray())) {

            p.setFileNameInZip("note.txt");
            zos.putNextEntry(p);

            zos.write(input, 0, input.length);
            zos.closeEntry();
            zos.close();
            fos.close();

            return fos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fos.toByteArray();
    }

}
