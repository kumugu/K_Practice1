package model;

import java.util.Date;

public class Report {
    private int id;
    private String title;
    private Date date;
    private String details;

    // Constructor
    public Report(int id, String title, Date date, String details) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.details = details;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }
}
