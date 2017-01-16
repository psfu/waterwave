package waterwave.net;

import shuisea.common.buffer.CommonBuffer;

public interface DealerClient {
	void ClientOnConnect(Channel channel);

	void ClientBeforeRead(Channel channel);

	void ClientOnData(Channel channel,CommonBuffer b, int bytes);

	void ClientAfterWrite(Channel channel,CommonBuffer b, int bytes);

	void ClientOnError(Channel channel,CommonBuffer b, Throwable exc);

	void ClientOnClose(Channel channel);
}
