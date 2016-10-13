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

import java.nio.ByteBuffer;

import waterwave.common.buffer.BufferSp;

public interface BioServerDataDealer {
	void serverOnConnect(BioServerChannel c);
	void serverBeforeRead(BioServerChannel channel);
	void serverOnData(BioServerChannel c, BufferSp b);
	void serverAfterWrite(BioServerChannel c, BufferSp b, int count);
	void serverOnError(BioServerChannel c,Throwable e, ByteBuffer b);
	void serverOnClose(BioServerChannel channel);
	
}
