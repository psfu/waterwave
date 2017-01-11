package waterwave.net.nioSingle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import shuisea.common.buffer.BufferPoolSingleNIO;
import waterwave.net.nioSingle.define.Handler;
import waterwave.net.nioSingle.define.NioSingleDataDealerFactory;
import waterwave.net.nioSingle.define.NioSingleServerDataDealer;

public class NioSingleServer extends NioSingleService {

	final static int TIMEOUT = 3000 * 1000;
	final static int SO_RCVBUF = 32 * 1024;
	final static int SO_SNDBUF = 32 * 1024;

	final static int BACKLOG = 2 * 1024;

	protected Selector s;

	protected int port;

	private ServerSocketChannel ssc;

	BufferPoolSingleNIO bp;

	Handler acceptHandler;

	protected NioSingleDataDealerFactory nioDataDealerFactory;

	public NioSingleServer(int port, BufferPoolSingleNIO bp, NioSingleDataDealerFactory nioDataDealerFactory) throws IOException {
		this.port = port;
		this.bp = bp;
		this.nioDataDealerFactory = nioDataDealerFactory;

		ssc = ServerSocketChannel.open();
		ssc.socket().setReuseAddress(true);
		ssc.socket().bind(new InetSocketAddress(port), BACKLOG);
		//
		ssc.configureBlocking(false);

		this.acceptHandler = new NioAcceptHandler(ssc, this);

		init();
	}

	public void init() throws IOException {
		s = Selector.open();
		this.register(ssc, SelectionKey.OP_ACCEPT, acceptHandler);
	}

	@Override
	public void run() {
		for (;;) {
			try {
				dispatch();
			} catch (IOException x) {
				x.printStackTrace();
			}
		}
	}

	private void dispatch() throws IOException {
		s.select();
		for (Iterator<SelectionKey> i = s.selectedKeys().iterator(); i.hasNext();) {
			SelectionKey sk = i.next();
			i.remove();
			Handler h = (Handler) sk.attachment();
			//
			//log.log(1, "handler->", h);
			
			h.handle(sk);
		}
	}

	public void register(SelectableChannel ch, int ops, Handler h) throws IOException {
		ch.register(s, ops, h);
	}

	private final void setSocket(SocketChannel channel) throws SocketException {
		Socket socket = channel.socket();
		socket.setReceiveBufferSize(SO_RCVBUF);
		socket.setSendBufferSize(SO_SNDBUF);
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);
	}

	//
	class NioAcceptHandler implements Handler {

		private ServerSocketChannel ssc;
		private NioSingleServer s;

		public NioAcceptHandler(ServerSocketChannel ssc, NioSingleServer s) {
			super();
			this.ssc = ssc;
			this.s = s;
		}

		@Override
		public void handle(SelectionKey sk) throws IOException {
			if (!sk.isAcceptable())
				return;

			SocketChannel sc = ssc.accept();

			if (sc == null) {
				return;
			}

			sc.configureBlocking(false);

			setSocket(sc);

			NioSingleServerDataDealer dealer = nioDataDealerFactory.getNioServerDataDealer();

			NioSingleServerChannel c = new NioSingleServerChannel(sc, bp, dealer);

			dealer.serverOnConnect(c);

			NioRequestHandler rh = new NioRequestHandler(c);

			s.register(sc, SelectionKey.OP_READ, rh);
		}

	}

	class NioRequestHandler implements Handler {

		NioSingleServerChannel c;

		public NioRequestHandler(NioSingleServerChannel c) {
			super();
			this.c = c;
		}

		@Override
		public void handle(SelectionKey sk) throws IOException {
			c.read();
		}

	}

}
