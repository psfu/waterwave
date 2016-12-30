package waterwave.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import shuisea.common.buffer.BufferPoolNIO;
import shuisea.common.service.ThreadSharedService;
import waterwave.net.nio.define.NioServerDataDealer;

public class NioServerChannel extends ThreadSharedService {

	private SocketChannel sc;
	private final BufferPoolNIO bp;

	NioServerDataDealer dealer;
	
	boolean isClosed = false;
	
	

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public NioServerChannel(SocketChannel channel,BufferPoolNIO bp) {
		super();
		this.sc = channel;
		this.bp = bp;
	}

	public int read(ByteBuffer b) {
		//ByteBuffer b = bp.allocate();
		
		int in  = -1;
		try {
			in = read0(b);
			//log.log(1, "-------->c read:", in);
			if (in < 0) {
				//log.log(1, "-------->!!:", in);
				bp.recycle(b);
				close();
				dealer.serverOnClose(this);
			} else if (in ==0){
				//log.log(1, "-------->!!!:", in);
			}

			//dealer.serverOnData(this, b,  in);
			
		} catch (IOException e) {
			bp.recycle(b);
			e.printStackTrace();
			close();
		}
		return in;
	}
	
	public void read(ByteBuffer b, int in) {
		dealer.serverOnData(this, b,  in);
	}
	
	public void close() {
		close0();
		dealer.serverOnClose(this);
		
	}
	
	public void close0() {
		this.isClosed = true;
		try {
			log.log(2,"server channel close ing",sc);
			sc.close();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}


	public void write() {
		// TODO Auto-generated method stub
		
	}
	
	public int read0(ByteBuffer b) throws IOException {
		int r = sc.read(b);
		return r;
	}

	public int write0(ByteBuffer b) throws IOException {
		int w = sc.write(b);
		bp.recycle(b);
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
