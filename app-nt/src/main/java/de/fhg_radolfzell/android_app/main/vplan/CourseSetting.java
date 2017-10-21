package de.fhg_radolfzell.android_app.main.vplan;

import android.support.annotation.NonNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public class CourseSetting {

    private final String mGrade;
    private final String[] mCourses;

    public CourseSetting(@NonNull String mGrade, @NonNull String[] mCourses) {
        this.mGrade = mGrade;
        this.mCourses = mCourses;
    }

    public String getGrade() {
        return mGrade;
    }

    @NonNull
    public String[] getCourses() {
        return mCourses;
    }

    public boolean getAllSelected() {
        return mCourses.length == 0;
    }

    public String getGradeMatcher() {
        StringBuilder builder = new StringBuilder();
        char[] grade = mGrade.toLowerCase().toCharArray();
        if(grade.length >= 2 && grade[0] == 'k'){
            builder.append(".*[Kk](1|2)").append(grade[1]).append(".*");
        } else {
            //FIXME: generate correct pattern
            builder.append(".*10[^0-9]*[dD].*");
        }
        return builder.toString();
    }

    public String getCourseMatcher() {
        if (mCourses.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mCourses.length - 1; i++) {
                builder.append(mCourses[i]).append("|");
            }
            builder.append(mCourses[mCourses.length]);
            return builder.toString();
        } else {
            return ".*";
        }
    }
}
