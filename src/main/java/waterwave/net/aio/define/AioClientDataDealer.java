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

package waterwave.net.aio;

import java.nio.ByteBuffer;

public interface AioClientDataDealer {

    
	void clientBeforeRead(AioClientChannel channel);
	void clientOnConnect(AioClientChannel channel);
	void clientOnData(AioClientChannel channel, ByteBuffer buffer, int result);
	void clientAfterWrite(AioClientChannel channel, ByteBuffer buffer, int bytes);
	void clientOnError(AioClientChannel channel,Throwable exc, ByteBuffer attachment);
    void clientOnClose(AioClientChannel channel);
	boolean clientAcceptsMessages();
}
