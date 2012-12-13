package tagEditor;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyRandomAccessFile extends RandomAccessFile {
	public MyRandomAccessFile(String file, String mode) throws FileNotFoundException {
		super(file, mode);
	}
	
	/**
	 * Read a string of a specific length.
	 * @param len Length of the String to read
	 * @return
	 * @throws IOException
	 */
	public String readString(int len) throws IOException {
		byte[] b = new byte[len];
		this.read(b);
		
		return new String(b);
	}
	
	public byte[] readEOFString() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte b;
		for(b = this.readByte();0x00 != b;b = this.readByte()){
				buffer.write(b);
		}
		//secnod 00?
		b = this.readByte();
		if(b == 0x00)
			;
		else
			this.seek(this.getFilePointer() - 1);
		
		return buffer.toByteArray();
		
	}

	

}
