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

import waterwave.net.nio.NioClient;
import waterwave.net.nio.NioServer;
import waterwave.net.nio.define.NioClientDataDealer;
import waterwave.net.nio.define.NioDataDealerFactory;
import waterwave.net.nio.define.NioServerDataDealer;

public class ProxyNioDataDealerFactory implements NioDataDealerFactory {

	NioServer server;
	NioClient client;

	public NioServer getServer() {
		return server;
	}

	public void setServer(NioServer server) {
		this.server = server;
	}

	public NioClient getClient() {
		return client;
	}

	public void setClient(NioClient client) {
		this.client = client;
	}


	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NioServerDataDealer getNioServerDataDealer() {
		ProxyNioDataDealer dl = new ProxyNioDataDealer();
		dl.setClient(client);
		return dl;
	}

	@Override
	public NioClientDataDealer getNioClientDataDealer() {
		// TODO Auto-generated method stub
		return null;
	}

}
