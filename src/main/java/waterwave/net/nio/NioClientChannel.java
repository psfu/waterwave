package waterwave.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import waterwave.net.nio.define.NioClientDataDealer;

public class NioClientChannel {

	SocketChannel channel;

	// just for proxy
	private NioServerChannel nioServerChannel;

	private Object attr;

	private NioClientDataDealer dealer;

	public int read0(ByteBuffer dst) throws IOException {
		int r = channel.read(dst);
		return r;
	}

	public int write0(ByteBuffer src) throws IOException {
		int r = channel.write(src);
		return r;
	}

	public void read() {
		dealer.clientOnData(this);
	}

	public void write() {
		// TODO Auto-generated method stub
	}

	public NioClientDataDealer getDealer() {
		return dealer;
	}

	public void setDealer(NioClientDataDealer dealer) {
		this.dealer = dealer;
	}

	public NioClientChannel(SocketChannel channel) {
		super();
		this.channel = channel;
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
		return channel;
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub


	}

}
