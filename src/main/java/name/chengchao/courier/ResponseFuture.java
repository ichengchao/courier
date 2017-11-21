package name.chengchao.courier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月21日
 */
public class ResponseFuture {

    private Message response;
    private CountDownLatch syncLockLatch = new CountDownLatch(1);
    private AtomicBoolean finish = new AtomicBoolean(false);
    private ResponseCallback responseCallback;
    private Integer timeoutMS;
    private Integer sequence;

    private boolean sendDone = false;
    private Throwable cause = null;

    public ResponseFuture(Integer sequence, ResponseCallback responseCallback, Integer timeoutMS) {
        super();
        this.responseCallback = responseCallback;
        this.timeoutMS = timeoutMS;
        this.sequence = sequence;
    }

    public void responseDone(Message response) {
        this.response = response;
        syncLockLatch.countDown();
    }

    private void doTimeout(ExecutorService executorService, ConcurrentHashMap<Integer, ResponseFuture> callbackMap) {
        if (callbackMap.get(this.sequence) == null) {
            return;
        }
        cause = new TimeoutException();
        doCallback(executorService, callbackMap);
    }

    public void invokeTimeoutCount(ScheduledExecutorService scheduledExecutorService, ExecutorService executorService,
        ConcurrentHashMap<Integer, ResponseFuture> callbackMap) {
        final int tmpSequence = this.sequence;
        scheduledExecutorService.schedule(new Runnable() {

            @Override
            public void run() {
                ResponseFuture responseFuture = callbackMap.get(tmpSequence);
                if (null != responseFuture) {
                    responseFuture.doTimeout(executorService, callbackMap);
                }
                // System.out.println(new Date() + "____" + sequence);
            }
        }, timeoutMS, TimeUnit.MILLISECONDS);
    }

    public void doCallback(ExecutorService executorService, ConcurrentHashMap<Integer, ResponseFuture> callbackMap) {
        final ResponseFuture myThis = this;
        boolean getLock = finish.compareAndSet(false, true);
        if (!getLock) {
            return;
        }
        callbackMap.remove(this.sequence);
        if (null == responseCallback) {
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                responseCallback.onComplete(myThis);
            }
        });
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setSendDone(boolean sendDone) {
        this.sendDone = sendDone;
    }

    public boolean isSendDone() {
        return sendDone;
    }

    public CountDownLatch getSyncLockLatch() {
        return syncLockLatch;
    }

}
