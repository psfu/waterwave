package waterwave.net.nio;

import java.nio.channels.SocketChannel;

import waterwave.net.nio.define.NioServerDataDealer;

public class NioServerChannel {
	
	private SocketChannel channel;
	
	private NioServerDataDealer dealer;
	
	public NioServerChannel(SocketChannel channel) {
		super();
		this.channel = channel;
	}


	public void read() {
		// TODO Auto-generated method stub
		
	}
	
	public void write() {
		// TODO Auto-generated method stub
		
	}


	public SocketChannel getChannel() {
		return channel;
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
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







	public void close() {
		// TODO Auto-generated method stub
		
	}

}
