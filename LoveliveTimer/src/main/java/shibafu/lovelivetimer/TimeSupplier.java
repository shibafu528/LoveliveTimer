package shibafu.lovelivetimer;

/**
 * Created by shibafu on 14/01/31.
 */
public interface TimeSupplier {
    long getTimerTime();
    int getRequestedLp();
}
