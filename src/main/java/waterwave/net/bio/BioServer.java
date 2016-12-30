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
package waterwave.net.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import shuisea.common.buffer.BufferPool;
import shuisea.common.service.SingleThreadService;
import waterwave.net.bio.define.BioDataDealerFactory;
import waterwave.net.bio.define.BioServerHandler;

public class BioServer extends SingleThreadService {
	
	final static int TIMEOUT = 3000 * 1000;
	final static int RCVBUF = 32 * 1024;
	
	private final int port;
	private final ServerSocket ss;
	
	// int interval = 1500;
	
	private final ExecutorService es;
	private final BufferPool bp;
	
//	long last;
//	String thisIp;

	// 0:init 1:sleeping 2:running
	int status = 0;
	boolean closed = false;

	private final BioDataDealerFactory bioDataDealerFactory;

	// BioServerChannelPools ps = null;

	public int getPort() {
		return port;
	}


	

	//
	//int poolSize = 0;
	//List<BioServerHandlerOld> pdhs = new ArrayList<BioServerHandlerOld>();
	
	// CoordinatorService cs = null;
	//
	// public LogReceiverServerService(CoordinatorService cs) {
	// this.cs = cs;
	// }


	


	public BioServer(int port, ExecutorService es, BufferPool bp, BioDataDealerFactory bioDataDealerFactory) throws IOException {
		super();
		this.port = port;
		this.es = es;
		this.bp = bp;
		this.bioDataDealerFactory = bioDataDealerFactory;
		
		ss = new ServerSocket(port);
	}






	@Override
	public void run() {
		for (;;) {
			try {
				Socket s = ss.accept();
				s.setTcpNoDelay(true);
				s.setSoTimeout(TIMEOUT);
				s.setReceiveBufferSize(RCVBUF);

				if (closed) {
					break;
				}
				bioAccept(s);
				// ps.dealSocket(s);
				dealNewConnection(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private final void bioAccept(Socket s) {
		InetSocketAddress rsa = (InetSocketAddress) s.getRemoteSocketAddress();
		//log.log(1 "BioServer: accept ", rsa.getHostName(), rsa.getPort());
	}

	private void dealNewConnection(Socket s) {
		BioServerChannel c = new BioServerChannel(s, bp, this);
		BioServerHandler handler = bioDataDealerFactory.getBioServerDataDealer();
		// BioServerHandlerOld handler = new BioServerHandlerOld(s, bp, this);
		handler.init(c);
		es.execute(handler);

	}

	@Override
	public void init(Properties pp) {
		
//		try {
//			String _port = pp.getProperty("serverPort", "9001");
//			port = Integer.parseInt(_port);
//
//			// TODO
//			// this.ps = cs.ps;
//
//			ss = new ServerSocket(port);
//			this.start();
//			log.log(9, "server start...", port);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void onExit() {
		closed = true;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTime() {
		// TODO Auto-generated method stub

	}

}
