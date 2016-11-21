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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import waterwave.common.buffer.BufferPool;
import waterwave.common.service.SingleThreadService;
import waterwave.net.bio.define.BioClientHandler;

public class BioClient  extends SingleThreadService {

	final static int TIMEOUT = 3000 * 1000;
	final static int RCVBUF = 32 * 1024;
	final static int SNDBUF = 32 * 1024;

	private final ExecutorService es;
	private final BufferPool bp;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

	public BioClient(ExecutorService es, BufferPool bp) {
		super();
		this.es = es;
		this.bp = bp;
	}
	
	public void startHandler(BioClientHandler handler) {
		es.execute(handler);
	}

	public BioServerChannel connect(InetAddress ip, int port) throws IOException {
		Socket s = connect0(ip, port);

		BioServerChannel c = new BioServerChannel(s, bp, null);
		//log.log(1 "BioClient:  connect finish", s);

		return c;
	}

	public static Socket connect0(InetAddress ip, int port) throws IOException {

		Socket s = new Socket(ip, port);
		s.setTcpNoDelay(true);
		s.setSendBufferSize(SNDBUF);
		s.setReceiveBufferSize(RCVBUF);
		s.setSoTimeout(TIMEOUT);

		return s;
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
