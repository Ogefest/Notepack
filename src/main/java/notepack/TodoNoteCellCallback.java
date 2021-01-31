package notepack;

import notepack.app.domain.Todo;

public interface TodoNoteCellCallback {
    public void onTodoFinished(Todo todo);
    public void onTodoEdit(Todo todo);
}
