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
 */

package waterwave.proxy;

import java.util.Properties;

import waterwave.common.log.Logger;
import waterwave.common.util.Common;
import waterwave.proxy.aio.ProxyAioServerService;

public class ProxyServerStartUp {

	static final String initPpFile = "wwProxy.properties";

	public static void startUp(String[] args) {
		
		//get env param
		Properties initPp = Common.loadPropertiesfile(initPpFile);
		
		String ppfile = initPp.getProperty("service.ppfile");

		ppfile = Common.setPp(args, ppfile);

		Logger.log(ppfile);

		
		//get param
		Properties pp = Common.loadPropertiesfile(ppfile);

		Common.setArgs(args, pp);

		Logger.log(pp);
		
		//service start up
		ProxyAioServerService cs = new ProxyAioServerService();
		cs.init(pp);
	}

	public static void main(String[] args) {
		Logger.log("init...");
		startUp(args);

	}

}
