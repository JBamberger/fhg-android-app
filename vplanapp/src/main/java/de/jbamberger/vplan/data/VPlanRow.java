package de.jbamberger.vplan.data;

import com.google.gson.annotations.SerializedName;

public class VPlanRow extends VPlanElement {

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
    @SerializedName("kind")
    private String kind;
    @SerializedName("marked_new")
    private boolean marked_new;

    public VPlanRow() {
        super(VPlanElement.TYPE_ROW);
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "VPlanRow{" +
                "subject='" + subject + '\'' +
                ", omitted=" + omitted +
                ", hour='" + hour + '\'' +
                ", room='" + room + '\'' +
                ", content='" + content + '\'' +
                ", grade='" + grade + '\'' +
                ", kind='" + kind + '\'' +
                ", marked_new=" + marked_new +
                '}';
    }
}
