package waterwave.net.nioSingle1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import shuisea.common.buffer.BufferPoolSingleNIO;
import shuisea.common.buffer.CommonBuffer;
import shuisea.common.service.ThreadSharedService;
import waterwave.net.Channel;

public abstract class NioSingleChannel extends ThreadSharedService implements Channel {

	SocketChannel sc;

	BufferPoolSingleNIO bp;


	boolean isClosed = false;

	protected int read(ByteBuffer b) {
		// ByteBuffer b = bp.allocate();
		// ByteBuffer b = bp.getBuffer();

		int in = -1;
		try {
			in = read0(b);
			// log.log(1, getId(), "--------> read:", in);
			if (in < 0) {
				// log.log(1, getId(), "-------->!!:", in);
				bp.giveupBuffer(b);
				close();
			} else if (in == 0) {
				log.log(1, getId(), "-------->!!!:", in);
			}

			// dealer.clientOnData(this, b, in);

		} catch (IOException e) {
			bp.giveupBuffer(b);
			e.printStackTrace();
			close();
		}
		return in;
	}
	//
	// public void read(ByteBuffer b, int in) {
	// dealer.clientOnData(this, b, in);
	// }

	protected void close0() {
		this.isClosed = true;
		try {
			log.log(2, "client channel close ing", sc);
			sc.close();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}

	protected int read0(ByteBuffer b) throws IOException {
		int r = sc.read(b);
		return r;
	}

	protected int write0(ByteBuffer b) throws IOException {
		b.flip();
		int w = sc.write(b);
		if (b.hasRemaining()) {
			log.log(2, "b.hasRemaining()", b.remaining());
			while (b.hasRemaining()) {
				write0(b);
			}
		}
		// log.log(1, getId(), " write0", w);
		bp.giveupBuffer(b);
		// TODO
		// dealer.clientAfterWrite(this, b, w);
		return w;
	}

	public SocketChannel getChannel() {
		return sc;
	}

	public void setChannel(SocketChannel channel) {
		this.sc = channel;
	}

	public boolean isClosed() {
		return isClosed;
	}

	@Override
	abstract public boolean close();

	@Override
	abstract public int write(CommonBuffer in) throws IOException;

	@Override
	abstract public CommonBuffer read();
	
	abstract public String getId();

}
