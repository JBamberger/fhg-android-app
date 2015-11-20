package xyz.jbapps.vplan.util;

import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.data.VPlanRow;

public class VPlanSorter {

    private static final String PATTERN_MATCHES_EVERYTHING = ".*";
    private static final String PATTERN_START = "(.*";
    private static final String PATTERN_END = ".*)";
    private static final String[] GRADE_PATTERNS = {
            "(.*5[^0-9]*[aA].*)",
            "(.*5[^0-9]*[bB].*)",
            "(.*5[^0-9]*[cC].*)",
            "(.*5[^0-9]*[dD].*)",
            "(.*5[^0-9]*[eE].*)",
            "(.*6[^0-9]*[aA].*)",
            "(.*6[^0-9]*[bB].*)",
            "(.*6[^0-9]*[cC].*)",
            "(.*6[^0-9]*[dD].*)",
            "(.*6[^0-9]*[eE].*)",
            "(.*7[^0-9]*[aA].*)",
            "(.*7[^0-9]*[bB].*)",
            "(.*7[^0-9]*[cC].*)",
            "(.*7[^0-9]*[dD].*)",
            "(.*7[^0-9]*[eE].*)",
            "(.*8[^0-9]*[aA].*)",
            "(.*8[^0-9]*[bB].*)",
            "(.*8[^0-9]*[cC].*)",
            "(.*8[^0-9]*[dD].*)",
            "(.*8[^0-9]*[eE].*)",
            "(.*9[^0-9]*[aA].*)",
            "(.*9[^0-9]*[bB].*)",
            "(.*9[^0-9]*[cC].*)",
            "(.*9[^0-9]*[dD].*)",
            "(.*9[^0-9]*[eE].*)",
            "(.*10[^0-9]*[aA].*)",
            "(.*10[^0-9]*[bB].*)",
            "(.*10[^0-9]*[cC].*)",
            "(.*10[^0-9]*[dD].*)",
            "(.*10[^0-9]*[eE].*)",
            "(.*[kK].*1.*)",
            "(.*[kK].*2.*)"};

    private final String gradePattern;
    private final String coursePattern;

    public VPlanSorter(String grades, String courses) {
        gradePattern = generateGradePattern(grades);
        coursePattern = generateCoursePattern(courses);
    }

    /**
     * Filters the data, using the grade- and course pattern. Entities matching the patterns will
     * stay in the data set.
     * */
    public VPlanData filterData(VPlanData data) {
        for (int i = data.getVPlanRowCount(); i > 0; i--) {
            VPlanRow row = data.getVPlanRowAtPosition(i - 1);
            if (!row.getGrade().matches(gradePattern)) {
                data.deleteVPlanRow(i - 1);
            } else {
                if (isGradeCourse(row)) {
                    if (!row.getSubject().toLowerCase().matches(coursePattern)) {
                        data.deleteVPlanRow(i - 1);
                    }
                }
            }
        }
        return data;
    }

    /**
     * Returns if the grade pattern matches every string.
     * */
    public boolean matchesEverything() {
        return gradePattern.equals(PATTERN_MATCHES_EVERYTHING);
    }

    /**
     * True if the given row contains data relevant for the "Kursstufe".
     * */
    private boolean isGradeCourse(VPlanRow row) {
        return row.getGrade().matches("(.*[kK].*2.*)|(.*[kK].*1.*)");
    }

    /**
     * Generates the Regex pattern to match all grades specified in "grades".
     * */
    private String generateGradePattern(String grades) {
        StringBuilder patternBuilder = new StringBuilder();
        boolean first = true;
        if (grades.equals("")) {
            return PATTERN_MATCHES_EVERYTHING;
        }
        String[] gradeArray = grades.split(",");

        for (String pattern : GRADE_PATTERNS) {
            for (String temp : gradeArray) {
                if (temp.matches(pattern)) {
                    if (first) {
                        patternBuilder.append(pattern);
                        first = false;
                    } else {
                        patternBuilder.append("|");
                        patternBuilder.append(pattern);
                    }
                }
            }
        }
        String result = patternBuilder.toString();

        if (result.equals("")) {
            return PATTERN_MATCHES_EVERYTHING;
        } else {
            return result;
        }
    }

    /**
     * Generates the Regex pattern to match all courses specified in "courses".
     * */
    private String generateCoursePattern(String courses) {
        StringBuilder patternBuilder = new StringBuilder();
        boolean first = true;
        if (courses.equals("")) {
            return PATTERN_MATCHES_EVERYTHING;
        }
        String[] courseArray = courses.split(",");
        for (String course : courseArray) {
            if (first) {
                first = false;
            } else {
                patternBuilder.append("|");
            }
            patternBuilder.append(PATTERN_START);
            patternBuilder.append(course.toLowerCase().trim());
            patternBuilder.append(PATTERN_END);
        }

        String result = patternBuilder.toString();

        if (result.equals("")) {
            return PATTERN_MATCHES_EVERYTHING;
        } else {
            return result;
        }
    }
}
