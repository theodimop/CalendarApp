package model;

import java.util.Calendar;

/**
 * Class for the Event.
 */
public class Event {

    private int id;
    private String title;
    private Calendar startDate;
    private Calendar endDate;
    private String location;
    private String owner;

    /**
     * Event's constructor.
     * @param title     The title.
     * @param startDate The start date.
     * @param endDate   The end date.
     * @param location  The location.
     * @param owner     The owner/creator.
     */
    public Event(String title, Calendar startDate, Calendar endDate, String location, String owner) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.owner = owner;
    }

    /**
     * Getter.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter.
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter.
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter.
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter.
     * @return the start date.
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * Setter.
     * @param startDate the start date.
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * Getter.
     * @return the end date.
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * Setter.
     * @param endDate the end date.
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter.
     * @return the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter.
     * @param location the location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter.
     * @return the owner/creator.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Getter.
     * @param owner the owner/creator.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

}
