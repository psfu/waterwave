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

import waterwave.net.bio.BioClient;
import waterwave.net.bio.BioDataDealerFactory;
import waterwave.net.bio.BioServer;
import waterwave.net.bio.BioServerHandler;

public class ProxyBioDataDealerFactory implements BioDataDealerFactory {
	
	private  BioClient client;
	private  BioServer server;
	

	public BioClient getClient() {
		return client;
	}

	public void setClient(BioClient client) {
		this.client = client;
	}
	
	public BioServer getServer() {
		return server;
	}

	public void setServer(BioServer server) {
		this.server = server;
	}


	@Override
	public BioServerHandler getBioServerDataDealer() {
		// TODO Auto-generated method stub
		return new ProxyBioHanlder(client, server);
	}



}
