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

package waterwave.proxy.nioSingle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import shuisea.common.buffer.BufferTools;
import shuisea.common.log.Logger;
import shuisea.common.service.ThreadSharedService;
import shuisea.common.util.Common;
import waterwave.net.nioSingle.NioSingleClient;
import waterwave.net.nioSingle.NioSingleClientChannel;
import waterwave.net.nioSingle.NioSingleServerChannel;
import waterwave.net.nioSingle.define.NioSingleClientDataDealer;
import waterwave.net.nioSingle.define.NioSingleServerDataDealer;
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
public class ProxyNioSingleDataDealer extends ThreadSharedService implements NioSingleServerDataDealer, NioSingleClientDataDealer {

	NioSingleClientChannel cc;
	NioSingleServerChannel sc;

	NioSingleClient client;
	SocketAddress remote;

	private boolean clientIniting;

	@Override
	public void serverOnConnect(NioSingleServerChannel channel) {
		//log.log(1, "serverOnConnect...", channel);
		 
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
	public void serverBeforeRead(NioSingleServerChannel channel) {

		// TODO Auto-generated method stub

	}

	@Override
	public void serverOnData(NioSingleServerChannel channel, ByteBuffer b, int bytes) {
		//log.log(1, "serverOnData...", b);

		// TODO
		// Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		// Logger.log(BufferTools.getBuffer2Byte(b));

		if (cc == null) {
			if (clientIniting) {
				log.log(9, "serverOnData clientIniting long ...............");
			}

			// waitingToIniting(b);

		} else {
			// normal
			writeToClient(b);
			// chekcServerQueue();
		}

	}

	// private final LinkedList<ByteBuffer> cq = new LinkedList<>();
	// private final LinkedList<ByteBuffer> sq = new LinkedList<>();

	// private void waitingToIniting(ByteBuffer buffer) {
	// synchronized (cq) {
	// cq.add(buffer);
	// }
	// }

	private final void writeToClient(ByteBuffer buffer) {

		try {
			cc.write0(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			//clientOnError(cc, e, buffer);
		}

	}

	private final void writeToServer(ByteBuffer buffer) {
		try {
			sc.write0(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			//serverOnError(sc, e, buffer);
		}
	}

	@Override
	public void serverAfterWrite(NioSingleServerChannel channel, ByteBuffer buffer, int bytes) {
		// chekcServerQueue();
	}

	@Override
	public void serverOnError(NioSingleServerChannel channel, Throwable e, ByteBuffer b) {
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
	public void serverOnClose(NioSingleServerChannel channel) {
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
	public void clientBeforeRead(NioSingleClientChannel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientOnConnect(NioSingleClientChannel channel) {
		//log.log(1, "clientOnData...", channel);
		clientIniting = false;
		// TODO Auto-generated method stub
		// chekcClientQueue();
	}

	@Override
	public void clientOnData(NioSingleClientChannel channel, ByteBuffer b, int bytes) {
		//log.log(1, "clientOnData...", b);

		// TODO
		// Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		// Logger.log(BufferTools.getBuffer2Byte(b));

		if (cc == null) {
			cc = channel;
		}
		writeToServer(b);
		// chekcClientQueue();

	}

	@Override
	public void clientAfterWrite(NioSingleClientChannel channel, ByteBuffer buffer, int bytes) {

		// chekcClientQueue();

	}

	@Override
	public void clientOnError(NioSingleClientChannel channel, Throwable e, ByteBuffer b) {
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
	public void clientOnClose(NioSingleClientChannel channel) {
		if (!sc.isClosed()) {
			sc.close();
		}
	}

	@Override
	public boolean clientAcceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClient(NioSingleClient client) {
		this.client = client;

	}

}
