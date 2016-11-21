package waterwave.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import waterwave.common.buffer.BufferPoolNIO;
import waterwave.net.nio.define.NioServerDataDealer;

public class NioServerChannel {

	private SocketChannel sc;
	private final BufferPoolNIO bp;

	private NioServerDataDealer dealer;

	public NioServerChannel(SocketChannel channel,BufferPoolNIO bp) {
		super();
		this.sc = channel;
		this.bp = bp;
	}

	public void read() {
		ByteBuffer b = bp.allocate();
		
		try {
			int in = read(b);
			dealer.serverOnData(this, b,  in);
		} catch (IOException e) {
			bp.recycle(b);
			e.printStackTrace();
			close();
		}
		
	}
	
	public void close() {
		try {
			sc.close();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
		dealer.serverOnClose(this);
		
	}


	public void write() {
		// TODO Auto-generated method stub
		
	}
	
	public int read(ByteBuffer b) throws IOException {
		int r = sc.read(b);
		return r;
	}

	public int write(ByteBuffer b) throws IOException {
		int w = sc.write(b);
		//TODO
		//dealer.serverAfterWrite(this, b, w);
		return w;
	}

	public SocketChannel getChannel() {
		return sc;
	}

	public void setChannel(SocketChannel channel) {
		this.sc = channel;
	}

	public NioServerDataDealer getDealer() {
		return dealer;
	}

	public void setDealer(NioServerDataDealer dealer) {
		this.dealer = dealer;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
