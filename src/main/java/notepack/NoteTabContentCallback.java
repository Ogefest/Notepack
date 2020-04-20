/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import notepack.app.domain.Note;

/**
 *
 * @author lg
 */
public interface NoteTabContentCallback {
    public void onSaveNote(Note n);
    public void onOpenNote();
    public void onCloseNote(Note n);
}
