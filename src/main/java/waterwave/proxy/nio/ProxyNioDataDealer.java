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

package waterwave.proxy.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import shui.common.buffer.BufferTools;
import shui.common.log.Logger;
import shui.common.service.ThreadSharedService;
import shui.common.util.Common;
import waterwave.net.nio.NioClient;
import waterwave.net.nio.NioClientChannel;
import waterwave.net.nio.NioServerChannel;
import waterwave.net.nio.define.NioClientDataDealer;
import waterwave.net.nio.define.NioServerDataDealer;
import waterwave.proxy.router.ProxyRouter;

/**
 * 
 * @author vv
 * 
 * 
 *         server:in -> queue client:in -> queue
 * 
 *
 */
public class ProxyNioDataDealer extends ThreadSharedService implements NioServerDataDealer, NioClientDataDealer {

	NioClientChannel cc;
	NioServerChannel sc;

	NioClient client;
	SocketAddress remote;

	private boolean clientIniting;

	@Override
	public void serverOnConnect(NioServerChannel channel) {
		this.sc = channel;
		InetAddress ip = ProxyRouter.staticRemoteIp;
		int port = ProxyRouter.staticRemotePort;

		clientIniting = true;

		try {
			this.cc = client.createConnect(ip, port, this);
			client.connect(this.cc);
		} catch (IOException e) {
			e.printStackTrace();
			this.clientOnError(null, e, null);
		}
	}

	@Override
	public void serverBeforeRead(NioServerChannel channel) {

		// TODO Auto-generated method stub

	}

	@Override
	public void serverOnData(NioServerChannel channel, ByteBuffer b, int bytes) {
		// log.log(1, "serverOnData...", b);

		// TODO
		// Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		// Logger.log(BufferTools.getBuffer2Byte(b));

		if (cc == null) {
			if (clientIniting) {
				log.log(9, "serverOnData clientIniting long ...............");
			}

			waitingToIniting(b);

		} else {
			// normal
			writeToClient(b);
			chekcServerQueue();
		}

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
			sc.write0(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			// buffer.position(buffer.limit());
			// sq.add(buffer);
			// writeServerFromQueue();
		}
	}

	private final void writeToClient0(ByteBuffer buffer) {
		try {
			buffer.flip();
			cc.write0(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			// buffer.position(buffer.limit());
			// cq.add(buffer);
			// writeClientFromQueue();
		}
	}

	/**
	 * client writeQueue and merge Buffer;
	 * 
	 */
	private final void writeClientFromQueue() {
		// //log.log(1, "cq", cq, cq.size());
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
				if (b0 != b1) {

				}
				b0.put(b1);
				cq.poll();
				BufferTools.returnBuffer(b1);
			}
			// log.log(1, "writeClientFromQueue merging...", r0, s1);
		}
		// log.log(1, "writeClientFromQueue write...", b0.position());
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
			// log.log(1, "writeServerFromQueue merging...", r0, s1);
		}
		// log.log(1, "writeServerFromQueue write...", b0.position());
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
	public void serverAfterWrite(NioServerChannel channel, ByteBuffer buffer, int bytes) {
		chekcServerQueue();
	}

	@Override
	public void serverOnError(NioServerChannel channel, Throwable e, ByteBuffer b) {
		log.log(9, "serverOnError...", e.toString());
		if (b != null) {
			log.log(9, "serverOnError... : ByteBuffer", b.position());

			e.printStackTrace();

			// TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b)));
			//
			writeToServer(b);
		} else {
			String err = Common.getStringFromException(e);
			b = ByteBuffer.wrap(err.getBytes());
			writeToServer(b);
		}
		cc.close();
		sc.close();

	}

	@Override
	public void serverOnClose(NioServerChannel channel) {
		log.log(3, "dealer:serverOnClose...");
		if (cc != null && !cc.isClosed()) {
			cc.close();
		}
	}

	@Override
	public boolean serverAcceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clientBeforeRead(NioClientChannel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientOnConnect(NioClientChannel channel) {
		clientIniting = false;
		// TODO Auto-generated method stub
		chekcClientQueue();
	}

	@Override
	public void clientOnData(NioClientChannel channel, ByteBuffer b, int bytes) {
		// log.log(1, "clientOnData...", b);

		// TODO
		// Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		// Logger.log(BufferTools.getBuffer2Byte(b));

		if (cc == null) {
			cc = channel;
		}
		writeToServer(b);
		chekcClientQueue();

	}

	@Override
	public void clientAfterWrite(NioClientChannel channel, ByteBuffer buffer, int bytes) {

		chekcClientQueue();

	}

	@Override
	public void clientOnError(NioClientChannel channel, Throwable e, ByteBuffer b) {
		log.log(9, "clientOnError...", e.toString());
		if (b != null) {
			log.log(9, "clientOnError... : ByteBuffer", b.position());

			e.printStackTrace();

			// TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b)));
			//
			writeToServer(b);
		} else {
			// b = BufferTools.getBuffer();
			String err = Common.getStringFromException(e);
			b = ByteBuffer.wrap(err.getBytes());
			writeToServer(b);
		}
		if (cc != null) {
			cc.close();
		}
		if (sc != null) {
			sc.close();
		}

	}

	@Override
	public void clientOnClose(NioClientChannel channel) {
		if (!sc.isClosed()) {
			sc.close();
		}
	}

	@Override
	public boolean clientAcceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClient(NioClient client) {
		this.client = client;

	}

}
