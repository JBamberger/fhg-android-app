package de.jbamberger.fhgapp.ui.vplan

import de.jbamberger.api.data.VPlan
import de.jbamberger.api.data.VPlanDay
import de.jbamberger.api.data.VPlanRow
import timber.log.Timber

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

object VPlanUtils {

    interface VPlanMatcher {
        fun matches(row: VPlanRow): Boolean;
    }

    fun filter(plan: VPlan, matcher: (VPlanRow) -> Boolean): VPlan {
        return VPlan.Builder()
                .addDay1(filter(plan.day1, matcher))
                .addDay2(filter(plan.day2, matcher))
                .build()
    }

    private fun filter(day: VPlanDay, matcher: (VPlanRow) -> Boolean): VPlanDay {
        return VPlanDay(day.dateAndDay, day.lastUpdated, day.motd,
                day.vPlanRows.filter(matcher))
    }

    private val PATTERN_MATCHES_EVERYTHING = ".*"
    private val PATTERN_START = "(.*"
    private val PATTERN_END = ".*)"
    private val GRADE_PATTERNS = arrayOf(
            "(.*5[^0-9]*[aA].*)", "(.*5[^0-9]*[bB].*)", "(.*5[^0-9]*[cC].*)", "(.*5[^0-9]*[dD].*)", "(.*5[^0-9]*[eE].*)",
            "(.*6[^0-9]*[aA].*)", "(.*6[^0-9]*[bB].*)", "(.*6[^0-9]*[cC].*)", "(.*6[^0-9]*[dD].*)", "(.*6[^0-9]*[eE].*)",
            "(.*7[^0-9]*[aA].*)", "(.*7[^0-9]*[bB].*)", "(.*7[^0-9]*[cC].*)", "(.*7[^0-9]*[dD].*)", "(.*7[^0-9]*[eE].*)",
            "(.*8[^0-9]*[aA].*)", "(.*8[^0-9]*[bB].*)", "(.*8[^0-9]*[cC].*)", "(.*8[^0-9]*[dD].*)", "(.*8[^0-9]*[eE].*)",
            "(.*9[^0-9]*[aA].*)", "(.*9[^0-9]*[bB].*)", "(.*9[^0-9]*[cC].*)", "(.*9[^0-9]*[dD].*)", "(.*9[^0-9]*[eE].*)",
            "(.*10[^0-9]*[aA].*)", "(.*10[^0-9]*[bB].*)", "(.*10[^0-9]*[cC].*)", "(.*10[^0-9]*[dD].*)", "(.*10[^0-9]*[eE].*)",
            "(.*[kK].*1.*)", "(.*[kK].*2.*)")

    fun matcherForSettings(grades: Set<String>, courses: String): (VPlanRow) -> Boolean {
        if (grades.isEmpty()) {
            return { true }
        }
        val gradeMatcher = getGradeMatcher(grades)
        if (courses.isBlank()) {
            return gradeMatcher
        }
        val courseMatcher = getCourseMatcher(courses)
        return { gradeMatcher(it) && courseMatcher(it) }
    }

    private fun getGradeMatcher(grades: Set<String>): (VPlanRow) -> Boolean {
        val patternBuilder = StringBuilder()

        grades.forEachIndexed { index, grade ->
            run {
                GRADE_PATTERNS.forEach {
                    if (grade.matches(it.toRegex())) {
                        if (index > 0) {
                            patternBuilder.append("|")
                        }
                        patternBuilder.append(it)
                    }
                }
            }
        }

        val pattern = patternBuilder.toString().toRegex()
        return { it.grade.matches(pattern) }
    }

    private fun getCourseMatcher(courses: String): (VPlanRow) -> Boolean {
        val patternBuilder = StringBuilder()
        val courseArray = courses.split(",".toRegex())
                .filter { !it.isBlank() }

        courseArray.forEachIndexed { index, course ->
            run {
                if (index > 0) {
                    patternBuilder.append("|")
                }
                patternBuilder.append(PATTERN_START)
                patternBuilder.append(course.toLowerCase().trim { it <= ' ' })
                patternBuilder.append(PATTERN_END)
            }
        }

        val isCoursePattern = "(.*[kK].*2.*)|(.*[kK].*1.*)".toRegex()
        val pattern = patternBuilder.toString().toRegex()

        Timber.d("Patterns /%s/   /%s/", isCoursePattern, pattern)

        return {
            it.grade.matches(isCoursePattern) && it.subject.toLowerCase().matches(pattern)
        }
    }
}
