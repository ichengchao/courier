package name.chengchao.courier.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import name.chengchao.courier.ResponseFuture;
import name.chengchao.courier.utils.RemotingThreadFactory;

public class ContextHolder {

    public static final int IDLE_TIMEOUT_SECONDS = 20;

    public static final long CONNECT_TIMEOUT_MILLIS = 5000;

    public static final long LOCK_CREATE_TIMEOUT_MILLIS = 5000;

    public static final int CALLBACK_EXECUTOR_THREAD_COUNT = 4;

    public static ConcurrentHashMap<Integer, ResponseFuture> callbackMap = new ConcurrentHashMap<>();

    // 回调执行线程池
    public static ExecutorService callBackExecutorService =
        Executors.newFixedThreadPool(CALLBACK_EXECUTOR_THREAD_COUNT, new RemotingThreadFactory("remoting-callback-"));

    public static final Timer timer = new HashedWheelTimer(new RemotingThreadFactory("remoting-timer-"));

}
