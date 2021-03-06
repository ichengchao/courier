package name.chengchao.courier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import name.chengchao.courier.context.ContextHolder;
import name.chengchao.courier.protocol.Message;

/**
 * 异常处理: <br>
 * <li>超时异常</li>
 * <li>写异常</li>
 * 
 * 
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
    private ChannelFuture sendFuture;

    public ResponseFuture(Integer sequence, ResponseCallback responseCallback, Integer timeoutMS) {
        super();
        this.responseCallback = responseCallback;
        this.timeoutMS = timeoutMS;
        this.sequence = sequence;
    }

    public void setSendFuture(ChannelFuture sendFuture) {
        this.sendFuture = sendFuture;
    }

    // 异步超时
    public void invokeTimeoutCount() {
        final int tmpSequence = this.sequence;
        ContextHolder.timer.newTimeout(new TimerTask() {

            @Override
            public void run(Timeout timeout) throws Exception {
                ResponseFuture responseFuture = ContextHolder.callbackMap.get(tmpSequence);
                if (null != responseFuture) {
                    doCallback(false, null, new TimeoutException());
                }

            }
        }, timeoutMS, TimeUnit.MILLISECONDS);
    }

    // 接收response消息
    // public void receiveResponse(Message response) {
    // this.response = response;
    // doCallback(true, response, null);
    // // 激活同步等待
    // this.syncLockLatch.countDown();
    // }

    // 获取同步结果
    public Message getSyncResult() {
        try {
            boolean result = syncLockLatch.await(timeoutMS, TimeUnit.MILLISECONDS);
            if (!result) {
                throw new RuntimeException("timeout(ms):" + timeoutMS);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        if (null != sendFuture && !sendFuture.isSuccess()) {
            throw new RuntimeException(sendFuture.cause());
        }
        return this.response;
    }

    public void doCallback(boolean success, Message response, Throwable cause) {
        // 防止重复结束
        boolean getLock = finish.compareAndSet(false, true);
        if (!getLock) {
            return;
        }

        this.response = response;
        // 激活同步等待
        this.syncLockLatch.countDown();

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
