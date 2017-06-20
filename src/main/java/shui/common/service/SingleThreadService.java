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

package shui.common.service;

import java.util.Properties;

import shui.common.log.Logger;
import shui.common.log.SimpleLogger;



public abstract class SingleThreadService extends Thread implements Runnable {
	protected Logger log = new SimpleLogger(true);

	public abstract void init(Properties pp);
	
	public abstract void onExit();
	
	public abstract void onTime();
	
	public static void log(Object o) {
		Logger.log(o);
	}

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			log.log(9, e);
		}
	}
	
	protected void sysExit() {
		
		log.log(9, "---> finish and exit ...");
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}

		};
		t.start();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
