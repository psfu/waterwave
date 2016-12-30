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
package waterwave.proxy.bio;

import java.io.IOException;

import shuisea.common.buffer.BufferSp;
import shuisea.common.service.ThreadSharedService;
import waterwave.net.bio.BioClient;
import waterwave.net.bio.BioServer;
import waterwave.net.bio.BioServerChannel;
import waterwave.net.bio.define.BioClientHandler;
import waterwave.net.bio.define.BioServerHandler;
import waterwave.proxy.router.ProxyRouter;

public class ProxyBioHanlder extends ThreadSharedService implements BioServerHandler{
	
	private final BioClient client;
	//private final BioServer server;
	private BioServerChannel sc;
	private BioServerChannel cc;

	public ProxyBioHanlder(BioClient client, BioServer server) {
		super();
		this.client = client;
		//this.server = server;
	}

	@Override
	public void run() {
		//log.log(1, "ProxyBioHanlder: server runing....");
		try {
			this.cc = client.connect(ProxyRouter.staticRemoteIp, ProxyRouter.staticRemotePort);
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		
		ProxyBioClientHanlder hc = new ProxyBioClientHanlder(sc,cc);
		client.startHandler(hc);
		
		for(;;){
			if(cc.isClose) {
				break;
			}
			//log.log(1, "ProxyBioHanlder: server cc reading...");
			BufferSp bc = this.cc.read();
			
			//Logger.log(new String(bc.b, 0, bc.pos));
			
			//log.log(1, "ProxyBioHanlder: server sc writing...");
			this.sc.write(bc);
		}
	}
	
	class ProxyBioClientHanlder extends ThreadSharedService implements BioClientHandler {
		
		private BioServerChannel sc;
		private BioServerChannel cc;

		public ProxyBioClientHanlder(BioServerChannel sc, BioServerChannel cc) {
			super();
			this.sc = sc;
			this.cc = cc;
		}

		@Override
		public void run() {
			//log.log(1, "ProxyBioHanlder: client runing....");
			for(;;){
				if(sc.isClose) {
					break;
				}
				
				//log.log(1, "ProxyBioHanlder: client sc reading...");
				BufferSp bs = this.sc.read();
				
				//Logger.log(new String(bs.b, 0, bs.pos));
				
				//log.log(1, "ProxyBioHanlder: client cc writing...");
				this.cc.write(bs);
			}
			
		}
		
	}
	 

	@Override
	public void init(BioServerChannel sc) {
		this.sc = sc;
	}

	@Override
	public void serverOnError(BioServerChannel c, Throwable e, BufferSp b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverOnClose(BioServerChannel channel) {
		// TODO Auto-generated method stub
		
	}

}
