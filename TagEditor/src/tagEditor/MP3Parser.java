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
				f.setID(new String(temp2));
				System.out.println(f.getID());
				int currentFrameSize = s.readInt();
				f.setFlags(s.readShort());
				if(f.getID().equals("APIC")) {
					int size2 = 0;
					f.setEncodingflag(s.readByte());
					size2++;
					String temp = new String();
					for(byte b = s.readByte();0x00 != b;b = s.readByte()){
							temp += new String(new byte[]{b});
							size2++;
					} f.setMIMEType(temp);
					size2++;
					byte[] tempByte = new byte[1];
					s.read(tempByte);
					f.setPictureType(tempByte);
					size2++;
					temp = "";
					for(byte b = s.readByte();0x00 != b;b = s.readByte()){
						temp += new String(new byte[]{b});
						size2++;
					} f.setImageDescription(temp);
					size2++;
					System.out.println(currentFrameSize - (size2));
					f.setBody(new byte[currentFrameSize - (size2)]);
					if( f.getBody().length + 1 + f.getMIMEType().length() + 1 + f.getPictureType().length + f.getImageDescription().length() + 1 != currentFrameSize)
						System.out.println("wtf");
				}
				else{
					f.setEncodingflag(s.readByte());
					if(currentFrameSize > 1)
						f.setBody(new byte[currentFrameSize -1]);
				}
				if(f.getBody() != null)
					s.read(f.getBody());
				
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
			if(f.getID().equals("TPE1"))
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
			if(f.getID().equals("TALB"))
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
			if(f.getID().equals("TIT2"))
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
			if(f.getID().equals("TYER"))
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
			if(f.getID().equals("APIC"))
				return f.getBody();
		}
		
		return null;
	}


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MP3Parser TestParser = new MP3Parser();
		MP3File mp3 = new MP3File();
		try {
			mp3 = TestParser.readMP3(Paths.get("/home/tom/Dropbox/3 Semester/MPGI 4/The Whind Whistles/Animals are people too/01_Turtle.mp3"));
			mp3.setPath("/home/tom/Dropbox/3 Semester/MPGI 4/The Whind Whistles/Animals are people too/01_Turtle.mp3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(mp3.getAlbum());
		System.out.println(mp3.getSong());
		System.out.println(mp3.getArtist());
		System.out.println(mp3.getYear());
		mp3.setSong("oeueu");
		MP3Saver testSaver = new MP3Saver();
		try {
			testSaver.saveMP3(mp3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
