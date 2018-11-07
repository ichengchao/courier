package name.chengchao.courier.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import name.chengchao.courier.ResponseFuture;

public class ContextHolder {

    public static final int IDLE_TIMEOUT_SECONDS = 20;

    public static final long CONNECT_TIMEOUT_MILLIS = 5000;

    public static final long LOCK_CREATE_TIMEOUT_MILLIS = 5000;

    public static ConcurrentHashMap<Integer, ResponseFuture> callbackMap = new ConcurrentHashMap<>();

    public static ExecutorService callBackExecutorService = Executors.newFixedThreadPool(4);

    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

}
