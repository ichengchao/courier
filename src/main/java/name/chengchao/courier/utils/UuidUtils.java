package name.chengchao.courier.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author charles
 * @date 2017年11月21日
 */
public class UuidUtils {

    private static AtomicInteger SequenceCounter = new AtomicInteger(0);

    public static int getSequence() {
        return SequenceCounter.getAndIncrement();
    }

}
