package de.fhg_radolfzell.android_app.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * @author Jannik
 * @version 28.07.2016.
 */
public class VPlan {

    @SerializedName("etag")
    public String etag;

    @SerializedName("motd")
    public String motd;

    @SerializedName("date_at")
    public String dateAt;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("entries")
    public VPlanEntry[] entries;

    @Override
    public String toString() {
        return etag + dateAt + updatedAt + Arrays.toString(entries);
    }

    static public class VPlanEntry {
        @SerializedName("subject")
        public String subject;

        @SerializedName("grade")
        public String grade;

        @SerializedName("hour")
        public String hour;

        @SerializedName("message")
        public String message;

        @SerializedName("room")
        public String room;

        @SerializedName("omit")
        public int omitted;
    }
}


//[
// {
// "etag":"\"522e2b-12d8-53899b7c75900\"",
// "date_at":"2016-07-27",
// "updated_at":"2016-07-28 02:28:27",
// "entries":[
//      {"subject":"E_4",
//       "grade":"K1",
//       "hour":"3 - 4",
//       "message":"",
//       "room":"",
//       "omit":1},
//      {"subject":"E","grade":"7b","hour":"1","message":"","room":"","omit":1}
//  ]
// }
//]