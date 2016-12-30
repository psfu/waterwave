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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;

import shuisea.common.buffer.BufferTools;
import waterwave.net.aio.define.AioServerDataDealer;

public final class AioServerChannel extends AioChannel{
	
	private final AsynchronousSocketChannel channel;
	private final AioServerDataDealer dealer;
	private final AioServer aioServer;
	private final int channelId;
    //private final Queue<ByteBuffer> queue = new LinkedList<ByteBuffer>();
	private final Queue<ByteBuffer> queue = null;
    
    private boolean writing = false;
	
    

	public int getChannelId() {
		return channelId;
	}


	public AioServerChannel(int channelId, AsynchronousSocketChannel channel, AioServerDataDealer dealer, AioServer aioServer) {
		dealer.serverOnConnect(this);
		this.channel = channel;
		this.dealer = dealer;
		this.aioServer = aioServer;
		this.channelId = channelId;
	}


	public void run(ReadHandler reader) {
		dealer.serverBeforeRead(this);
		if (reader == null) {
			reader = new ReadHandler(this);
		}
		read(reader);

	}
	
    /**
     * Runs a cycle of doing a beforeRead action and then enqueuing a new
     * read on the client. Handles closed channels and errors while reading.
     * If the client is still connected a new round of actions are called.
     */
	class ReadHandler implements CompletionHandler<Integer, ByteBuffer>{
		AioServerChannel serverChannel;
		
        public ReadHandler(AioServerChannel serverChannel) {
			super();
			this.serverChannel = serverChannel;
		}

		@Override
        public void completed(Integer result, ByteBuffer buffer) {
            // if result is negative or zero the connection has been closed or something gone wrong
            if (result < 1) {
            	//log.log(1, "server:ReadHandler result < 1 Closing connection to " + serverChannel);
            	
            	serverChannel.close();
            	dealer.serverOnClose(serverChannel);
            	
            } else {
            	dealer.serverOnData(serverChannel, buffer, result);
                // enqueue next round of actions
                serverChannel.run(this);
            }
        }

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			// TODO Auto-generated method stub
			serverChannel.close();
			
			dealer.serverOnError(serverChannel, exc, attachment);
		}
		
//		public void onData(AioServerChannel channel, ByteBuffer buffer, int bytes) {
//	        buffer.flip();
//	        // Just append the message on the buffer
//	        //AioServerChannel.appendMessage(new String(buffer.array(), 0, bytes));
//	    }
		
	}
	
    /**
     * Enqueue a read
     * @param completionHandler callback on completed read
     */
    public final void read(CompletionHandler<Integer, ? super ByteBuffer> completionHandler) {
        ByteBuffer input = BufferTools.getBuffer();
        if (!channel.isOpen()) {
            return;
        }
        channel.read(input, input, completionHandler);
    }
    

    
	class WriteHandler implements CompletionHandler<Integer, ByteBuffer>{
		AioServerChannel serverChannel;
		
        public WriteHandler(AioServerChannel serverChannel) {
			super();
			this.serverChannel = serverChannel;
		}

        @Override
		public void completed(Integer result, ByteBuffer buffer) {
			// if result is negative or zero the connection has been closed or something gone wrong
			//System.out.println("read success: ");
			if (buffer.hasRemaining()) {
				//log.log(1, "server write... ");
				channel.write(buffer, buffer, this);
			} else {
				// Go back and check if there is new data to write
				// writeFromQueue();
				//log.log(1, "server write complete " + result);
				
			}
			dealer.serverAfterWrite(serverChannel, buffer, result);
			BufferTools.returnBuffer(buffer);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer buffer) {
			log.log(9, "server WriteHandler write fail: " + exc);
			serverChannel.close();
		}
		
//		public void onData(AioServerChannel channel, ByteBuffer buffer, int bytes) {
//	        buffer.flip();
//	        // Just append the message on the buffer
//	        //AioServerChannel.appendMessage(new String(buffer.array(), 0, bytes));
//	    }
		
	}
    
    

    
    private CompletionHandler<Integer, ? super ByteBuffer> writerHandler = null;
    public final void write(final ByteBuffer input) {
    	if(this.writerHandler == null) {
    		writerHandler = new WriteHandler(this);
    	}
    	write(input, writerHandler);
    }
    
    /**
     * Enqueue a read
     * @param completionHandler callback on completed read
     */
    public final void write(ByteBuffer b ,CompletionHandler<Integer, ? super ByteBuffer> completionHandler) {
    	//log.log(1, "start server write ");
    	
        if (!channel.isOpen()) {
            return;
        }
        channel.write(b, b, completionHandler);
    }
    


	
	/**
     * Closes the channel
     */
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        aioServer.removeAioChannel(this);
    }


    
    /**
     * Enqueues a write of the buffer to the channel.
     * The call is asynchronous so the buffer is not safe to modify after
     * passing the buffer here.
     *
     * @param buffer the buffer to send to the channel
     */
    @Deprecated
    public void writeOld(final ByteBuffer buffer) {
        boolean threadShouldWrite = false;

        synchronized(queue) {
            queue.add(buffer);
            // Currently no thread writing, make this thread dispatch a write
            if (!writing) {
                writing = true;
                threadShouldWrite = true;
            }
        }

        if (threadShouldWrite) {
        	writeFromQueueOld();
        }
    }
    
    @Deprecated
    private void writeFromQueueOld() {
        ByteBuffer buffer;

        synchronized (queue) {
            buffer = queue.poll();
            if (buffer == null) {
                writing = false;
            }
        }

        // No new data in buffer to write
        if (writing) {
        	writeBufferOld(buffer);
        }
    }
    
    @Deprecated
    private void writeBufferOld(ByteBuffer buffer) {
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                	writeFromQueueOld();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }
    
    

    /**
     * Sends a message
     * @param string the message
     */
    @Deprecated
    public void writeStringMessageOld(String string) {
    	writeOld(ByteBuffer.wrap(string.getBytes()));
    }

    /**
     * Send a message from a specific client
     * @param client the message is sent from
     * @param message to send
     */
    @Deprecated
    public void writeMessageFromOld(AioServerChannel client, String message) {
        if (dealer.serverAcceptsMessages()) {
            //writeStringMessage(client.getUserName() + ": " + message);
        }
    }
    

	

}
