package de.jbamberger.fhgapp.ui.vplan

import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhg.repository.data.VPlanRow
import de.jbamberger.fhgapp.Settings
import java.util.*

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

object VPlanUtils {

    private const val PATTERN_START = "(.*"
    private const val PATTERN_END = ".*)"
    private val GRADE_PATTERNS = arrayOf(
            "(.*5\\D*[aA].*)", "(.*5\\D*[bB].*)", "(.*5\\D*[cC].*)", "(.*5\\D*[dD].*)", "(.*5\\D*[eE].*)",
            "(.*6\\D*[aA].*)", "(.*6\\D*[bB].*)", "(.*6\\D*[cC].*)", "(.*6\\D*[dD].*)", "(.*6\\D*[eE].*)",
            "(.*7\\D*[aA].*)", "(.*7\\D*[bB].*)", "(.*7\\D*[cC].*)", "(.*7\\D*[dD].*)", "(.*7\\D*[eE].*)",
            "(.*8\\D*[aA].*)", "(.*8\\D*[bB].*)", "(.*8\\D*[cC].*)", "(.*8\\D*[dD].*)", "(.*8\\D*[eE].*)",
            "(.*9\\D*[aA].*)", "(.*9\\D*[bB].*)", "(.*9\\D*[cC].*)", "(.*9\\D*[dD].*)", "(.*9\\D*[eE].*)",
            "(.*10\\D*[aA].*)", "(.*10\\D*[bB].*)", "(.*10\\D*[cC].*)", "(.*10\\D*[dD].*)", "(.*10\\D*[eE].*)",
            "(.*[kK].*1.*)", "(.*[kK].*2.*)")
    private val IS_COURSE_PATTERN = "(.*[kK].*2.*)|(.*[kK].*1.*)".toRegex()

    fun filter(plan: VPlan, matcher: (VPlanRow) -> Boolean): List<VPlanListItem> {
        val out = mutableListOf<VPlanListItem>()
        out.add(VPlanListItem.Header(plan.day1.header))
        out.addAll(plan.day1.vPlanRows.filter(matcher).map { VPlanListItem.Row(it) })
        out.add(VPlanListItem.Header(plan.day2.header))
        out.addAll(plan.day2.vPlanRows.filter(matcher).map { VPlanListItem.Row(it) })
        out.add(VPlanListItem.Footer)
        return out
    }

    fun getVPlanMatcher(settings: Settings.VPlanSettings): (VPlanRow) -> Boolean = when {
        settings.showAll || settings.grades.isEmpty() -> { _ -> true }
        settings.courses.isEmpty() -> getGradeMatcher(settings.grades)
        else -> { it -> getGradeMatcher(settings.grades)(it) && getCourseMatcher(settings.courses)(it) }
    }

    private fun getGradeMatcher(grades: Set<String>): (VPlanRow) -> Boolean {
        val pattern = grades
                .flatMap { grade -> GRADE_PATTERNS.filter { grade.matches(it.toRegex()) } }
                .joinToString("|")
                .toRegex()
        return { it.grade.matches(pattern) }
    }

    private fun getCourseMatcher(courses: Set<String>): (VPlanRow) -> Boolean {
        val pattern = courses.joinToString(
                separator = "$PATTERN_END|$PATTERN_START",
                prefix = PATTERN_START,
                postfix = PATTERN_END
        ) { course ->
            course.lowercase(Locale.ROOT).trim { it <= ' ' }
        }.toRegex(RegexOption.IGNORE_CASE)

        return { it.grade.matches(IS_COURSE_PATTERN) && it.subject.matches(pattern) }
    }
}
