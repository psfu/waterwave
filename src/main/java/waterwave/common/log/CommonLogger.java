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

package waterwave.common.log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonLogger extends Logger {

	final static int bufferSize = 256 * 1024;

	byte[] bu = null;
	int pos = 0;
	int size = 0;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CommonLogger() {

	}

	public CommonLogger(int size) {
		this.size = size;
	}
	
	@Override
	public PrintWriter getErrorWriter() {
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.err)));
	}
	
	@Override
	public void log(int level, Object o) {
		log0(o);
		log0();
	}

	@Override
	public void log(int level, Object... os) {
		printPrefix();
		for (int i = 0; i < os.length; ++i) {
			log0(os[i]);
			log0("\t");
		}

		log0();
	}

	private void log0() {
		log0("\r\n");
		this.flush();

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

	final private void log0(Object o) {
		String s = null;
		if (o == null) {
			s = "null";
		} else {
			s = o.toString();
		}

		byte[] b = s.getBytes();

		int remain = size - pos;
		int l = b.length;
		if (l > remain) {
			if (l > size) {
				l = size;
			}
			this.flush();
		}
		System.arraycopy(b, 0, bu, pos, l);
	}

	private void flush() {
		System.out.write(bu, 0, pos);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
