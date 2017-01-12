package waterwave.net;

public interface Dealer {
	void onConnect(ChannelNIO channel);

	void beforeRead(ChannelNIO channel);

	void onData(ChannelNIO channel, int bytes);

	void afterWrite(ChannelNIO channel, int bytes);

	void onError(ChannelNIO channel, Throwable exc);

	void onClose(ChannelNIO channel);
}
