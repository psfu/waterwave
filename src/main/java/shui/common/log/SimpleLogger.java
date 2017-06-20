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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleLogger extends Logger {

	PrintWriter pw;
	PrintWriter ew;
	Calendar ca = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public SimpleLogger() {
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		ew = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.err)));
	}

	public SimpleLogger(boolean printTime) {
		this();
		this.printTime = printTime;
	}

	@Override
	public PrintWriter getErrorWriter() {
		return ew;
	}
	
	public void log(int level, Object o) {
		printPrefix();
		log0(o);
		log0();
	}

	public void log(int level, Object... os) {
		printPrefix();

		for (Object o : os) {
			log0(o);
			log0("\t");
		}

		log0();
	}

	private void printPrefix() {
		if (printTime) {
			String date = sdf.format(new Date(System.currentTimeMillis()));
			log0(date);
			log0(" : ");
		}
		if (threadInfo != null) {
			log0(threadInfo);
			log0(" : ");
		} else if (printThreadInfo) {
			String threadInfo = Thread.currentThread().getName();
			log0(threadInfo);
			log0(" : ");
		}
	}

	public final void log0(Object o) {

		if (o == null) {
			pw.write("null");
		} else {
			pw.write(o.toString());
		}
	}

	public final void log0() {
		pw.write("\r\n");
		pw.flush();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
