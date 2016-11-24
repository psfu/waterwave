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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import waterwave.common.buffer.BufferPoolNIO;
import waterwave.net.nio.define.NioClientDataDealer;
import waterwave.net.nio.define.NioDataDealerFactory;

public class NioClient extends NioService {

	final static int TIMEOUT = 3000 * 1000;
	final static int SO_RCVBUF = 32 * 1024;
	final static int SO_SNDBUF = 32 * 1024;

	DispatcherReader d;

	protected NioDataDealerFactory nioDataDealerFactory;
	
	private BufferPoolNIO bp;
	
	public NioClient(ExecutorService es, BufferPoolNIO bp, NioDataDealerFactory nioDataDealerFactory) throws IOException {
		// ExecutorService channelWorkers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		this.nioDataDealerFactory = nioDataDealerFactory;
		this.bp = bp;

		d = new DispatcherReader(es);
		d.start();

	}

	public NioClientChannel createConnect(InetAddress ip, int port, NioClientDataDealer dealer) throws IOException {
		SocketChannel sc = connect0(ip, port);

		if(dealer == null) {
			dealer = nioDataDealerFactory.getNioClientDataDealer();
		}
		NioClientChannel c = new NioClientChannel(sc, bp, dealer);

		return c;
	}

	public NioClientChannel connect(final NioClientChannel nc) throws IOException {
		SocketChannel sc = nc.sc;

		// //log.log(1 "BioClient: connect finish", s);

		d.register(sc, SelectionKey.OP_CONNECT, nc);

		return nc;
	}

	private SocketChannel connect0(InetAddress ip, int port) throws IOException {

		SocketChannel s = SocketChannel.open();
		s.configureBlocking(false);

		s.connect(new InetSocketAddress(ip, port));

		return s;
	}

	public class DispatcherReader extends Dispatcher {
		protected Selector s;

		long reactCount;
		private final ExecutorService es;

		public DispatcherReader(ExecutorService es) throws IOException {
			
			this.setName("clientDisp");
			
			this.es = es;
			s = Selector.open();
		}

		private Object gate = new Object();

		public void register(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			synchronized (gate) {
				//log.log(1, "wakeup!!");
				s.wakeup();
				sc.register(s, ops, o);
			}
		}

		private void registerNosync(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			sc.register(s, ops, o);
		}

		private void read(NioClientChannel cc) {
			ByteBuffer b = bp.allocate();
			int r = cc.read(b);
			//
			if(r > 0 ){
				es.submit(new ReadHandler(cc, b, r));
			}
		}

		private void write(NioClientChannel cc) {
			ByteBuffer b = bp.allocate();
			cc.write(b);
		}

		private void connect(NioClientChannel attr) throws IOException {
			SocketChannel sc = attr.sc;
			//
			sc.finishConnect();

			setSocket(sc);

			registerNosync(sc, SelectionKey.OP_READ, attr);

		}

		@Override
		public void run() {
			log.log(5, "Client DispatcherSingle start");
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
				// selector.select(20000L);
				selector.select();
				////log.log(1, "--->client selector:" + selector.keys());
				// register(selector);
				Set<SelectionKey> keys = selector.selectedKeys();
				//log.log(1, "--->client keys:" + keys);
				//TODO
				//showKeys(keys);
				try {
					Iterator<SelectionKey> it = keys.iterator();

					for (; it.hasNext();) {
						SelectionKey key = it.next();
						it.remove();

						Object conn = key.attachment();

						if (conn != null && key.isValid()) {
							// TODO
							//log.log(1, "--->conn:" + conn);

							int readyOps = key.readyOps();

							if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
								// keys.remove(key);
								connect((NioClientChannel) conn);
							} else if ((readyOps & SelectionKey.OP_READ) != 0) {
								// keys.remove(key);
								read((NioClientChannel) conn);
							} else if ((readyOps & SelectionKey.OP_WRITE) != 0) {
								// keys.remove(key);
								write((NioClientChannel) conn);
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

				//
				synchronized (gate) {
				}

			} catch (Throwable e) {
				e.printStackTrace();
				log.log(7, "Reader:", e);
			}

		}

	}

	final static class ReadHandler extends NioHandler implements Runnable {

		private final NioClientChannel nsc;

		private final ByteBuffer b;
		private final int r;

		public ReadHandler(NioClientChannel nsc, ByteBuffer b, int r) {
			super();
			this.nsc = nsc;
			this.b = b;
			this.r = r;
		}

		@Override
		public void run() {
			//
			//
			//log.log(1, "client read start");
			nsc.read(b, r);
		}

	}

	private final static void setSocket(SocketChannel channel) throws SocketException {
		Socket socket = channel.socket();
		socket.setReceiveBufferSize(SO_RCVBUF);
		socket.setSendBufferSize(SO_SNDBUF);
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);
	}

	public static void main(String[] args) {

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
