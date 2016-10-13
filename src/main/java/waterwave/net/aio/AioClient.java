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

package waterwave.net.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class AioClient extends AioService implements Runnable {

	// private final AsynchronousSocketChannel listener;
	private final AsynchronousChannelGroup channelGroup;
	private final AioDataDealerFactory aioDataDealerFactory;
	
	private final Map<Integer, AioServerChannel> connections = new ConcurrentHashMap<Integer, AioServerChannel>();
	

	public AioClient(ExecutorService channelWorkers, AioDataDealerFactory aioDataDealerFactory) throws IOException {
		// ExecutorService channelWorkers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		channelGroup = AsynchronousChannelGroup.withThreadPool(channelWorkers);
		this.aioDataDealerFactory = aioDataDealerFactory;
	}

	class AcceptHandler implements CompletionHandler<Void, Void> {
		private final AsynchronousSocketChannel listener;
		private final AioClientDataDealer aioClientDataDealer;

		public AcceptHandler(AsynchronousSocketChannel listener, AioClientDataDealer aioClientDataDealer) {
			super();
			this.listener = listener;
			this.aioClientDataDealer = aioClientDataDealer;
		}

		@Override
		public void completed(Void result, Void attachment) {
			//log.log(1, "Client AcceptHandler connected ");
			handleNewConnection(listener, aioClientDataDealer);
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			//log.log(1, "Client AcceptHandler failed result: " + exc);
		}

	}
	
	public AsynchronousSocketChannel connect(SocketAddress remote) throws IOException {
		return connect(remote, null);
	}
	public AsynchronousSocketChannel connect(SocketAddress remote, AioClientDataDealer aioClientDataDealer) throws IOException {
		AsynchronousSocketChannel listener = createListener(channelGroup);
		//log.log(1, "client start connect");

		AcceptHandler acceptHandler = new AcceptHandler(listener, aioClientDataDealer);

		listener.connect(remote, null, acceptHandler);
		return listener;
	}

	protected void handleNewConnection(AsynchronousSocketChannel channel, AioClientDataDealer aioClientDataDealer) {
		if (!channel.isOpen()) {
			//log.log(1, "handleNewConnection closed.. ");
			return;
		}
		
		AioClientDataDealer dealer = null;
		if (aioClientDataDealer != null) {
			dealer = aioClientDataDealer;
		} else {
			aioDataDealerFactory.getAioClientDataDealer();
		}

		int channelId = getChannelId();
		AioClientChannel aioChannel = new AioClientChannel(channelId, channel, dealer, this);
		
		// connections.add(aioChannel);
		aioChannel.run(null);
		
		dealer.clientOnConnect(aioChannel);

		// String w = "GET / HTTP/1.1 \n\n";
		// ByteBuffer buffer = ByteBuffer.wrap(w.getBytes());
		// System.out.println("set write ");
		// channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
		// @Override
		// public void completed(Integer result, ByteBuffer buffer) {
		// if (buffer.hasRemaining()) {
		// System.out.println("write... ");
		// channel.write(buffer, buffer, this);
		// } else {
		// // Go back and check if there is new data to write
		// // writeFromQueue();
		// System.out.println("write complete " + result);
		// }
		// }
		//
		// @Override
		// public void failed(Throwable exc, ByteBuffer attachment) {
		// }
		// });

	}

	/**
	 * 
	 * 
	 * @param channelGroup
	 * @return
	 * @throws IOException
	 * 
	 * 
	 *             SO_SNDBUF The size of the socket send buffer .
	 *             SO_RCVBUF The size of the socket receive buffer. 
	 *             SO_KEEPALIVE Keep connection alive. 
	 *             SO_REUSEADDR Re-use address .
	 *             TCP_NODELAY Disable the Nagle algorithm.
	 * 
	 * 
	 */
	private AsynchronousSocketChannel createListener(AsynchronousChannelGroup channelGroup) throws IOException {
		final AsynchronousSocketChannel listener = AsynchronousSocketChannel.open(channelGroup);
		//TODO
		//listener.setOption(StandardSocketOptions.TCP_NODELAY, true);
		listener.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		listener.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		listener.setOption(StandardSocketOptions.SO_SNDBUF, 16 * 1024);
		return listener;
	}

	//for test
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + "---run");

		InetSocketAddress r = new InetSocketAddress("10.213.33.176", 11200);
		try {
			AsynchronousSocketChannel channel = connect(r);
			System.out.println(channel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	private AtomicInteger channelId = new AtomicInteger();
	private int getChannelId() {
		return channelId.incrementAndGet();
	}

	public void removeAioChannel(AioClientChannel client) {
       connections.remove(client.getChannelId());
	}
	

	public static void main(String[] args) {
		try {
			int threadCap = 4;
			ExecutorService channelWorkers = Executors.newFixedThreadPool(threadCap, Executors.defaultThreadFactory());
			AioClient c = new AioClient(channelWorkers, null);
			c.run();

			Thread.sleep(100000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
