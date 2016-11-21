package waterwave.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import waterwave.common.buffer.BufferPoolNIO;
import waterwave.net.nio.define.NioClientDataDealer;

public class NioClientChannel {

	SocketChannel sc;

	// just for proxy
	private NioServerChannel nioServerChannel;

	private Object attr;

	private NioClientDataDealer dealer;
	
	private final BufferPoolNIO bp;
	
	public NioClientChannel(SocketChannel channel, BufferPoolNIO bp) {
		super();
		this.sc = channel;
		this.bp = bp;
	}

	public int read(ByteBuffer dst) throws IOException {
		int r = sc.read(dst);
		return r;
	}

	public int write(ByteBuffer src) throws IOException {
		int w = sc.write(src);
		//TODO
		//dealer.clientAfterWrite(this, src, w);
		return w;
	}
	
	

	public void read() {
		ByteBuffer b = bp.allocate();
		try {
			int in = read(b);
			dealer.clientOnData(this, b,  in);
		} catch (IOException e) {
			bp.recycle(b);
			e.printStackTrace();
			close();
		}
	}


	public void write() {
		// TODO Auto-generated method stub
	}
	
	public void close() {
		try {
			sc.close();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
		dealer.clientOnClose(this);

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

	
	

}
