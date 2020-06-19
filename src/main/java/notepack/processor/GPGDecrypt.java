package notepack.processor;

import notepack.encrypt.GPG;


public class GPGDecrypt implements NoteProcessor {

    public GPGDecrypt(GPG gpg) {
        
    }
    
    @Override
    public String run(String input) {
        return input;
    }

}
