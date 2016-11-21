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

package waterwave.proxy.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import waterwave.common.buffer.BufferPoolNIO;
import waterwave.common.service.ShutdownService;
import waterwave.common.service.SingleThreadService;
import waterwave.common.util.PropertiesUtil;
import waterwave.net.nio.NioClient;
import waterwave.net.nio.NioServer;
import waterwave.proxy.router.ProxyRouter;

public class ProxyNioServerService extends SingleThreadService{

	public static void main(String[] args) {
		ProxyNioServerService service = new ProxyNioServerService();
		
		Properties pp = new Properties();
		service.init(pp);

	}
	
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
	
	@Override
	public void onTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Properties p) {
		
		//
		log.log(9 , "init...");
		log.log(9 , p);
		new ShutdownService(this);
		
		//
		int threadNum = Runtime.getRuntime().availableProcessors() / 2;
		if (threadNum == 0) {
			threadNum = 1;
		}
		
		
		threadNum = 2;
		//
		int serverPort = 8001;
		
		PropertiesUtil pp = new PropertiesUtil(p);
		threadNum = pp.getInt("threadNum", threadNum);
		serverPort = pp.getInt("serverPort", serverPort);
		
		
		ExecutorService serverES = Executors.newFixedThreadPool(threadNum, Executors.defaultThreadFactory());
		ExecutorService clientES = Executors.newFixedThreadPool(threadNum, Executors.defaultThreadFactory());
		
		NioServer server = null;
		NioClient client = null;
		
		int bpSize = 4000;
		int bpBufferSize = 2 * 1024 * 1024;
		
		BufferPoolNIO bp = new BufferPoolNIO(bpSize, bpBufferSize);
		
		ProxyNioDataDealerFactory nioDataDealerFactory = new ProxyNioDataDealerFactory();
		try {
			server = new NioServer(serverPort,serverES, false, false, bp, nioDataDealerFactory);
			client = new NioClient(clientES, bp, nioDataDealerFactory);
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		nioDataDealerFactory.setServer(server);
		nioDataDealerFactory.setClient(client);

		//IP
		String ipStr = null;
		int remortPort = 80;
		
		remortPort = 9300;
		remortPort = 9200;
		remortPort = 11200;
		remortPort = 3306;
		
		ipStr = "www.baidu.com";
		ipStr = "news.163.com";
		ipStr = "www.bing.com";
		ipStr = "10.213.33.176";
		ipStr = "127.0.0.1";
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(ipStr);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		ProxyRouter.staticRemote = new InetSocketAddress(ip , remortPort);
		this.start();
		server.run();
		//client.run();
		
		log.log(9 , "init finished");
	}

	@Override
	public void onExit() {
		log.log(9 , "exit...");
		// TODO Auto-generated method stub
		
	}

	public void setClient(NioClient client) {
		// TODO Auto-generated method stub
		
	}



}
