package de.jbamberger.fhgapp.ui.vplan

import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanRow
import de.jbamberger.fhgapp.Settings

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

object VPlanUtils {

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
    private val IS_COURSE_PATTERN = "(.*[kK].*2.*)|(.*[kK].*1.*)".toRegex()

    fun filter(plan: VPlan, matcher: (VPlanRow) -> Boolean): VPlan {
        return VPlan.Builder()
                .addDay1(filter(plan.day1, matcher))
                .addDay2(filter(plan.day2, matcher))
                .build()
    }

    private fun filter(day: VPlanDay, matcher: (VPlanRow) -> Boolean): VPlanDay {
        return VPlanDay(day.header, day.vPlanRows.filter(matcher))
    }

    fun getVPlanMatcher(settings: Settings.VPlanSettings): (VPlanRow) -> Boolean {
        if (settings.showAll) {
            return { true }
        } else {
            if (settings.grades.isEmpty()) {
                return { true }
            }
            val gradeMatcher = getGradeMatcher(settings.grades)
            if (settings.courses.isEmpty()) {
                return gradeMatcher
            }
            val courseMatcher = getCourseMatcher(settings.courses)
            return { gradeMatcher(it) && courseMatcher(it) }
        }
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

    private fun getCourseMatcher(courses: Set<String>): (VPlanRow) -> Boolean {
        val patternBuilder = StringBuilder()

        courses.forEachIndexed { index, course ->
            run {
                if (index > 0) {
                    patternBuilder.append("|")
                }
                patternBuilder.append(PATTERN_START)
                patternBuilder.append(course.toLowerCase().trim { it <= ' ' })
                patternBuilder.append(PATTERN_END)
            }
        }
        val pattern = patternBuilder.toString().toRegex()

        return {
            it.grade.matches(IS_COURSE_PATTERN) && it.subject.toLowerCase().matches(pattern)
        }
    }
}
