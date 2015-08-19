package xyz.jbapps.vplanapp.data;

import com.google.gson.annotations.SerializedName;

public class VPlanRow {

    public static final int SUBJECT = 0;
    public static final int OMITTED = 1;
    public static final int HOUR = 2;
    public static final int ROOM = 3;
    public static final int CONTENT = 4;
    public static final int GRADE = 5;
    public static final int MARKED_NEW = 6;

    @SerializedName("subject")
    private String subject;
    @SerializedName("omitted")
    private boolean omitted;
    @SerializedName("hour")
    private String hour;
    @SerializedName("room")
    private String room;
    @SerializedName("content")
    private String content;
    @SerializedName("grade")
    private String grade;
    @SerializedName("marked_new")
    private boolean marked_new;


    public VPlanRow(String grade, String hour, String content, String subject, String room, boolean omitted, boolean marked_new) {
        this.grade = grade;
        this.subject = subject;
        this.room = room;
        this.hour = hour;
        this.content = content;
        this.omitted = omitted;
        this.marked_new = marked_new;
    }

    public VPlanRow() {
    }

    public boolean getMarkedNew() {
        return marked_new;
    }

    public void setMarkedNew(boolean marked_new) {
        this.marked_new = marked_new;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean getOmitted() {
        return omitted;
    }

    public void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


}
