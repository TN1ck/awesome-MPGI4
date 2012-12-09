package tagEditor;


import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


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
	private int size;
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
		this.size = size;
	}
	public int getSize() {
		return this.size;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	
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
	 * Constructor-Method for creating the meta-info of a mp3-file
	 * 
	 * @param artist The artist of the file
	 * @param title The title of the file
	 * @param album The album of the file
	 * @param year The year the file was recorded
	 * @param cover The cover of the album
	 */
	public MP3File(String artist, String song, String album, String year, byte[] cover, ArrayList<Frame> frames) {
		this.artist = artist;
		this.song = song;
		this.album = album;
		this.year = year;
		this.cover = cover;
		this.frames = frames;
		this.size = size;
		this.path = path;
	}
	
	public MP3File(){
		
	}
	
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
/** This class represents a directory
 */
class Directory {
	String name;
	
	/**Constructor-Method of the directory, takes the name as parameter
	 * 
	 * @param name The name of the directory
	 */
	public Directory(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}



class Frame {
	Charset utf16charset= Charset.forName("UTF-16");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	Charset utf8charset = Charset.forName("UTF-8");
	Charset ascii = Charset.forName("ASCII");
	
	
	private String ID;
	private byte[] body;
	private short flags;
	private byte encodingflag;
	
	private String MIMEType;
	private String imageDescription;
	private byte[] pictureType;
	
	public Frame(){
		this.MIMEType = new String();
		this.pictureType = new byte[1];
		this.imageDescription = new String();
	}
	
	public String toString() {
		try {
			if(this.encodingflag == 0) {
				return new String(body, "ISO-8859-1");
			}
			else {
				return new String(body, "UTF-16");	
			}
		}
		catch(Exception e) {
			return "Unsupported charset! " + e.toString();
		}
	}
	
	public String getID() {
		return ID;
	}

	public byte[] getBody() {
		return body;
	}

	public short getFlags() {
		return flags;
	}

	public byte getEncodingflag() {
		return encodingflag;
	}

	public String getMIMEType() {
		return MIMEType;
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public byte[] getPictureType() {
		return pictureType;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public void setFlags(short flags) {
		this.flags = flags;
	}

	public void setEncodingflag(byte encodingflag) {
		this.encodingflag = encodingflag;
	}

	public void setMIMEType(String mIMEType) {
		MIMEType = mIMEType;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public void setPictureType(byte[] pictureType) {
		this.pictureType = pictureType;
	}

	public int getSize(){
		if(this.ID.equals("APIC")){
			// Encoding flg + 2 eofs
			return 1 + MIMEType.length() + 1 + pictureType.length + imageDescription.length() + 1 + body.length;
		} else {
			// beware of the encoding flag!
			return body.length +1;
		}
	}
}

