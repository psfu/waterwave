package waterwave.net;

public interface DealerClient {
	void ClientOnConnect(ChannelNIO channel);

	void ClientBeforeRead(ChannelNIO channel);

	void ClientOnData(ChannelNIO channel, int bytes);

	void ClientAfterWrite(ChannelNIO channel, int bytes);

	void ClientOnError(ChannelNIO channel, Throwable exc);

	void ClientOnClose(ChannelNIO channel);
}
