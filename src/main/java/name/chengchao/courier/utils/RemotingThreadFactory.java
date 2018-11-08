package name.chengchao.courier.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RemotingThreadFactory implements ThreadFactory {

    private final AtomicInteger nextId = new AtomicInteger();
    private String prefix;

    public RemotingThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(prefix + nextId.getAndIncrement());
        return t;
    }

}
