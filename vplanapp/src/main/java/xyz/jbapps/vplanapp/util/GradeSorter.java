package xyz.jbapps.vplanapp.util;

import xyz.jbapps.vplanapp.data.VPlanData;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class GradeSorter {

    private static final String[] PATTERNS = {
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

    private String gradePattern;

    public GradeSorter(String grades) {
        gradePattern = generatePattern(grades);
    }

    public VPlanData applyPatternToData(VPlanData data) {
        for (int i = data.getVPlanRowCount(); i > 0; i--) {
            if (!data.getVPlanRowAtPosition(i - 1).getGrade().matches(gradePattern)) {
                data.deleteVPlanRow(i - 1);
            }
        }
        return data;
    }

    private String generatePattern(String grades) {
        StringBuilder patternBuilder = new StringBuilder();
        boolean first = true;
        if (grades.equals("")) {
            return ".*";
        }
        String[] gradeArray = grades.split(",");

        for (String pattern : PATTERNS) {
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
            return ".*";
        } else {
            return result;
        }
    }

}
