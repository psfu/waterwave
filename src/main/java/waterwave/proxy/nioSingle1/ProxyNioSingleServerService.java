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

package waterwave.proxy.nioSingle1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import shuisea.common.buffer.BufferPoolSingleNIO;
import shuisea.common.service.ShutdownService;
import shuisea.common.service.SingleThreadService;
import shuisea.common.util.PropertiesUtil;
import waterwave.net.nio.NioClient;
import waterwave.net.nioSingle1.NioSingleClient;
import waterwave.net.nioSingle1.NioSingleServer;
import waterwave.proxy.router.ProxyRouter;

public class ProxyNioSingleServerService extends SingleThreadService {

	public static void main(String[] args) {
		ProxyNioSingleServerService service = new ProxyNioSingleServerService();

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
		log.log(9, "init...");
		log.log(9, p);
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

		NioSingleServer server = null;
		NioSingleClient client = null;

		int bpSize = 200;
		int bpBufferSize = 200 * 1024;

		BufferPoolSingleNIO bp1 = new BufferPoolSingleNIO(bpSize, bpBufferSize);

		ProxyNioSingleDataDealerFactory nioDataDealerFactory = new ProxyNioSingleDataDealerFactory();
		try {
			server = new NioSingleServer(serverPort, bp1, nioDataDealerFactory);
			client = new NioSingleClient(bp1, server, nioDataDealerFactory);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		nioDataDealerFactory.setServer(server);
		nioDataDealerFactory.setClient(client);

		// IP
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

		remortPort = pp.getInt("remortPort", remortPort);
		ipStr = pp.getString("ipStr", ipStr);

		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(ipStr);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		ProxyRouter.staticRemote = new InetSocketAddress(ip, remortPort);
		ProxyRouter.staticRemoteIp = ip;
		ProxyRouter.staticRemotePort = remortPort;

		this.start();
		//server.run();
		server.start();
		// client.run();

		log.log(9, "init finished");
	}

	@Override
	public void onExit() {
		log.log(9, "exit...");
		// TODO Auto-generated method stub

	}

	public void setClient(NioClient client) {
		// TODO Auto-generated method stub

	}

}
