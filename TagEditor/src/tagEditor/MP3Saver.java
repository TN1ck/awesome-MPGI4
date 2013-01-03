package tagEditor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MP3Saver {
	Charset utf16charset= Charset.forName("UTF-16");
	Charset ascii = Charset.forName("ASCII");
	/** Overwrites the mp3 with the values of the mp3 model
	 * 
	 * @param mp3 - the mp3, which shall be saved
	 * @param newFile - this is a optional Parameter to rename a File
	 * 
	 * @throws IOException
	 */
	public void saveMP3(MP3File mp3) throws IOException{
		mp3.setFrames(); // set the frames 	
		
		FileChannel s = FileChannel.open(Paths.get(mp3.getPath()), StandardOpenOption.DELETE_ON_CLOSE);
		
		byte[] music = new byte[(int) s.size() - (mp3.getSize() +10)]; // create array for the music
		s.position(mp3.getSize() + 10); // set the position
		s.read(ByteBuffer.wrap(music)); // read the music
		s.close(); //will also delete the old file
		
 		RandomAccessFile sN = new RandomAccessFile(mp3.getPath(), "rw");
 		sN.write("ID3".getBytes(ascii)); // write ID3 version
 		sN.write(new byte[]{3,0}); // write main and subversion	
 		sN.write(new byte[]{0}); // write flag, our files don't have flags	
 		sN.writeInt(intToTag(mp3.getCalculatedSize()));  // write tagsize
 		 // write all the frames!!!!
 		for(Frame f : mp3.getFrames()){
 			sN.write(f.getID().getBytes(ascii)); // write ID
 			sN.writeInt(f.getSize()); // write size	
 			sN.writeShort(f.getFlags()); // write flags
 			sN.write(f.getEncodingflag()); // write the encoding of the text
 			if(f.getID().equals("APIC")){
 				sN.write(f.getMIMEType()); // write MIME-type (image/jpg or image/png)
 				sN.write(new byte[]{0}); // EOF
 				sN.write(f.getPictureType()); // specifies if the picture is an cover or something else
 				sN.write(f.getImageDescription()); // write the image description
 				sN.write(new byte[]{0}); // EOF
 			}
 			sN.write(f.getBody());
 		}
 		sN.write(music); //write the music
 		sN.close();	
	}
	/**
	 * 
	 * @param size - the size of the MP3 in a normal int
	 * @return the size of the MP3 File according to id3v2, every seventh bit is 0
	 */
	private int intToTag(int size){
		return ((size & 0x0000007F)) | ((size & 0x00003F80 ) << 1) | ((size & 0x001FC000) << 2) | ((size & 0x0FE00000) << 3);
	}

	
}