package notepack.app.domain;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import notepack.app.domain.exception.MessageError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

public class TodoWrapper {

    private Note note;
    private Calendar calendar;

    public TodoWrapper(Note note) {
        this.note = note;

        parseCalendar();
    }

    public ArrayList<Todo> getTodos() {
        ArrayList<Todo> result = new ArrayList<>();

        for (CalendarComponent component : calendar.getComponents()) {

            if (!component.getName().equals(Component.VTODO)) {
                continue;
            }

            Todo tmp = new Todo();
//            tmp.setNote(note);
            String Uid = component.getProperty(Property.UID).getValue();
            if (Uid != null) {
                tmp.setUuid(Uid);
            }

            String summary = component.getProperty(Property.SUMMARY).getValue();
            if (summary != null) {
                tmp.setSummary(summary);
            }

            Due d = component.getProperty(Property.DUE);
            if (d != null) {
                LocalDate dueDate = LocalDate.ofInstant(Instant.ofEpochMilli(d.getDate().getTime()), ZoneId.systemDefault());
                tmp.setDueDate(dueDate);
            }

            PercentComplete percentComplete = component.getProperty(Property.PERCENT_COMPLETE);
            if (percentComplete != null && percentComplete.getValue().equals("100")) {
                tmp.setFinished(true);
            }

            result.add(tmp);
        }

        return result;
    }

    public void setTodo(Todo todo) {

        Optional<CalendarComponent> component =  calendar.getComponents().stream().filter(calendarComponent -> {
            if (calendarComponent.getName().equals(Component.VTODO)) {
                return calendarComponent.getProperty(Property.UID).getValue().equals(todo.getUuid());
            }
            return false;
        }).findFirst();

        VToDo todoComponent;
        if (component.isPresent()) {
            todoComponent = (VToDo) component.get();
            todo.getIcalTodo(todoComponent);
        } else {
            todoComponent = todo.getIcalToDo();
            calendar.getComponents().add(todoComponent);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, out);
            note.setContents(out.toByteArray());
            note.saveToStorage();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    public void removeTodo(Todo todo) {
        //
    }

//    public Note getNote() {
//        return note;
//    }

    private void parseCalendar() {

        if (note.getContent().length == 0) {

            calendar = new Calendar();
            calendar.getProperties().add(new ProdId("-//Notepack"));
            calendar.getProperties().add(Version.VERSION_2_0);

            TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
            ZoneId zid = ZoneId.systemDefault();
            TimeZone timezone = registry.getTimeZone(zid.toString());
            VTimeZone tz = timezone.getVTimeZone();
            calendar.getComponents().add(tz);

            return;
        }

        calendar = null;
        try {
            StringReader sin = new StringReader(new String(note.getContent()));
            CalendarBuilder builder = new CalendarBuilder();
            calendar = builder.build(sin);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }

    }
}
