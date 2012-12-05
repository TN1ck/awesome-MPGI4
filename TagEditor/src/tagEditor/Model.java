package tagEditor;


import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


/** This class represents a MP3-File*/
class MP3File {
	
	private String artist;
	private String song;
	private String album;
	private String year;
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
		return size;
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
	}
	
	public MP3File(){
		
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
	public String ID;
	public int size;
	public byte[] body;
	public short flags;
	public byte encodingflag;
	
	public String MIMEType;
	public String imageDescription;
	public byte[] pictureType;
	
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
}

