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
package shui.common.buffer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import shui.common.log.Logger;
import shui.common.log.SimpleLogger;


public class QueueBuffer {

	LinkedBlockingDeque<BufferSimple> bqr = new LinkedBlockingDeque<BufferSimple>(500);
	LinkedBlockingDeque<BufferSimple> bqw = new LinkedBlockingDeque<BufferSimple>(500);

	ArrayList<BufferSimple> bs = null;

	public Logger log = new SimpleLogger();

	public int size;
	public int bsize;

	AtomicInteger rc = new AtomicInteger(0);
	AtomicInteger wc = new AtomicInteger(0);
	int i;

	// b.stat: 0(rq)->2(reading)->3(wq)->1(writing)->0
	public QueueBuffer(int size, int bsize) {
		this.size = size;
		this.bsize = bsize;
		bs = new ArrayList<BufferSimple>(size);
		for (; i < size; i++) {
			BufferSimple b = new BufferSimple(i, bsize);

			bs.add(b);
			bqr.add(b);
		}
	}

	public void checkBuffer() {
		int rSize = bqr.size();
		int wSize = bqw.size();
		int rOut = rc.get();
		int wOut = wc.get();

		int total = rSize + wSize + rOut + wOut;
		if (total < size) {

		}

	}

	public BufferSimple getReadBuffer() {
		BufferSimple b;
		try {
			log.log(1, "take bqr", bqr);
			b = bqr.take();
			b.stat = 2;
			rc.incrementAndGet();
			log.log(1, "out bqr", bqr);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	public BufferSimple getReadBuffer(int timeout) {
		BufferSimple b;
		try {
			log.log(1, "take bqr timeout", bqr , timeout);
			// b = bqr.take();
			b = bqr.poll(timeout, TimeUnit.MILLISECONDS);
			if (b == null) {
				return b;
			}
			b.stat = 2;
			rc.incrementAndGet();
			log.log(1, "out bqr timeout", bqr);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	public BufferSimple getWriteBuffer() {
		BufferSimple b;
		try {
			log.log(1, "take bqw", bqw);
			b = bqw.take();
			b.stat = 1;
			wc.incrementAndGet();
			log.log(1, "out bqw", bqw);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	public BufferSimple getWriteBuffer(int timeout) {
		BufferSimple b;
		try {
			log.log(1, "take bqw timeout", bqw , timeout);
			b = bqw.poll(timeout, TimeUnit.MILLISECONDS);
			if (b == null) {
				return b;
			}
			b.stat = 1;
			wc.incrementAndGet();
			log.log(1, "out bqw timeout", bqw);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	public void finishReadBuffer(BufferSimple b) {
		if (b == null) {
			return;
		}
		boolean offer = bqw.offer(b);
		if (!offer) {
			log.log(10, "offer bqw fail");
			return;
		}
		b.stat = 3;
		wc.decrementAndGet();
		log.log(1, "in bqw", bqw);
	}

	public void giveupReadBuffer(BufferSimple b) {
		if (b == null) {
			return;
		}
		if (b.stat == 0) {
			return;
		}
		b.stat = 0;
		b.pos = 0;
		boolean offer = bqr.offerFirst(b);
		if (!offer) {
			log.log(10, "offer bqr fail");
			return;
		}
		rc.decrementAndGet();
		log.log(1, "giveup in bqr", bqr);
	}

	public void finishWriteBuffer(BufferSimple b) {
		if (b == null) {
			return;
		}
		if (b.stat == 0) {
			return;
		}
		b.stat = 0;
		b.pos = 0;
		boolean offer = bqr.offer(b);
		if (!offer) {
			log.log(10, "offer bqr fail");
			return;
		}
		rc.decrementAndGet();
		log.log(1, "in bqr", bqr);
	}

	public void giveupWriteBuffer(BufferSimple b) {
		if (b == null) {
			return;
		}
		if (b.stat == 3) {
			return;
		}
		b.stat = 3;
		boolean offer = bqw.offerFirst(b);
		if (!offer) {
			log.log(10, "offer bqr fail");
			return;
		}
		rc.decrementAndGet();
		log.log(1, "giveup in bqw", bqw);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
