package waterwave.net.aio.define;

import java.nio.ByteBuffer;

public interface AioChannelI {

	public void write(final ByteBuffer input);

	public void close();

}
