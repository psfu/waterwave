package waterwave.net;

import shui.common.buffer.CommonBuffer;

public interface Dealer {
	void onConnect(Channel channel);

	void beforeRead(Channel channel);

	void onData(Channel channel, CommonBuffer b, int bytes);

	void afterWrite(Channel channel, CommonBuffer b, int bytes);

	void onError(Channel channel, CommonBuffer b, Throwable exc);

	void onClose(Channel channel);
	
	boolean acceptsMessages();
}
