package waterwave.net;

import java.nio.ByteBuffer;

public interface ChannelNIO {
	public boolean close();
	public int write(final ByteBuffer in);
	public ByteBuffer read();
}
