package xyz.jbapps.vplan;

/**
 * Created by Jannik on 05.08.2015.
 */
public class VPlanException extends Exception {

    public VPlanException() {
        super();
    }

    public VPlanException(String detailMessage) {
        super(detailMessage);
    }

    public VPlanException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public VPlanException(Throwable throwable) {
        super(throwable);
    }


}
