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

public class BufferSp {

	public int size;
	public int pos = 0;
	public int id = 0;
	public int stat = 0;

	public byte[] b;

	public long originDataPosEnd = 0;
	public long originDataPosStart = 0;
	public String originDataDesc;

	public BufferSp(int size) {
		this.size = size;
		this.b = new byte[size];
	}

	public BufferSp(int id, int size) {
		this.id = id;
		this.size = size;
		this.b = new byte[size];
	}

	public byte[] getBytes() {
		return b;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		//
		return "id:" + id + ", pos:" + pos + " " ;
	}

	BufferSp() {
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
