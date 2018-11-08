## courier
remoting message communication, base on netty.

## hello world

支持三种类型的调用

- tell: 单向发送消息 
- ask: 同步调用
- askAsync: 异步调用


下面是几种类型的示例代码:


####  tell

```java
public class TestTell {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888).serve();
        Thread.sleep(3000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, ("tell" + i).getBytes());
            client.tell(message, "127.0.0.1", 8888);
        }

    }

}

```

####  ask

```java

# ask
public class TestAskSync {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                MessageHead head = request.getHead();
                head.setReq(false);
                Message response = new Message(head, ("sync callback").getBytes());

                return response;
            }
        }).serve();

        Thread.sleep(3000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, ("askSync" + i).getBytes());
            System.out.println("client send(sync):" + message);
            Message response = client.ask(message, "127.0.0.1", 8888, 3000);
            System.out.println("client receive(sync):" + response);
        }
    }

}

```


####  ask async

```java
public class TestAskAsync {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                MessageHead head = request.getHead();
                head.setReq(false);
                Message response = new Message(head, ("Async callback").getBytes());
                return response;
            }
        }).serve();

        Thread.sleep(1000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, ("askAsync" + i).getBytes());
            System.out.println("client send(Async):" + message);
            client.askAsync(message, "127.0.0.1", 8888, 5000, new ResponseCallback() {

                @Override
                public void onComplete(boolean success, Message response, Throwable cause) {
                    if (success) {
                        System.out.println("client receive(Async):" + response);
                    } else {
                        System.err.println("error:" + cause);
                    }
                }
            });
        }

        Thread.sleep(100000);

    }

}

```