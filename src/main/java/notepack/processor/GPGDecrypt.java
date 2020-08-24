package notepack.processor;

import notepack.app.domain.exception.MessageError;
import notepack.encrypt.GPG;


public class GPGDecrypt implements NoteProcessor {
    
    private GPG gpg;

    public GPGDecrypt(GPG gpg) {
        this.gpg = gpg;
    }
    
    @Override
    public byte[] run(byte[] input) throws MessageError {
        return gpg.decrypt(input);
    }

}
