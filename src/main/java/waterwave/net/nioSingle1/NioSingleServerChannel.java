package waterwave.net.nioSingle1;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import shui.common.buffer.BufferPoolSingleNIO;
import shui.common.buffer.CommonBuffer;
import waterwave.net.nioSingle1.define.NioSingleServerDataDealer;

public class NioSingleServerChannel extends NioSingleChannel {



	NioSingleServerDataDealer dealer;

	boolean isClosed = false;

	public NioSingleServerChannel(SocketChannel sc, BufferPoolSingleNIO bp, NioSingleServerDataDealer dealer) {
		super();
		this.sc = sc;
		this.bp = bp;
		this.dealer = dealer;
	}
	
	
	@Override
	public boolean close() {
		log.log(2, "server channel close ing", sc);
		close0();
		dealer.onClose(this);
		return false;
	}

	@Override
	public CommonBuffer read() {
		CommonBuffer b = bp.getBuffer();
		int r = read(b.b);
		dealer.onData(this, b, r);
		return b;
	}


	@Override
	public int write(CommonBuffer in) throws IOException {
		int r = write0(in.b);
		return r;
	}

	@Override
	public String getId() {
		return "server channel";
	}

	public NioSingleServerDataDealer getDealer() {
		return dealer;
	}

	public void setDealer(NioSingleServerDataDealer dealer) {
		this.dealer = dealer;
	}


}
