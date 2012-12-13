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
	
	private MyRandomAccessFile s;

	
	private int TagToInt(byte[] bytes){
		return (bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 7 ) | ((bytes[1] & 0xFF) << 14 ) | ((bytes[0] & 0xFF) << 21 );
	}
	
	public MP3File readMP3(Path filePath) throws IOException{
		s = new MyRandomAccessFile(filePath.toString(), "r");	
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
		int currentFrameSize;
		long position;
		
		ArrayList<Frame> frames = new ArrayList<Frame>();
		while(s.getFilePointer() < size){
			if(s.readByte() != 0x00) {// check if valid frame or NULL area
				s.seek(s.getFilePointer() - 1); // reset fp from previous check	
				Frame f = new Frame();
				//read the FrameID
				f.setID(s.readString(4));
				currentFrameSize = s.readInt(); // read the frame-size
				f.setFlags(s.readShort()); // read and set the flags, should throw an exception when it's not 0
				position = s.getFilePointer();
				f.setEncodingflag(s.readByte()); // read encoding-flag
				if(f.getID().equals("APIC")) {
					// Do we have flags? If so, break
					if(f.getEncodingflag() != 0){
						s.seek(s.getFilePointer() + currentFrameSize -1);
						break;
					}
					// read MIME-Type
					f.setMIMEType(s.readEOFString());
					//read picture-type
					f.setPictureType(s.readByte());
					//read picture-description
					f.setImageDescription(s.readEOFString());
					//read body
					f.setBody(new byte[currentFrameSize - (int) (s.getFilePointer() - position)]);
				}
				else{
					f.setBody(new byte[currentFrameSize -1]);
				}
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

}
