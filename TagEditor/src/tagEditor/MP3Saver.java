package tagEditor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class MP3Saver {
	Charset utf16charset= Charset.forName("UTF-16");
	Charset ascii = Charset.forName("ASCII");
	private RandomAccessFile s;
	
	public void saveMP3(MP3File mp3) throws IOException{
		s = new RandomAccessFile(mp3.getPath(), "r");
		byte[] music = new byte[(int) s.length() - mp3.getSize()];
		s.seek(mp3.getSize());
		s.read(music, 0, music.length);
 		mp3.setFrames();
 		RandomAccessFile sN = new RandomAccessFile(mp3.getPath() + "-2", "rw");
 		// write ID3 version
 		sN.write("ID3".getBytes(ascii));
 		// write main and subversion
 		sN.write(new byte[]{3,0});
 		//write flag, our files don't have flags
 		sN.write(new byte[]{0});
 		//write tagsize
 		sN.writeInt(intToTag(mp3.getCalculatedSize()));
 		//write all the frames!!!!
 		for(Frame f : mp3.getFrames()){
 			//write ID
 			sN.write(f.getID().getBytes(ascii));
 			//write size
 			sN.writeInt(f.getSize());
 			sN.writeShort(f.getFlags());
 			sN.write(f.getEncodingflag());
 			if(f.getID().equals("APIC")){
 				sN.write(f.getMIMEType().getBytes(ascii));
 				sN.write(new byte[]{0});
 				
 				sN.write(f.getPictureType());
 				sN.write(f.getImageDescription().getBytes(utf16charset));
 				sN.write(new byte[]{0});
 			}
 			sN.write(f.getBody());
 		}
 		//write the music
 		sN.write(music);
 		sN.close();
 		s.close();		
	}
	private int intToTag(int size){
		return ((size & 0x0000007F)) | ((size & 0x00003F80 ) << 1) | ((size & 0x001FC000) << 2) | ((size & 0x0FE00000) << 3);
	}

	
}