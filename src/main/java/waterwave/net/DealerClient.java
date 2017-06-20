package waterwave.net;

import shui.common.buffer.CommonBuffer;

public interface DealerClient {
	void clientOnConnect(Channel channel);

	void clientBeforeRead(Channel channel);

	void clientOnData(Channel channel,CommonBuffer b, int bytes);

	void clientAfterWrite(Channel channel,CommonBuffer b, int bytes);

	void clientOnError(Channel channel,CommonBuffer b, Throwable exc);

	void clientOnClose(Channel channel);
	
	boolean clientAcceptsMessages();
}
