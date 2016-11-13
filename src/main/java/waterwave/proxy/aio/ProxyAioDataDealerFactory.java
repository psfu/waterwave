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

import waterwave.net.aio.AioClient;
import waterwave.net.aio.AioClientDataDealer;
import waterwave.net.aio.AioDataDealerFactory;
import waterwave.net.aio.AioServer;
import waterwave.net.aio.AioServerDataDealer;

public class ProxyAioDataDealerFactory implements AioDataDealerFactory {

	AioServer server;
	AioClient client;

	public AioServer getServer() {
		return server;
	}

	public void setServer(AioServer server) {
		this.server = server;
	}

	public AioClient getClient() {
		return client;
	}

	public void setClient(AioClient client) {
		this.client = client;
	}


	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AioServerDataDealer getAioServerDataDealer() {
		ProxyAioDataDealer dl = new ProxyAioDataDealer();
		dl.setClient(client);
		return dl;
	}

	@Override
	public AioClientDataDealer getAioClientDataDealer() {
		// TODO Auto-generated method stub
		return null;
	}

}
