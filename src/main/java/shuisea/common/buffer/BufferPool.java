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

package shuisea.common.buffer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import shuisea.common.log.Logger;
import shuisea.common.log.SimpleLogger;



public class BufferPool {

	LinkedBlockingQueue<BufferSp> bq = new LinkedBlockingQueue<BufferSp>(500);
	ArrayList<BufferSp> bs = null;

	public Logger log = new SimpleLogger();

	int size;
	int bsize;

	int i = 0;

	AtomicInteger c = new AtomicInteger(0);

	/**
	 * 
	 * @param size
	 * @param bsize
	 */
	public BufferPool(int size, int bsize) {
		this.size = size;
		bs = new ArrayList<BufferSp>(size);
		for (; i < size; i++) {
			BufferSp b = new BufferSp(i, bsize);
			bs.add(b);
			bq.add(b);
		}
	}

	public void checkBuffer() {
		int size = bq.size();
		int out = c.get();

		int total = size + out;
		if (total < size) {

		}
	}

	public BufferSp getBuffer() {
		BufferSp b;
		try {
			b = bq.take();
			b.stat = 1;
			c.getAndIncrement();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	public void finishBuffer(BufferSp b) {
		if (b.stat == 0) {
			return;
		}
		b.stat = 0;
		b.pos = 0;
		boolean offer = bq.offer(b);

		if (!offer) {
			log.log(10, "offer bqw fail");
			return;
		}
		c.decrementAndGet();
	}
	
	public void giveupBuffer(BufferSp b) {
		finishBuffer(b);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
