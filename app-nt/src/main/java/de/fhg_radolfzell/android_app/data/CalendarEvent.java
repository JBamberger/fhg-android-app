package de.fhg_radolfzell.android_app.data;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jannik
 * @version 13.08.2016.
 */
public class CalendarEvent {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("start_at")
    private LocalDateTime startDate;

    @SerializedName("end_at")
    private LocalDateTime endDate;

    @SerializedName("url")
    private String url;

    @SerializedName("categories")
    private String categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate == null ? null : startDate.toString(DATE_PATTERN);
    }

    public void setStartDate(String startDate) {
        this.startDate = LocalDateTime.parse(startDate);
    }

    public LocalDateTime getStartDateObject() {
        return startDate;
    }

    public String getEndDate() {
        return endDate == null ? null : endDate.toString(DATE_PATTERN);
    }

    public void setEndDate(String endDate) {
        this.endDate = LocalDateTime.parse(endDate);
    }

    public LocalDateTime getEndDateObject() {
        return endDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategories() {
        return categories;
    }

    public List<String> getCategoriesList() {
        if (categories == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(categories.split(" |\t|\n"));
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
