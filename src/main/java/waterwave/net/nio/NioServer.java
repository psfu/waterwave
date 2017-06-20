/*
 * Licensed to waterwave under one or more contributor
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package waterwave.net.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import shui.common.buffer.BufferPoolNIO;
import waterwave.net.nio.define.NioDataDealerFactory;
import waterwave.net.nio.define.NioServerDataDealer;

public class NioServer extends NioService {
	final static int TIMEOUT = 3000 * 1000;
	final static int SO_RCVBUF = 32 * 1024;
	final static int SO_SNDBUF = 32 * 1024;

	final static int BACKLOG = 2 * 1024;

	protected int port;
	// private ServerSocket ss;
	protected boolean secure;
	protected boolean couple;
	protected ExecutorService es;

	private SSLContext sslContext = null;
	private ServerSocketChannel ssc;

	private NioDataDealerFactory nioDataDealerFactory;

	private BufferPoolNIO bp;

	public NioServer(int port, ExecutorService es, boolean secure, boolean couple, BufferPoolNIO bp, NioDataDealerFactory nioDataDealerFactory)
			throws IOException {
		this.port = port;
		this.es = es;
		this.secure = secure;
		this.couple = couple;
		this.nioDataDealerFactory = nioDataDealerFactory;
		this.bp = bp;
		
		if (secure) {
			try {
				createSSLContext();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ssc = ServerSocketChannel.open();
		ssc.socket().setReuseAddress(true);
		ssc.socket().bind(new InetSocketAddress(port), BACKLOG);
		//
		ssc.configureBlocking(false);

		if (couple) {
			DispatcherCouple d = new DispatcherCouple(ssc, es, nioDataDealerFactory);
			d.start();
		} else {

			DispatcherSingle d = new DispatcherSingle(ssc, es, nioDataDealerFactory);
			d.start();
		}

	}

	final class AcceptorSingle {
		private final NioDataDealerFactory nioDataDealerFactory;

		public AcceptorSingle(ServerSocketChannel ssc, DispatcherSingle d, ExecutorService es, NioDataDealerFactory nioDataDealerFactory) {
			super();
			this.nioDataDealerFactory = nioDataDealerFactory;
		}

		public final void accept(SelectionKey sk, DispatcherSingle d) throws IOException {
			if (!sk.isAcceptable()) {
				return;
			}

			SocketChannel sc = d.ssc.accept();
			if (sc == null) {
				return;
			}

			sc.configureBlocking(false);

			setSocket(sc);

			//
			// ChannelIO cio = (sslContext != null ?
			// ChannelIOSecure.getInstance(
			// sc, false /* non-blocking */, sslContext) :
			// ChannelIO.getInstance(
			// sc, false /* non-blocking */));
			//
			// RequestHandler rh = new RequestHandler(cio);

			//
			NioServerChannel nsc = new NioServerChannel(sc, bp);

			onConnect(nsc);

			d.register(sc, SelectionKey.OP_READ, nsc);

		}

		private void onConnect(NioServerChannel nsc) {

			NioServerDataDealer dealer = nioDataDealerFactory.getNioServerDataDealer();

			nsc.dealer = dealer;
			dealer.serverOnConnect(nsc);

		}

		private final void setSocket(SocketChannel channel) throws SocketException {
			Socket socket = channel.socket();
			socket.setReceiveBufferSize(SO_RCVBUF);
			socket.setSendBufferSize(SO_SNDBUF);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
		}

	}

	final class DispatcherSingle extends Dispatcher implements Runnable {
		protected ServerSocketChannel ssc;
		protected Selector s;
		private AcceptorSingle a;


		public DispatcherSingle(ServerSocketChannel ssc, ExecutorService es, NioDataDealerFactory nioDataDealerFactory) throws IOException {
			
			this.setName("serverDisp");
			
			this.ssc = ssc;
			s = Selector.open();


			ssc.register(s, SelectionKey.OP_ACCEPT);
			AcceptorSingle a = new AcceptorSingle(ssc, this, es, nioDataDealerFactory);
			this.a = a;

		}

		@Override
		public void run() {
			log.log(5, "Server DispatcherSingle start");
			final Selector selector = this.s;
			for (;;) {
				// ++reactCount;
				try {
					dispatch(selector);
				} catch (Throwable e) {
					e.printStackTrace();
					log.log(7, "Reader:", e);
				}
			}

		}

		private final void dispatch(final Selector selector) throws IOException {
			// selector.select(20000L);
			selector.select();
			////log.log(1,"--->server selector:" + selector.keys());
			// register(selector);
			Set<SelectionKey> keys = selector.selectedKeys();
			//log.log(1,"--->server keys:" + keys);
			
			//TODO
			//showKeys(keys);
			try {
				Iterator<SelectionKey> it = keys.iterator();

				for (; it.hasNext();) {
					SelectionKey key = it.next();
					it.remove();

					Object attr = key.attachment();

					// TODO
					// //log.log(1,"--->att:"+att);

					if (key.isValid()) {
						int readyOps = key.readyOps();
						if ((readyOps & SelectionKey.OP_ACCEPT) != 0) {
							// keys.remove(key);
							a.accept(key, this);
						} else if (attr != null && (readyOps & SelectionKey.OP_READ) != 0) {
							// keys.remove(key);
							read((NioServerChannel) attr);
						} else if (attr != null && (readyOps & SelectionKey.OP_WRITE) != 0) {
							// keys.remove(key);
							write((NioServerChannel) attr);
						} else {
							key.cancel();
						}
					} else {
						key.cancel();
					}
				}
			} finally {
				keys.clear();
			}
		}

		private void read(NioServerChannel nsc) {
			ByteBuffer b = bp.allocate();
			int in = nsc.read(b);
			//
			if(in >0){
				es.submit(new ReadHandler(nsc, b, in));
			}
		}

		private void write(NioServerChannel nsc) {
			//
			nsc.write();
		}

		public void register(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			sc.register(s, ops, o);
		}

	}

	final class ReadHandler extends NioHandler implements Runnable {

		private final NioServerChannel nsc;

		private final ByteBuffer b;
		private final int r;

		public ReadHandler(NioServerChannel nsc, ByteBuffer b, int r) {
			super();
			this.nsc = nsc;
			this.b = b;
			this.r = r;
		}

		@Override
		public void run() {
			//
			//log.log(1, "server read start");
			nsc.read(b, r);
		}

	}

	/**
	 * 
	 * 
	 *
	 */
	final class AcceptorCouple extends Dispatcher implements Runnable {
		private ServerSocketChannel ssc;

		private DispatcherCouple d;

		protected SSLContext sslContext;

		long acceptCount;

		private NioDataDealerFactory nioDataDealerFactory;

		AcceptorCouple(ServerSocketChannel ssc, DispatcherCouple d, SSLContext sslContext, NioDataDealerFactory nioDataDealerFactory) {
			this.ssc = ssc;
			this.d = d;
			this.sslContext = sslContext;
			this.nioDataDealerFactory = nioDataDealerFactory;
		}

		@Override
		public void run() {
			for (;;) {
				try {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);

					// ChannelIO cio = (sslContext != null ?
					// ChannelIOSecure.getInstance(
					// sc, false /* non-blocking */, sslContext) :
					// ChannelIO.getInstance(
					// sc, false /* non-blocking */));
					//
					// RequestHandler rh = new RequestHandler(cio);

					// d.register(cio.getSocketChannel(), SelectionKey.OP_READ, rh);

					// sc.configureBlocking(false);
					NioServerChannel nsc = new NioServerChannel(sc, bp);

					setSocket(sc);
					onConnect(nsc);

					d.register(sc, SelectionKey.OP_READ, nsc);

				} catch (IOException x) {
					x.printStackTrace();
					break;
				}
			}
		}

		private void onConnect(NioServerChannel nsc) {
			NioServerDataDealer dealer = null;

			dealer = nioDataDealerFactory.getNioServerDataDealer();

			dealer.serverOnConnect(nsc);

		}


		private void setSocket(SocketChannel channel) throws SocketException {
			Socket socket = channel.socket();
			socket.setReceiveBufferSize(SO_RCVBUF);
			socket.setSendBufferSize(SO_SNDBUF);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
		}

	}

	final class DispatcherCouple extends Dispatcher implements Runnable {

		long reactCount;

		protected final ServerSocketChannel ssc;
		private final Selector s;

		private AcceptorCouple a;

		private NioDataDealerFactory nioDataDealerFactory;

		public DispatcherCouple(ServerSocketChannel ssc, ExecutorService es, NioDataDealerFactory nioDataDealerFactory) throws IOException {
			super();
			this.ssc = ssc;
			this.nioDataDealerFactory = nioDataDealerFactory;

			s = Selector.open();
			ssc.register(s, SelectionKey.OP_ACCEPT);
			AcceptorCouple a = new AcceptorCouple(ssc, this, sslContext, nioDataDealerFactory);
			this.a = a;
			a.start();
		}

		private Object gate = new Object();

		public void register(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			synchronized (gate) {
				s.wakeup();
				sc.register(s, ops, o);
			}
		}

		@Override
		public void run() {
			final Selector selector = this.s;
			for (;;) {
				// ++reactCount;
				try {
					dispatch(selector);
				} catch (Throwable e) {
					e.printStackTrace();
					log.log(7, "Reader:", e);
				}
			}

		}

		private void dispatch(Selector selector) {
			++reactCount;
			try {
				selector.select(2000L);
				// System.out.println("--->selector:"+selector);
				// register(selector);
				Set<SelectionKey> keys = selector.selectedKeys();
				try {
					for (SelectionKey key : keys) {
						Object attr = key.attachment();

						// TODO
						// System.out.println("--->att:"+att);

						if (attr != null && key.isValid()) {
							int readyOps = key.readyOps();

							if ((readyOps & SelectionKey.OP_READ) != 0) {
								read((NioServerChannel) attr);
							} else if ((readyOps & SelectionKey.OP_WRITE) != 0) {
								write((NioServerChannel) attr);
							} else {
								key.cancel();
							}
						} else {
							key.cancel();
						}
					}
				} finally {
					keys.clear();
				}

				synchronized (gate) {
				}

			} catch (Throwable e) {
				e.printStackTrace();
				log.log(7, "Reader:", e);
			}

		}

		private void read(NioServerChannel nsc) {
			ByteBuffer b = bp.allocate();
			nsc.read(b);
		}

		private void write(NioServerChannel nsc) {
			nsc.write();
		}

	}

	/*
	 * If this is a secure server, we now setup the SSLContext we'll use for creating the SSLEngines throughout the lifetime of this process.
	 */
	private void createSSLContext() throws Exception {

		char[] passphrase = "passphrase".toCharArray();

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("testkeys"), passphrase);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
	}

	@Override
	public void init(Properties pp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTime() {
		// TODO Auto-generated method stub

	}

}
