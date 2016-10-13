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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import waterwave.common.service.ShutdownService;
import waterwave.common.service.SingleThreadService;
import waterwave.net.aio.AioClient;
import waterwave.net.aio.AioServer;

public class ProxyAioServerService extends SingleThreadService{

	public static void main(String[] args) {
		ProxyAioServerService service = new ProxyAioServerService();
		service.init(new Properties());

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
	public void init(Properties pp) {
		
		//
		log.log(9 , "init...");
		new ShutdownService(this);
		
		int threadNum = Runtime.getRuntime().availableProcessors() / 2;
		if (threadNum == 0) {
			threadNum = 1;
		}
		
		threadNum = 2;
		
		ExecutorService serverES = Executors.newFixedThreadPool(threadNum, Executors.defaultThreadFactory());
		ExecutorService clientES = Executors.newFixedThreadPool(threadNum, Executors.defaultThreadFactory());
		
		AioServer server = null;
		AioClient client = null;
		
		ProxyAioDataDealerFactory aioDataDealerFactory = new ProxyAioDataDealerFactory();
		try {
			server = new AioServer(8001, serverES, aioDataDealerFactory);
			client = new AioClient(clientES, aioDataDealerFactory);
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		aioDataDealerFactory.setServer(server);
		aioDataDealerFactory.setClient(client);

		//IP
		InetAddress ip;
		int remortPort = 80;
		remortPort = 9300;
		remortPort = 9200;
		remortPort = 11200;
		remortPort = 3306;
		try {
//			ip = InetAddress.getLocalHost();
//			ip = InetAddress.getByName("www.baidu.com");
//			ip = InetAddress.getByName("news.163.com");
//			ip = InetAddress.getByName("www.bing.com");
			ip = InetAddress.getByName("10.213.33.176");
			ip = InetAddress.getByName("127.0.0.1");
			ProxyAioRouter.staticRemote = new InetSocketAddress(ip , remortPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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



}
