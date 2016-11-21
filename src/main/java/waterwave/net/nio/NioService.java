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
package waterwave.net.nio;

import java.nio.channels.SelectionKey;
import java.util.Properties;
import java.util.Set;

import waterwave.common.service.Service;
import waterwave.common.service.SingleThreadService;

public abstract class NioService extends SingleThreadService {
	
	
	
	
	static class Dispatcher extends SingleThreadService  {

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
	
	void showKeys(Set<SelectionKey> keys) {
		for (SelectionKey key:keys){
			log.log(1,key.readyOps(),key.interestOps(),key.channel(),key.attachment());
		}
		
	}
	
	static class NioHandler extends Service {
		
	}

}
