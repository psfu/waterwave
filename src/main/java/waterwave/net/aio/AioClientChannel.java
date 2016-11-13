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

import waterwave.common.buffer.BufferTools;
import waterwave.net.aio.define.AioClientDataDealer;

public final class AioClientChannel extends AioChannel{
	
	private final AsynchronousSocketChannel channel;
	private final AioClientDataDealer dealer;
	
	private final AioClient aioClient;
	private final int channelId;
	
	
	

	public AsynchronousSocketChannel getChannel() {
		return channel;
	}

	public int getChannelId() {
		return channelId;
	}	

	public AioClientChannel(int channelId, AsynchronousSocketChannel channel, AioClientDataDealer dealer, AioClient aioClient) {
		this.channelId = channelId;
		this.channel = channel;
		this.dealer = dealer;
		this.aioClient = aioClient;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void run(ReadHandler reader) {
		dealer.clientBeforeRead(this);
		if(reader == null){
			//new
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
		AioClientChannel clientChannel;
		
        public ReadHandler(AioClientChannel clientChannel) {
			super();
			this.clientChannel = clientChannel;
		}

        @Override
		public void completed(Integer result, ByteBuffer buffer) {
			// if result is negative or zero the connection has been closed or something gone wrong
			//log.log(1, "client ReadHandler read success: ");
			if (result < 1) {
				clientChannel.close();
				dealer.clientOnClose(clientChannel);
				
				//log.log(1, "result < 1 ReadHandler Closing connection to " + channel);
			} else {
//				System.out.println("result: " + result);
//				// callback.onData(client, buffer, result);
//				buffer.flip();
//				String name;
//				name = new String(buffer.array(), 0, result);
//				System.out.println("data: " + name);
//				// enqueue next round of actions
//				// client.run();
				
				dealer.clientOnData(clientChannel, buffer, result);
                // enqueue next round of actions
				clientChannel.run(this);
				
			}
			
		}

		@Override
		public void failed(Throwable e, ByteBuffer buffer) {
			log.log(9, "client ReadHandler read fail: " + e);
			
			clientChannel.close();
			dealer.clientOnError(clientChannel, e, buffer);
		}
		
//		public void onData(AioServerChannel channel, ByteBuffer buffer, int bytes) {
//	        buffer.flip();
//	        // Just append the message on the buffer
//	        //AioServerChannel.appendMessage(new String(buffer.array(), 0, bytes));
//	    }
		
	}
	
	class WriteHandler implements CompletionHandler<Integer, ByteBuffer>{
		AioClientChannel clientChannel;
		
        public WriteHandler(AioClientChannel clientChannel) {
			super();
			this.clientChannel = clientChannel;
		}

		@Override
		public void completed(Integer result, ByteBuffer buffer) {
			// if result is negative or zero the connection has been closed or something gone wrong
			//log.log(1, "client WriteHandler write success: ");
			if (buffer.hasRemaining()) {
				//log.log(1, "client WriteHandler write...  hasRemaining ");
				channel.write(buffer, buffer, this);
			} else {
				// Go back and check if there is new data to write
				// writeFromQueue();
				//log.log(1, "client write complete " + result);

			}
			dealer.clientAfterWrite(clientChannel, buffer, result);
			
			BufferTools.returnBuffer(buffer);
		}

		@Override
		public void failed(Throwable e, ByteBuffer buffer) {
			log.log(9, "client WriteHandler write fail: " + e);
			clientChannel.close();
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
    	//log.log(1, "Cliet: start client read ");
    	
        ByteBuffer input = BufferTools.getBuffer();
        if (!channel.isOpen()) {
            return;
        }
        channel.read(input, input, completionHandler);
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
    	//log.log(1, "start client write ");
    	
        if (!channel.isOpen()) {
            return;
        }
        channel.write(b, b, completionHandler);
    }
    

	public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        aioClient.removeAioChannel(this);
		
		
	}

}
