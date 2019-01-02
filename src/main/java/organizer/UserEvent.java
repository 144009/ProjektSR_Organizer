package organizer;

import java.util.Date;

/**
 * Created by Maciej on 2018-12-28 12:27
 */
public class UserEvent {
    private int id;
    private String name;
    private String desc;
    private Date eventBegin;
    private Date eventEnd;

    public UserEvent(int id, String name, String desc, Date eventBegin, Date eventEnd) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.eventBegin = eventBegin;
        this.eventEnd = eventEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getEventBegin() {
        return eventBegin;
    }

    public void setEventBegin(Date eventBegin) {
        this.eventBegin = eventBegin;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
    }
}
