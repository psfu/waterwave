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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import waterwave.net.aio.define.AioDataDealerFactory;
import waterwave.net.aio.define.AioServerDataDealer;


public final class AioServer extends AioService implements Runnable {

	// private final List<Client> connections = Collections.synchronizedList(new ArrayList<Client>());
	//private final List<AioServerChannel> connections = Collections.synchronizedList(new ArrayList<AioServerChannel>());
	private final Map<Integer, AioServerChannel> connections = new ConcurrentHashMap<Integer, AioServerChannel>();
	private int port;
	private final AsynchronousServerSocketChannel listener;
	private final AsynchronousChannelGroup channelGroup;
	private final AcceptHandler acceptHandler;
	private final AioDataDealerFactory aioDataDealerFactory;


	/**
	 * init the aioServer
	 * 
	 * @param port
	 * @param channelWorkers
	 * @throws IOException
	 */
	public AioServer(int port, ExecutorService  channelWorkers , AioDataDealerFactory aioDataDealerFactory) throws IOException {
		this.aioDataDealerFactory = aioDataDealerFactory;
		channelGroup = AsynchronousChannelGroup.withThreadPool(channelWorkers);
		this.port = port;
		listener = createListener(channelGroup);
		acceptHandler = new AcceptHandler();
	}

	/*
	 * Creates a listener and starts accepting connections
	 * Option Name	Description
	 * SO_RCVBUF	The size of the socket receive buffer
	 * SO_REUSEADDR	Re-use address
	 */
	private AsynchronousServerSocketChannel createListener(AsynchronousChannelGroup channelGroup) throws IOException {
		final AsynchronousServerSocketChannel listener = openChannel(channelGroup);
		listener.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		listener.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		listener.bind(new InetSocketAddress(port), 0);
		return listener;
	}
	
	private AsynchronousServerSocketChannel openChannel(AsynchronousChannelGroup channelGroup) throws IOException {
        return AsynchronousServerSocketChannel.open(channelGroup);
    }	
	
	/**
    *
    * @return The socket address that the server is bound to
    * @throws java.io.IOException if an I/O error occurs
    */
   public SocketAddress getSocketAddress() throws IOException {
       return listener.getLocalAddress();
   }	
   
   /**
    * Start accepting connections
    */
	@Override
	public void run() {
		// call accept to wait for connections, tell it to call our CompletionHandler when there
		// is a new incoming connection
		listener.accept(null, acceptHandler);
	}

	class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
		
		

		@Override
		public void completed(AsynchronousSocketChannel result, Void attachment) {
			// request a new accept and handle the incoming connection
			listener.accept(null, this);
			handleNewConnection(result);
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
		}

	}
   
   

   /**
    * Creates a new client and adds it to the list of connections.
    * Sets the clients handler to the initial state of NameReader
    *
    * @param channel the newly accepted channel
    */
	private void handleNewConnection(AsynchronousSocketChannel channel) {
		try {
			channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		} catch (IOException e) {
			// ignore
			//
			e.printStackTrace();
		}

		//new dealer and channel
		AioServerDataDealer dealer = null;
		
		dealer = aioDataDealerFactory.getAioServerDataDealer();
		
		int channelId = getChannelId();
		AioServerChannel aioChannel = new AioServerChannel(channelId, channel, dealer, this);
		connections.put(channelId, aioChannel);
		
		//start channel
		aioChannel.run(null);
	}

	/**
	 * 
	 */
	private AtomicInteger channelId = new AtomicInteger();
	private int getChannelId() {
		return channelId.incrementAndGet();
	}

	public void removeAioChannel(AioServerChannel aioServerChannel) {
       connections.remove(aioServerChannel.getChannelId());
	}
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int threadNum = Runtime.getRuntime().availableProcessors() / 2;
		if (threadNum == 0) {
			threadNum = 1;
		}

		ExecutorService es = Executors.newFixedThreadPool(threadNum, Executors.defaultThreadFactory());

		try {
			AioServer s = new AioServer(8001, es, null);
			s.run();

			Thread.sleep(100000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
