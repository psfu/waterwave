package waterwave.net;

import java.io.IOException;

import shuisea.common.buffer.CommonBuffer;

public interface Channel {
	public boolean close();
	
	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * 
	 * write 报错
	 * 
	 */
	public int write(final CommonBuffer in)  throws IOException;
	
	/**
	 * 
	 * @return
	 * 
	 * read 不报错？
	 * 
	 */
	public CommonBuffer read();
}
