package waterwave.net.nioSingle1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import shui.common.buffer.BufferPoolSingleNIO;
import waterwave.net.nioSingle1.define.Handler;
import waterwave.net.nioSingle1.define.NioSingleClientDataDealer;
import waterwave.net.nioSingle1.define.NioSingleDataDealerFactory;

public class NioSingleClient {
	final static int TIMEOUT = 3000 * 1000;
	final static int SO_RCVBUF = 32 * 1024;
	final static int SO_SNDBUF = 32 * 1024;


	BufferPoolSingleNIO bp;

	NioSingleServer nss;

	protected NioSingleDataDealerFactory nioDataDealerFactory;

	

	public NioSingleClient(BufferPoolSingleNIO bp, NioSingleServer nss, NioSingleDataDealerFactory nioDataDealerFactory) {
		super();
		this.bp = bp;
		this.nss = nss;
		this.nioDataDealerFactory = nioDataDealerFactory;
	}

	private final static void setSocket(SocketChannel channel) throws SocketException {
		Socket socket = channel.socket();
		socket.setReceiveBufferSize(SO_RCVBUF);
		socket.setSendBufferSize(SO_SNDBUF);
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);
	}

	public NioSingleClientChannel connect(final NioSingleClientChannel nc) throws IOException {
		SocketChannel sc = nc.sc;

		// //log.log(1 "BioClient: connect finish", s);

		NioAcceptHandler ah = new NioAcceptHandler(nss, nc);
		nss.register(sc, SelectionKey.OP_CONNECT, ah);

		return nc;
	}

	class NioAcceptHandler implements Handler {

		private NioSingleServer s;

		NioSingleClientChannel cc;

		public NioAcceptHandler(NioSingleServer s, NioSingleClientChannel cc) {
			super();
			this.s = s;
			this.cc = cc;
		}

		private void connect(NioSingleClientChannel cc) throws IOException {
			SocketChannel sc = cc.sc;
			//
			sc.finishConnect();

			setSocket(sc);

			// registerNosync(sc, SelectionKey.OP_READ, attr);

			//NioSingleServerChannel c = new NioSingleServerChannel(sc, s);
			NioRequestHandler rh = new NioRequestHandler(cc);

			s.register(sc, SelectionKey.OP_READ, rh);

		}

		@Override
		public void handle(SelectionKey sk) throws IOException {

			connect(cc);
		}

	}

	class NioRequestHandler implements Handler {

		NioSingleClientChannel c;

		public NioRequestHandler(NioSingleClientChannel c) {
			super();
			this.c = c;
		}

		@Override
		public void handle(SelectionKey sk) throws IOException {
			c.read();
		}

	}

	// createConnect
	public NioSingleClientChannel createConnect(InetAddress ip, int port, NioSingleClientDataDealer dealer) throws IOException {
		SocketChannel sc = connect0(ip, port);

		if (dealer == null) {
			dealer = nioDataDealerFactory.getNioClientDataDealer();
		}
		NioSingleClientChannel c = new NioSingleClientChannel(sc, bp, dealer);

		return c;
	}

	// createConnect
	private SocketChannel connect0(InetAddress ip, int port) throws IOException {

		SocketChannel s = SocketChannel.open();
		s.configureBlocking(false);

		s.connect(new InetSocketAddress(ip, port));

		return s;
	}

}
