package waterwave.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import shuisea.common.buffer.BufferPoolNIO;
import shuisea.common.service.ThreadSharedService;
import waterwave.net.nio.define.NioClientDataDealer;

public class NioClientChannel extends ThreadSharedService {

	protected SocketChannel sc;

	// just for proxy
	private NioServerChannel nioServerChannel;

	private Object attr;

	private NioClientDataDealer dealer;

	private final BufferPoolNIO bp;

	boolean isClosed = false;

	public NioClientChannel(SocketChannel channel, BufferPoolNIO bp,NioClientDataDealer dealer) {
		super();
		this.sc = channel;
		this.bp = bp;
		this.dealer = dealer;
	}

	public int read0(ByteBuffer dst) throws IOException {
		int r = sc.read(dst);
		return r;
	}

	public int write0(ByteBuffer src) throws IOException {
		int w = sc.write(src);
		bp.recycle(src);
		// TODO
		// dealer.clientAfterWrite(this, src, w);
		return w;
	}

	public int read(ByteBuffer b) {
		// ByteBuffer b = bp.allocate();
		int in = -1;
		try {
			in = read0(b);
			//log.log(1, "-------->c read:", in);
			if (in < 0) {
				//log.log(1, "-------->!!:", in);
				bp.recycle(b);
				close();
				dealer.clientOnClose(this);
			} else if (in ==0){
				//log.log(1, "-------->!!!:", in);
			}

		} catch (IOException e) {
			bp.recycle(b);
			e.printStackTrace();
			close();
		}
		return in;
	}

	public void read(ByteBuffer b, int in) {
		//log.log(1, "client call read.");
		dealer.clientOnData(this, b, in);
	}

	public void write(ByteBuffer b) {
		// TODO Auto-generated method stub
	}

	public void close() {
		close0();
		if (dealer != null) {
			dealer.clientOnClose(this);
		}
	}

	public void close0() {
		this.isClosed = true;
		try {
			log.log(2, "client channel close ing", sc);
			sc.close();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}

	public NioClientDataDealer getDealer() {
		return dealer;
	}

	public void setDealer(NioClientDataDealer dealer) {
		this.dealer = dealer;
	}

	public NioServerChannel getNioServerChannel() {
		return nioServerChannel;
	}

	public void setNioServerChannel(NioServerChannel nioServerChannel) {
		this.nioServerChannel = nioServerChannel;
	}

	public Object getAttr() {
		return attr;
	}

	public void setAttr(Object attr) {
		this.attr = attr;
	}

	public SocketChannel getChannel() {
		return sc;
	}

	public void setChannel(SocketChannel channel) {
		this.sc = channel;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isClosed() {
		return isClosed;
	}

}
