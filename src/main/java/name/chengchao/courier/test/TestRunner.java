package name.chengchao.courier.test;

public class TestRunner {

    public static void main(String[] args) throws Exception {
        if (null != args && args.length != 0 && "server".equals(args[0])) {
            TestPerfServer.start();
        } else {
            TestPerfClient.start();
        }
    }

}
