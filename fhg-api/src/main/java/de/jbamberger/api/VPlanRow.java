package de.jbamberger.api;

import android.support.annotation.NonNull;

import static de.jbamberger.util.Preconditions.checkNotNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanRow {

    @NonNull
    private final String subject;

    private final boolean omitted;

    @NonNull
    private final String hour;

    @NonNull
    private final String room;

    @NonNull
    private final String content;

    @NonNull
    private final String grade;

    @NonNull
    private final String kind;

    private final boolean markedNew;

    VPlanRow(@NonNull String subject, boolean omitted, @NonNull String hour, @NonNull String room, @NonNull String content, @NonNull String grade, @NonNull String kind, boolean markedNew) {
        this.subject = checkNotNull(subject);
        this.omitted = omitted;
        this.hour = checkNotNull(hour);
        this.room = checkNotNull(room);
        this.content = checkNotNull(content);
        this.grade = checkNotNull(grade);
        this.kind = checkNotNull(kind);
        this.markedNew = markedNew;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public boolean isOmitted() {
        return omitted;
    }

    @NonNull
    public String getHour() {
        return hour;
    }

    @NonNull
    public String getRoom() {
        return room;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public String getGrade() {
        return grade;
    }

    @NonNull
    public String getKind() {
        return kind;
    }

    public boolean isMarkedNew() {
        return markedNew;
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
                ", markedNew=" + markedNew +
                '}';
    }
}