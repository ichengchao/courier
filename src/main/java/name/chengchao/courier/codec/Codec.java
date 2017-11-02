package name.chengchao.courier.codec;

import java.io.IOException;

/**
 * @author charles
 * @date 2017年11月2日
 */
public interface Codec {

    void encode() throws IOException;

    Object decode() throws IOException;

}
