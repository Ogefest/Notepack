package notepack.app.task;

import notepack.app.listener.GuiListener;

public class ShowUserMessage implements TypeGui {
    
    public enum TYPE {
        INFO,
        ERROR
    }
    
    @Override
    public void notify(GuiListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
