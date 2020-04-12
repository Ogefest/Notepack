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
public interface SearchForNoteCallback {
    public void openNote(Note note);
}
