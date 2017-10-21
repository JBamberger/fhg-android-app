package de.fhg_radolfzell.android_app.util;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class Preconditions {

    private Preconditions() {}

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
        return reference;
    }
}
