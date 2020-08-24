package notepack.processor;

import notepack.app.domain.exception.MessageError;

public interface NoteProcessor {

    public byte[] run(byte[] input) throws MessageError;
}
