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

package shui.common.log;

import java.io.PrintWriter;


public abstract class Logger {

	public String threadInfo = null;
	public int level;

	protected boolean printTime = true;
	protected boolean printThreadInfo = true;

	public Logger() {
	}

	public Logger(boolean printTime) {
		this();
		this.printTime = printTime;
	}

	
	public abstract void log(int level, Object o) ;
	public abstract void log(int level, Object... os) ;
	public abstract PrintWriter getErrorWriter();


	public final static void log(byte[] bs) {
		for (byte b : bs) {
			log1(b);
			log1("\t");
		}
		log("\t");
	}
	
	
	public final static void log1(Object o) {
		System.out.print(o);
	}
	
	public final static void log(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
