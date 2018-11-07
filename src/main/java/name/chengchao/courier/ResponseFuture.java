package name.chengchao.courier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.chengchao.courier.context.ContextHolder;
import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月21日
 */
public class ResponseFuture {

    private static Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private CountDownLatch syncLockLatch = new CountDownLatch(1);
    private AtomicBoolean finish = new AtomicBoolean(false);
    private ResponseCallback responseCallback;
    private Message response;
    private Integer timeoutMS;
    private Integer sequence;

    public ResponseFuture(Integer sequence, ResponseCallback responseCallback, Integer timeoutMS) {
        super();
        this.responseCallback = responseCallback;
        this.timeoutMS = timeoutMS;
        this.sequence = sequence;
    }

    // 异步超时
    public void invokeTimeoutCount() {
        final int tmpSequence = this.sequence;
        ContextHolder.scheduledExecutorService.schedule(new Runnable() {

            @Override
            public void run() {
                ResponseFuture responseFuture = ContextHolder.callbackMap.get(tmpSequence);
                if (null != responseFuture) {
                    doCallback(false, null, new TimeoutException());
                }
            }
        }, timeoutMS, TimeUnit.MILLISECONDS);
    }

    // 接收response消息
    public void receiveResponse(Message response) {
        this.response = response;
        doCallback(true, response, null);
        // 激活同步等待
        this.syncLockLatch.countDown();
    }

    // 获取同步结果
    public Message getSyncResult() {
        try {
            syncLockLatch.await(timeoutMS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return this.response;
    }

    public void doCallback(boolean success, Message response, Throwable cause) {
        // 防止重复结束
        boolean getLock = finish.compareAndSet(false, true);
        if (!getLock) {
            return;
        }
        ContextHolder.callbackMap.remove(this.sequence);
        if (null == responseCallback) {
            return;
        }
        ContextHolder.callBackExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                responseCallback.onComplete(success, response, cause);
            }
        });
    }

}
