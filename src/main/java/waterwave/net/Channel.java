package waterwave.net;

import shuisea.common.buffer.CommonBuffer;

public interface Channel {
	public boolean close();
	public int write(final CommonBuffer in);
	public CommonBuffer read();
}
