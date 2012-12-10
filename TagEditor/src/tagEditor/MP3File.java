package tagEditor;

import java.nio.charset.Charset;
import java.util.ArrayList;

/** This class represents a MP3-File*/
class MP3File {
	
	Charset utf16charset= Charset.forName("UTF-16");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	Charset utf8charset = Charset.forName("UTF-8");
	
	private String artist;
	private String song;
	private String album;
	private String year;
	private String path;
	//needed to read the music
	private int tagSize;
	private ArrayList<Frame> frames;
	private byte[] cover;
	
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String newArtist) {
		artist = newArtist;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String newSong) {
		song = newSong;
	}
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String newAlbum) {
		album = newAlbum;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String newYear) {
		year = newYear;
	}
	
	public byte[] getCover() {
		return cover;
	}
	
	public void setCover(byte[] newCover) {
		cover = newCover;
	}
	
	public void setFrames(ArrayList<Frame> frames) {
		this.frames = frames;
	}
	public void setSize(int size) {
		this.tagSize = size;
	}
	public int getSize() {
		return this.tagSize;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	
	/**
	 * 
	 * @return the current size of the MP3, needed for saving
	 */
	public int getCalculatedSize() {
		int size = 0;
		for(Frame f : this.frames){
			size += f.getSize() + 10;
		}
		return size;
	}
	
	public ArrayList<Frame> getFrames() {
		return frames;
	}
	
	/**
	 * Overwrites the content of the frames with the variables of the current MP3File, should be done before saving
	 */
	public void setFrames(){
		for(Frame f: frames){
			if(f.getID().equals("APIC")){
				f.setBody(this.cover);
			}
			else if(f.getID().equals("TIT2")){
				System.out.println(f.getBody().length);
				f.setBody(this.song.getBytes(utf16charset));
				f.setEncodingflag((byte) 1);
			} 
			else if(f.getID().equals("TALB")){
				f.setBody(this.album.getBytes(utf16charset));
				f.setEncodingflag((byte) 1);
			}
			else if(f.getID().equals("TYER")){
				f.setBody(this.year.getBytes());
			}
			else if(f.getID().equals("TPE1")){
				f.setBody(this.artist.getBytes(utf16charset));
				f.setEncodingflag((byte) 1);
			}
		}
	}
	
	/**String representation of the file, needed for the Tree-UI
	 * 
	 * @return The artist and the title of the file
	 */
	
	public String toString(){
		return artist + " - " + song;
	}
}
