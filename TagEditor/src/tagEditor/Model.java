package tagEditor;


import java.awt.image.BufferedImage;

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
	private BufferedImage cover;
	
	
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
	
	public BufferedImage getCover() {
		return cover;
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
	public MP3File(String artist, String song, String album, String year, BufferedImage cover) {
		this.artist = artist;
		this.song = song;
		this.album = album;
		this.year = year;
		this.cover = cover;
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
