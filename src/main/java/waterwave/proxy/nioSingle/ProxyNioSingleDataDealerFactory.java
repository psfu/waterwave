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

package waterwave.proxy.nioSingle;

import waterwave.net.nioSingle.NioSingleClient;
import waterwave.net.nioSingle.NioSingleServer;
import waterwave.net.nioSingle.define.NioSingleClientDataDealer;
import waterwave.net.nioSingle.define.NioSingleDataDealerFactory;
import waterwave.net.nioSingle.define.NioSingleServerDataDealer;

public class ProxyNioSingleDataDealerFactory implements NioSingleDataDealerFactory {

	NioSingleServer server;
	NioSingleClient client;

	public NioSingleServer getServer() {
		return server;
	}

	public void setServer(NioSingleServer server) {
		this.server = server;
	}

	public NioSingleClient getClient() {
		return client;
	}

	public void setClient(NioSingleClient client) {
		this.client = client;
	}

	@Override
	public NioSingleServerDataDealer getNioServerDataDealer() {
		ProxyNioSingleDataDealer dl = new ProxyNioSingleDataDealer();
		dl.setClient(client);
		return dl;
	}

	@Override
	public NioSingleClientDataDealer getNioClientDataDealer() {
		// TODO Auto-generated method stub
		return null;
	}

}
