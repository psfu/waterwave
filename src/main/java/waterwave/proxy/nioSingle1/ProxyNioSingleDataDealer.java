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

package waterwave.proxy.nioSingle1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import shui.common.buffer.BufferTools;
import shui.common.buffer.CommonBuffer;
import shui.common.log.Logger;
import shui.common.service.ThreadSharedService;
import shui.common.util.CommonUtil;
import waterwave.net.Channel;
import waterwave.net.nioSingle1.NioSingleClient;
import waterwave.net.nioSingle1.NioSingleClientChannel;
import waterwave.net.nioSingle1.NioSingleServerChannel;
import waterwave.net.nioSingle1.define.NioSingleClientDataDealer;
import waterwave.net.nioSingle1.define.NioSingleServerDataDealer;
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
	public void onConnect(Channel channel) {
		// log.log(1, "serverOnConnect...", channel);

		this.sc = (NioSingleServerChannel) channel;
		InetAddress ip = ProxyRouter.staticRemoteIp;
		int port = ProxyRouter.staticRemotePort;

		clientIniting = true;

		try {
			this.cc = client.createConnect(ip, port, this);
			client.connect(this.cc);
		} catch (IOException e) {
			e.printStackTrace();
			this.clientOnError(null, null, e);
		}
	}

	@Override
	public void beforeRead(Channel channel) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onData(Channel channel, CommonBuffer b, int bytes) {
		// log.log(1, "serverOnData...", b);

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

	private final void writeToClient(CommonBuffer buffer) {

		try {
			cc.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			// clientOnError(cc, e, buffer);
		}

	}

	private final void writeToServer(CommonBuffer buffer) {
		try {
			sc.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			// serverOnError(sc, e, buffer);
		}
	}

	@Override
	public void afterWrite(Channel channel, CommonBuffer buffer, int bytes) {
		// chekcServerQueue();
	}

	@Override
	public void onError(Channel channel, CommonBuffer b, Throwable e) {
		log.log(9, "serverOnError...", e.toString());
		if (b != null) {
			log.log(9, "serverOnError... : ByteBuffer", b.b.position());

			e.printStackTrace();

			// TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b.b)));
			//
			writeToServer(b);
		} else {
			String err = CommonUtil.getStringFromException(e);
			ByteBuffer b1 = ByteBuffer.wrap(err.getBytes());

			b.b.clear();
			b.b.put(b1);
			writeToServer(b);
		}
		cc.close();
		sc.close();

	}

	@Override
	public void onClose(Channel channel) {
		log.log(3, "dealer:serverOnClose...");
		if (cc != null && !cc.isClosed()) {
			cc.close();
		}
	}

	@Override
	public boolean acceptsMessages() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clientBeforeRead(Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientOnConnect(Channel channel) {
		// log.log(1, "clientOnData...", channel);
		clientIniting = false;
		// TODO Auto-generated method stub
		// chekcClientQueue();
	}

	@Override
	public void clientOnData(Channel channel, CommonBuffer b, int bytes) {
		// log.log(1, "clientOnData...", b);

		// TODO
		// Logger.log(new String(BufferTools.getBuffer2Byte(b)));
		// Logger.log(BufferTools.getBuffer2Byte(b));

		if (cc == null) {
			cc = (NioSingleClientChannel) channel;
		}
		writeToServer(b);
		// chekcClientQueue();

	}

	@Override
	public void clientAfterWrite(Channel channel, CommonBuffer buffer, int bytes) {

		// chekcClientQueue();

	}

	@Override
	public void clientOnError(Channel channel, CommonBuffer b, Throwable e) {
		log.log(9, "clientOnError...", e.toString());
		if (b != null) {
			log.log(9, "clientOnError... : ByteBuffer", b.b.position());

			e.printStackTrace();

			// TODO
			Logger.log(new String(BufferTools.getBuffer2Byte(b.b)));
			//
			writeToServer(b);
		} else {
			// b = BufferTools.getBuffer();
			String err = CommonUtil.getStringFromException(e);
			ByteBuffer b1 = ByteBuffer.wrap(err.getBytes());

			b.b.clear();
			b.b.put(b1);
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
	public void clientOnClose(Channel channel) {
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
