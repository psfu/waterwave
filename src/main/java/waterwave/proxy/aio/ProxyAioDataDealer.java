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
 */

package waterwave.proxy.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.LinkedList;

import shui.common.buffer.BufferTools;
import shui.common.log.Logger;
import shui.common.service.ThreadSharedService;
import shui.common.util.CommonUtil;
import shui.common.util.ParamUtil;
import waterwave.net.aio.AioClient;
import waterwave.net.aio.AioClientChannel;
import waterwave.net.aio.AioServerChannel;
import waterwave.net.aio.define.AioClientDataDealer;
import waterwave.net.aio.define.AioServerDataDealer;
import waterwave.proxy.router.ProxyRouter;

/**
 * 
 * @author vv
 * 
 * 
 *         server:in -> queue 
 *         client:in -> queue
 * 
 *
 */
public class ProxyAioDataDealer extends ThreadSharedService implements AioServerDataDealer, AioClientDataDealer {

	AioClientChannel cc;
	AioServerChannel sc;

	AioClient client;
	SocketAddress remote;

	private boolean clientIniting;

	public ProxyAioDataDealer() {

	}
	
	final static boolean debug = false;
	

	public AioClient getClient() {
		return client;
	}
	public void setClient(AioClient client) {
		this.client = client;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	


	@Override
	public void serverOnConnect(AioServerChannel channel) {
		this.sc = channel;
		
		clientIniting = true;
		try {
			remote = ProxyRouter.getStaticRemote();
			client.connect(remote, this);
		} catch (IOException e) {
			this.clientOnError(null, e, null);
			e.printStackTrace();
		}
	}

	@Override
	public void serverBeforeRead(AioServerChannel channel) {
	}

	@Override
	public void serverOnData(AioServerChannel channel, ByteBuffer b, int bytes) {
		//log.log(1, "serverOnData...", b);
		
		//TODO
		//Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		
		//init client
		if (cc == null) {
			if (clientIniting) {
				log.log(9, "serverOnData clientIniting long ...............");
			}

//			clientIniting = true;
//			try {
//				remote = ProxyAioRouter.getStaticRemote();
//				client.connect(remote, this);
//			} catch (IOException e) {
//				this.clientOnError(null, e, null);
//				e.printStackTrace();
//			}
			waitingToIniting(b);
			
		} else {
			//normal
			writeToClient(b);
			chekcServerQueue();
		}

	}
	

	@Override
	public void serverAfterWrite(AioServerChannel channel, ByteBuffer buffer, int bytes) {
		chekcServerQueue();
	}
	
	private final LinkedList<ByteBuffer> cq = new LinkedList<>();
	private final LinkedList<ByteBuffer> sq = new LinkedList<>();
	
	private void waitingToIniting(ByteBuffer buffer) {
		synchronized (cq) {
			cq.add(buffer);
		}
	}



	private final void writeToClient(ByteBuffer buffer) {
		synchronized (cq) {
			if (cq.size() == 0) {
				writeToClient0(buffer);
			} else {
				cq.add(buffer);
				writeClientFromQueue();
			}
		}
	}
	
	private final void writeToServer(ByteBuffer buffer) {
		synchronized (sq) {
			if (sq.size() == 0) {
				writeToServer0(buffer);
			} else {
				sq.add(buffer);
				writeServerFromQueue();
			}
		}
	}
	
	private final void writeToServer0(ByteBuffer buffer) {
		try {
			buffer.flip();
			sc.write(buffer);
		} catch (WritePendingException e) {
			e.printStackTrace();
			buffer.position(buffer.limit());
			sq.add(buffer);
			writeServerFromQueue();
		}
	}
	
	private final void writeToClient0(ByteBuffer buffer) {
		try {
			buffer.flip();
			cc.write(buffer);
		} catch (WritePendingException e) {
			e.printStackTrace();
			buffer.position(buffer.limit());
			cq.add(buffer);
			writeClientFromQueue();
		}
	}

	/**
	 * client writeQueue and merge Buffer;
	 * 
	 */
	private final void writeClientFromQueue() {
		//log.log(1, "cq", cq, cq.size());
		ByteBuffer b0 = cq.poll();
		
		for (;;) {
			ByteBuffer b1 = cq.peek();
			if (b1 == null) {
				break;
			}
			int r0 = b0.remaining();
			int s1 = b1.position();
			if (s1 > r0) {
				break;
			} else {
				if(b0 != b1) {
					
				}
				b0.put(b1);
				cq.poll();
				BufferTools.returnBuffer(b1);
			}
			//log.log(1, "writeClientFromQueue merging...", r0, s1);
		}
		//log.log(1, "writeClientFromQueue write...", b0.position());
		writeToClient0(b0);
	}
	
	/**
	 * server writeQueue and merge Buffer;
	 * 
	 */
	private final void writeServerFromQueue() {
		ByteBuffer b0 = sq.poll();

		for (;;) {
			ByteBuffer b1 = sq.peek();
			if (b1 == null) {
				break;
			}
			int r0 = b0.remaining();
			int s1 = b1.position();
			if (s1 > r0) {
				break;
			} else {
				b0.put(b1);
				sq.poll();
				BufferTools.returnBuffer(b1);
			}
			//log.log(1, "writeServerFromQueue merging...", r0, s1);
		}
		//log.log(1, "writeServerFromQueue write...", b0.position());
		writeToServer0(b0);
	}

	
	private final void chekcClientQueue() {
		synchronized (cq) {
			if (cq.size() != 0) {
				writeClientFromQueue();
			}
		}
	}
	
	private final void chekcServerQueue() {
		synchronized (cq) {
			if (cq.size() != 0) {
				writeClientFromQueue();
			}
		}
	}

	
	@Override
	public void serverOnError(AioServerChannel channel, Throwable e, ByteBuffer b) {
		log.log(9, "serverOnError...", e.toString());
		if (b != null) {
			log.log(9, "serverOnError... : ByteBuffer", b.position());
			

			e.printStackTrace();
			
			//TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b)));
			//
			writeToServer(b);
		} else {
			String err = CommonUtil.getStringFromException(e);
			b = ByteBuffer.wrap(err.getBytes());
			writeToServer(b);
		}
		cc.close();
		sc.close();
		
	}

	@Override
	public void serverOnClose(AioServerChannel channel) {
		log.log(3, "dealer:serverOnClose...");
		sc.close();
		cc.close();
	}

	@Override
	public boolean serverAcceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	/**
	 * client
	 * 
	 * 
	 * 
	 */
	@Override
	public void clientOnConnect(AioClientChannel channel) {
		clientIniting = false;
		this.cc = channel;
		chekcClientQueue();
	}

	@Override
	public void clientBeforeRead(AioClientChannel channel) {

	}

	@Override
	public void clientOnData(AioClientChannel channel, ByteBuffer b, int result) {
		//log.log(1, "clientOnData...", b);
		
		//TODO
		//Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		
		if (cc == null) {
			cc = channel;
		}
		writeToServer(b);
		chekcClientQueue();
	}
	

	@Override
	public void clientAfterWrite(AioClientChannel channel, ByteBuffer buffer, int bytes) {
		chekcClientQueue();
	}

	@Override
	public void clientOnError(AioClientChannel channel, Throwable e, ByteBuffer b) {
		log.log(9, "clientOnError...", e.toString());
		if (b != null) {
			log.log(9, "clientOnError... : ByteBuffer", b.position());
			
			e.printStackTrace();
			
			//TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b)));
			//
			writeToServer(b);
		} else {
			//b = BufferTools.getBuffer();
			String err = CommonUtil.getStringFromException(e);
			b = ByteBuffer.wrap(err.getBytes());
			writeToServer(b);
		}
		cc.close();
		sc.close();

	}

	@Override
	public void clientOnClose(AioClientChannel channel) {
		channel.close();
		sc.close();

	}

	@Override
	public boolean clientAcceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}





}
