package shui.common.buffer;

import java.nio.ByteBuffer;

public class CommonBuffer {

	public final ByteBuffer b;
	public final BufferSimple bs;
	
	int r;

	public CommonBuffer(ByteBuffer b) {
		this.b = b;
		this.bs = null;
	}

	public CommonBuffer(BufferSimple bs) {
		this.b = null;
		this.bs = bs;
	}

}
