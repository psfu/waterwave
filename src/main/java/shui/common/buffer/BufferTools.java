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

import java.nio.ByteBuffer;

public class BufferTools {

	private final static BufferPoolNIO bp = new BufferPoolNIO(2 * 1024, 32 * 1024);
	
	public final static ByteBuffer getBuffer() {
		//ByteBuffer input = ByteBuffer.allocate(16 * 1024);
		ByteBuffer input = bp.allocate();
		return input;
	}

	public final static void returnBuffer(ByteBuffer buffer) {
		bp.recycle(buffer);
		
	}
	public final static byte[] getBuffer2Byte(ByteBuffer b) {
		int p = b.position();
		byte[] r = new byte[p];
		if (b.position() != 0) {
			b.flip();
		}
		b.get(r, 0, p);

		return r;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
