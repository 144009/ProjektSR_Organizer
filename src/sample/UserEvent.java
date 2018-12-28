package sample;

import java.util.Date;

/**
 * Created by Maciej on 2018-12-28 12:27
 */
public class UserEvent {
    private int id;
    private String name;
    private String desc;
    private Date eventDate;
    private Date eventTime;

    public UserEvent(int id, String name, String desc, Date eventDate, Date eventTime) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
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

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
}
