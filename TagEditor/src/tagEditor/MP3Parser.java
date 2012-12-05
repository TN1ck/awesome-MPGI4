package tagEditor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.lang.String;

public class MP3Parser {

	/**
	 * @param args
	 */
	Charset utf16charset= Charset.forName("UTF-16");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	Charset utf8charset = Charset.forName("UTF-8");
	
	private RandomAccessFile s;

	
	private int TagToInt(byte[] bytes){
	
		return (bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 7 ) | ((bytes[1] & 0xFF) << 14 ) | ((bytes[0] & 0xFF) << 21 );
	}
	
	private int FrameToInt(byte[] bytes){
		return 10+(bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 8 ) | ((bytes[1] & 0xFF) << 16 ) | ((bytes[0] & 0xFF) << 24 );
		
	}
	
	public MP3Parser(){
			
	}
	
	public MP3File readMP3(Path filePath) throws IOException{
		s = new RandomAccessFile(filePath.toString(), "r");	
		byte[] temp = new byte[3];
		s.read(temp);
		String IDString = new String(temp);
		MP3File mp3 = new MP3File();
		
		if(IDString.equals("ID3")){
			byte mainversion = s.readByte();
			byte subversion = s.readByte();
			if(mainversion == 3 && subversion == 0){
				byte flags = s.readByte();	
				if(flags == 0){
					byte[] id3Size = new byte[4]; 
					s.read(id3Size);			
					mp3.setSize(TagToInt(id3Size));
					ArrayList<Frame> frames = parseFrames(mp3.getSize());
					mp3.setFrames(frames);
					mp3.setAlbum(getAlbum(frames));
					mp3.setArtist(getArtist(frames));
					mp3.setCover(getCover(frames));
					mp3.setSong(getSong(frames));
					mp3.setYear(getYear(frames));
				} 
				else
					throw(new IOException("ID3 flags are set!"));
			} 
			else
				throw(new IOException("Invalid ID3 version!"));
		} 
		else
			throw(new IOException("Invalid or corrupted MP3 file!"));
		
		return mp3;
		
	}
	
	
	private ArrayList<Frame> parseFrames(int size) throws IOException
	{
		ArrayList<Frame> frames = new ArrayList<Frame>();
		while(s.getFilePointer() < size){
			if(s.readByte() != 0x00) {// check if valid frame or NULL area
				s.seek(s.getFilePointer() - 1); // reset fp from previous check
				
				Frame f = new Frame();
				
				byte[] temp2 = new byte[4];
				s.read(temp2);
				f.ID = new String(temp2);
				if(!f.ID.matches("\\w{4}"))
					break;
				f.size = s.readInt();
				f.flags = s.readShort();
				if(f.ID.equals("APIC")) {
					int size2 = 0;
					f.encodingflag = s.readByte();
					size2++;
					for(byte b = s.readByte();0x00 != b;b = s.readByte()){
							f.MIMEType += new String(new byte[]{b});
							size2++;
					}			
					s.read(f.pictureType);
					size2++;
					for(byte b = s.readByte();0x00 != b;b = s.readByte()){
						f.imageDescription += new String(new byte[]{b});
						size2++;
					}
					System.out.println(f.imageDescription);
					f.body = new byte[f.size - (size2 +2)];
					
				}
				else{
					f.encodingflag = s.readByte();
					if(f.size > 1)
						f.body = new byte[f.size -1];
				}
				if(f.body != null)
					s.read(f.body);
				
				frames.add(f);
			}
		}
		return frames;
	}
	

	
	/**
	 * Get parsed frame objects.
	 * @return
	 */
	public ArrayList<Frame> getFrames(ArrayList<Frame> frames){
		return frames;
	}
	
	/**
	 * Search through frames for the artist.
	 * @return Artist name or empty string if not specified.
	 */
	public String getArtist(	ArrayList<Frame> frames){
		for(Frame f : frames)
		{
			if(f.ID.equals("TPE1"))
				return f.toString();
		}
		
		return "";
	}
	
	/**
	 * Search through frames for the album name.
	 * @return Album name or empty string if not specified.
	 */
	public String getAlbum(	ArrayList<Frame> frames){
		for(Frame f : frames)
		{
			if(f.ID.equals("TALB"))
				return f.toString();
		}
		
		return "";
	}
	
	/**
	 * Search through frames for the track title.
	 * @return Track name or empty string if not specified.
	 */
	public String getSong(	ArrayList<Frame> frames){
		for(Frame f : frames)
		{
			if(f.ID.equals("TIT2"))
				return f.toString();
		}
		
		return "";
	}
	
	/**
	 * Search through frames for the year.
	 * @return Year or empty string if not specified.
	 */
	public String getYear(ArrayList<Frame> frames){
		for(Frame f : frames)
		{
			if(f.ID.equals("TYER"))
				return f.toString();
		}
		
		return "";
	}
	
	/**
	 * Search through frames for the album cover.
	 * @return Image data array or null if not specified.
	 */
	public byte[] getCover(	ArrayList<Frame> frames){
		for(Frame f : frames)
		{
			if(f.ID.equals("APIC"))
				return f.body;
		}
		
		return null;
	}


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MP3Parser TestParser = new MP3Parser();
		MP3File mp3 = new MP3File();
		try {
			mp3 = TestParser.readMP3(Paths.get("/Users/Tom/Dropbox/3 Semester/MPGI 4/The Whind Whistles/Animals are people too/01_Turtle.mp3"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(mp3.getAlbum());
		System.out.println(mp3.getSong());
		System.out.println(mp3.getArtist());
		System.out.println(mp3.getYear());
	}

}
