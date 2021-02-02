package notepack.app.domain;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public class Todo {

    private LocalDate dueDate;
    private LocalDate completeDate;
    private String summary;
    private boolean isFinished;
    private String uuid;
    private Note note;

    public Todo() {
        uuid = UUID.randomUUID().toString();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
        if (finished) {
            completeDate = LocalDate.now();
        } else {
            completeDate = null;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * move to TodoWrapper class
     * @param input
     * @return
     */
    public VToDo getIcalTodo(VToDo input) {
        Summary summary = input.getSummary();
        if (summary == null){
            summary = new Summary();
            input.getProperties().add(summary);
        }
        summary.setValue(getSummary());

        PercentComplete percentComplete = input.getPercentComplete();
        if (percentComplete == null) {
            percentComplete = new PercentComplete();
            input.getProperties().add(percentComplete);
        }
        if (isFinished()) {
            percentComplete.setValue("100");
        } else {
            percentComplete.setValue("0");
        }


        Due due = input.getDue();
        if (due == null && dueDate != null) {
            Timestamp ts = Timestamp.valueOf(getDueDate().atStartOfDay());
            Date dueDate = new Date(ts.getTime() + 86400000);
            input.getProperties().add(new Due(dueDate));
        }
        if (dueDate == null) {
            input.getProperties().remove(due);
        }
        if (dueDate != null && due != null) {
            Timestamp ts = Timestamp.valueOf(getDueDate().atStartOfDay());
            due.setDate(new Date(ts.getTime() + 86400000));
        }

        Status status = input.getStatus();
        if (status != null) {
            input.getProperties().remove(status);
        }
        status = new Status(isFinished() ? Status.VTODO_COMPLETED.getValue() : Status.VTODO_IN_PROCESS.getValue() );
        input.getProperties().add(status);

        Completed completed = input.getDateCompleted();
        if (completed != null && !isFinished()) {
            input.getProperties().remove(completed);
        }
        if (completed == null && isFinished()) {
            completed = new Completed(new DateTime());
            input.getProperties().add(completed);
        }

        return input;
    }

    /**
     * @TODO Move to TodoWrapper class
     * @return
     */
    public VToDo getIcalToDo() {
        VToDo todo = new VToDo();
        todo.getProperties().add(new Uid(UUID.randomUUID().toString()));
        todo.getProperties().add(new Created(new DateTime()));

        return getIcalTodo(todo);
    }

    public LocalDate getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(LocalDate completeDate) {
        this.completeDate = completeDate;
    }
}
