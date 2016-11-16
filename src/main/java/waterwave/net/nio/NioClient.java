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

<<<<<<< HEAD
package waterwave.net.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import waterwave.net.nio.define.NioDataDealerFactory;

public class NioClient extends NioService {
	
	final static int TIMEOUT = 3000 * 1000;
	final static int SO_RCVBUF = 32 * 1024;
	final static int SO_SNDBUF = 32 * 1024;

	DispatcherReader d;
	
	protected NioDataDealerFactory nioDataDealerFactory;
	
	public NioClient(ExecutorService es, NioDataDealerFactory nioDataDealerFactory) throws IOException {
		// ExecutorService channelWorkers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		this.nioDataDealerFactory = nioDataDealerFactory;
	}
	
	public NioClientChannel createConnect(InetAddress ip, int port) throws IOException {
		SocketChannel sc = connect0(ip, port);

		NioClientChannel c = new NioClientChannel(sc);
		
		return c;
	}

	public NioClientChannel connect(NioClientChannel nc) throws IOException {
		SocketChannel sc = nc.channel;
		
		NioClientChannel c = new NioClientChannel(sc);
		// log.log(1 "BioClient: connect finish", s);

		d.register(sc, SelectionKey.OP_CONNECT, c);

		return c;
	}

	private SocketChannel connect0(InetAddress ip, int port) throws IOException {
		// TODO Auto-generated method stub

		SocketChannel s = SocketChannel.open();
		s.configureBlocking(false);

		s.connect(new InetSocketAddress(ip, port));

		return s;
	}

	public static class DispatcherReader extends Dispatcher {
		protected Selector s;

		long reactCount;

		
		private Object gate = new Object();

		public void register(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			synchronized (gate) {
				s.wakeup();
				sc.register(s, ops, o);
			}
		}

		private void registerNosync(SocketChannel sc, int ops, Object o) throws ClosedChannelException {
			sc.register(s, ops, o);
		}
		
		private void read(NioClientChannel cc) {
			
			cc.read();
		}
		
		private void write(NioServerChannel cc) {
			cc.write();
		}

		private void connect(NioClientChannel attr) throws IOException {
			SocketChannel sc = attr.channel;
			//
			sc.finishConnect();
			
			setSocket(sc);
			
			registerNosync(sc, SelectionKey.OP_READ, attr);

		}

		@Override
		public void run() {
			final Selector selector = this.s;
			for (;;) {
				// ++reactCount;
				try {
					dispatch(selector);
				} catch (Throwable e) {
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

							if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
								connect((NioClientChannel) attr);
							} else if ((readyOps & SelectionKey.OP_READ) != 0) {
								read((NioClientChannel) attr);
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

				//
				synchronized (gate) {
				}

			} catch (Throwable e) {
				log.log(7, "Reader:", e);
			}

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
