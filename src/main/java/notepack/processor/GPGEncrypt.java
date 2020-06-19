package notepack.processor;

import notepack.encrypt.GPG;


public class GPGEncrypt implements NoteProcessor {

    public GPGEncrypt(GPG gpg) {
        
    }
    
    @Override
    public String run(String input) {
        return input;
    }

}
