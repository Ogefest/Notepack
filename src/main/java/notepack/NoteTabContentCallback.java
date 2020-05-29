package notepack;

import notepack.app.domain.Note;

public interface NoteTabContentCallback {
    public void onSaveNote(Note n);
    public void onOpenNote();
    public void onCloseNote(Note n);
}
