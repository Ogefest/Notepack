package notepack.processor;

import notepack.app.domain.exception.MessageError;

public interface NoteProcessor {

    public String run(String input) throws MessageError;
}
