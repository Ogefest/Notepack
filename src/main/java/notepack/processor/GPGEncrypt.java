package notepack.processor;

import notepack.app.domain.exception.MessageError;
import notepack.encrypt.GPG;

public class GPGEncrypt implements NoteProcessor {

    private GPG gpg;

    public GPGEncrypt(GPG gpg) {
        this.gpg = gpg;
    }

    @Override
    public byte[] run(byte[] input) throws MessageError {
        return gpg.encrypt(input);
    }

}
