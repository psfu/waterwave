package waterwave.net.nioSingle1;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import shuisea.common.buffer.BufferPoolSingleNIO;
import shuisea.common.buffer.CommonBuffer;
import waterwave.net.nioSingle1.define.NioSingleClientDataDealer;

public class NioSingleClientChannel extends NioSingleChannel {

	NioSingleClientDataDealer dealer;

	public NioSingleClientChannel(SocketChannel sc, BufferPoolSingleNIO bp, NioSingleClientDataDealer dealer) {
		super();
		this.sc = sc;
		this.bp = bp;
		this.dealer = dealer;
	}


	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	@Override
	public boolean close() {
		close0();
		dealer.clientOnClose(this);
		return true;
	}

	@Override
	public int write(CommonBuffer in) throws IOException {
		int r = write0(in.b);
		return r;
	}

	@Override
	public CommonBuffer read() {
		CommonBuffer b = bp.getBuffer();
		int r = read(b.b);
		dealer.clientOnData(this, b, r);
		return b;
	}

	@Override
	public String getId() {
		return "Channel Client";
	}
}
