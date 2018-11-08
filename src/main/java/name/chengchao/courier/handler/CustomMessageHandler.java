package name.chengchao.courier.handler;

import name.chengchao.courier.protocol.Message;

public interface CustomMessageHandler {

    public Message handle(Message request);

}
