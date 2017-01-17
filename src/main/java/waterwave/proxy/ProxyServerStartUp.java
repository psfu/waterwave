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

import shuisea.common.log.Logger;
import shuisea.common.util.Common;
import shuisea.common.util.PropertiesUtil;
import waterwave.proxy.ProxyRouterService.type;
import waterwave.proxy.aio.ProxyAioServerService;
import waterwave.proxy.bio.ProxyBioServerService;
import waterwave.proxy.nio.ProxyNioServerService;
import waterwave.proxy.nioSingle.ProxyNioSingleServerService;

public class ProxyServerStartUp {

	static final String initPpFile = "sp.properties";

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
		
		
		
		PropertiesUtil ppp = new PropertiesUtil(pp);
		type t = ppp.getEnum("type", ProxyRouterService.type.class, ProxyRouterService.type.aio);
		
		
		//service start up
		switch (t) {
		case aio:
			ProxyAioServerService cs = new ProxyAioServerService();
			cs.init(pp);
			break;
		case nio:
			ProxyNioServerService ns = new ProxyNioServerService();
			ns.init(pp);
			break;
		case single:
			ProxyNioSingleServerService ss = new ProxyNioSingleServerService();
			ss.init(pp);
			break;
		case single1:
			waterwave.proxy.nioSingle1.ProxyNioSingleServerService ss1 = new waterwave.proxy.nioSingle1.ProxyNioSingleServerService();
			ss1.init(pp);
			break;
		case bio:
			ProxyBioServerService bs = new ProxyBioServerService();
			bs.init(pp);
			break;
		}
		
		
		
	}

	public static void main(String[] args) {
		Logger.log("init...");
		startUp(args);

	}

}
