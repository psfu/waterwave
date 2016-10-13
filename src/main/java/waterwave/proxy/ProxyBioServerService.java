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
package waterwave.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import waterwave.common.buffer.BufferPool;
import waterwave.common.service.ShutdownService;
import waterwave.common.service.SingleThreadService;
import waterwave.net.bio.BioClient;
import waterwave.net.bio.BioServer;

public class ProxyBioServerService  extends SingleThreadService {
	
	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(1000);
				onTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ProxyBioServerService service = new ProxyBioServerService();
		service.init(new Properties());

	}

	@Override
	public void init(Properties pp) {
		//
		log.log(9 , "init...");
		new ShutdownService(this);
		

		
		int poolSize = Runtime.getRuntime().availableProcessors() * 5;
		
		ExecutorService serverES = Executors.newFixedThreadPool(poolSize, Executors.defaultThreadFactory());
		ExecutorService clientES = Executors.newFixedThreadPool(poolSize, Executors.defaultThreadFactory());
		
		ProxyBioDataDealerFactory bioDataDealerFactory = new ProxyBioDataDealerFactory();
		
		int bpSize = 400;
		int bpBufferSize = 2 * 1024 * 1024;
		
		BufferPool bp = new BufferPool(bpSize,bpBufferSize);
		BioServer bioServer = null;
		BioClient bioClient = new BioClient(clientES, bp);
		try {
			bioServer = new BioServer(8001, serverES, bp, bioDataDealerFactory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bioDataDealerFactory.setClient(bioClient);
		bioDataDealerFactory.setServer(bioServer);
		
		
		//IP
		InetAddress ip;
		int remortPort = 80;
		remortPort = 9200;
		remortPort = 3306;
		try {
//			ip = InetAddress.getLocalHost();
//			ip = InetAddress.getByName("www.baidu.com");
//			ip = InetAddress.getByName("news.163.com");
//			ip = InetAddress.getByName("www.bing.com");
			ip = InetAddress.getByName("127.0.0.1");
			ProxyAioRouter.staticRemoteIp = ip;
			ProxyAioRouter.staticRemotePort = remortPort;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		bioServer.start();
		
		log.log(9 , "init finished");
		
	}

	@Override
	public void onExit() {
		log.log(9 , "exit...");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTime() {
		// TODO Auto-generated method stub
		
	}

}
