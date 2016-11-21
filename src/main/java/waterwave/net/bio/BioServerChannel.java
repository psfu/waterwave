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
package waterwave.net.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import waterwave.common.buffer.BufferPool;
import waterwave.common.buffer.BufferSp;
import waterwave.net.bio.define.BioServerHandler;

public class BioServerChannel extends BioChannel{
	
	private final Socket s;
	private final BufferPool bp;
	private final BioServer bioServer;
	private BioServerHandler hanlder;
	public boolean isClose;
	
	public BioServerChannel(Socket s, BufferPool bp, BioServer bioServer) {
		super();
		this.s = s;
		this.bp = bp;
		this.bioServer = bioServer;
	}
	
	
//	private final BioServerDataDealer dealer;
//
//	public BioServerChannel(Socket s, BufferPool bp, BioServerDataDealer dealer) {
//		super();
//		this.s = s;
//		this.bp = bp;
//		this.dealer = dealer;
//	}

	public final BufferSp read() {
		InputStream is = null;
		BufferSp b = bp.getBuffer();
		try {
			is = s.getInputStream();

			int ri = 0;
			int length = b.size;

			int i = 0;
			for (;;) {
				
				int read = is.read(b.b, ri, (length - ri));
				ri += read;
				//log.log(1 "BioServerChannel: read: ", read);
				//log.log(1 "BioServerChannel: read count: ", ri);
				if (read <= 0) {
					if(i == 0) {
						this.close();
					}
					break;
				}
				
//				if ( read != 32 * 1024) {
//					break;
//				}
				if (ri == length) {
					log.log(9, "BioServerChannel: Read full !! ...... ", ri);
					break;
				}
				++i;
				
				//TODO
				if(true) {
					break;
				}
				
			}

			b.pos = ri;
			//log.log(1 "BioServerChannel: read buffer: ", b);
		} catch (IOException e) {
			e.printStackTrace();
			bp.giveupBuffer(b);
			//hanlder.serverOnError(this, e, b);
			//
			close();
		}

		return b;
	}

	public final void write(BufferSp b) {
		OutputStream or = null;
		try {
			or = s.getOutputStream();
			or.write(b.b, 0, b.pos);
			or.flush();
			//log.log(1 "BioServerChannel: write finish " + b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bp.giveupBuffer(b);
	}
	
	public final void close() {
		try {
			s.close();
			this.isClose = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
