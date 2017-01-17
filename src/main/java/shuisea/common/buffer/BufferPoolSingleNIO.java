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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import shuisea.common.log.Logger;
import shuisea.common.log.SimpleLogger;

public final class BufferPoolSingleNIO {

	LinkedList<ByteBuffer> bq = new LinkedList<ByteBuffer>();
	ArrayList<ByteBuffer> bs = null;

	public Logger log = new SimpleLogger();

	int size;
	int bsize;

	int i = 0;

	//AtomicInteger c = new AtomicInteger(0);
	int c = 0;

	/**
	 * 
	 * @param size
	 * @param bsize
	 */
	public BufferPoolSingleNIO(int size, int bsize) {
		this.size = size;
		bs = new ArrayList<ByteBuffer>(size);
		for (; i < size; i++) {
			ByteBuffer b =  create(bsize);
			bs.add(b);
			bq.add(b);
		}
	}
	
	private ByteBuffer create(int size) {
        return ByteBuffer.allocate(size);
    }

	public void checkBuffer() {
		int size = bq.size();
		//int out = c.get();
		int out = c;

		int total = size + out;
		if (total < size) {

		}
	}
	
	
	public CommonBuffer getBuffer() {
		ByteBuffer b = bq.getFirst();
		
		CommonBuffer cb = new CommonBuffer(b);
		//b.stat = 1;
		//c.getAndIncrement();
		++c;

		return cb;
	}

	public ByteBuffer getBufferNio() {
		ByteBuffer b;

		b = bq.getFirst();
		//b.stat = 1;
		//c.getAndIncrement();
		++c;

		return b;
	}

	public void finishBuffer(ByteBuffer b) {
//		if (b.stat == 0) {
//			return;
//		}
//		b.stat = 0;
//		b.pos = 0;
		
		b.clear();
		boolean offer = bq.offer(b);

		if (!offer) {
			log.log(10, "offer bqw fail!!");
			return;
		}
		//c.decrementAndGet();
		--c;
	}

	public void giveupBuffer(ByteBuffer b) {
		finishBuffer(b);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
