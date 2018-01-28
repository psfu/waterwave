package waterwave.net.bio.define;

import shui.common.buffer.BufferSimple;

public interface BioChannelI {

	public void write(BufferSimple b);

	public BufferSimple read();

	public void close();

}
