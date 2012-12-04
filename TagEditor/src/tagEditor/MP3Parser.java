package tagEditor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.String;

public class MP3Parser {

	/**
	 * @param args
	 */
	byte[] mp3ByteFile;

	Charset utf16charset= Charset.forName("UTF-16");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	Charset utf8charset = Charset.forName("UTF-8");

	
	private int TagToInt(byte[] bytes){
	
		return (bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 7 ) | ((bytes[1] & 0xFF) << 14 ) | ((bytes[0] & 0xFF) << 21 );
	}
	private int FrameToInt(byte[] bytes){
		return 10+(bytes[3] & 0xFF) | ((bytes[2] & 0xFF) << 8 ) | ((bytes[1] & 0xFF) << 16 ) | ((bytes[0] & 0xFF) << 24 );
		
	}
	
	public MP3Parser(){
		
		
	}
	
	
	public MP3File readMP3(Path pathToFile){
		byte[] ID = new byte[3];
		byte[] version = new byte[2];
		byte[] flags = new byte[1];
		byte[] byteSize = new byte[4];
		MP3File mp3 = new MP3File();
		int tagSize;
		
		try{
			mp3ByteFile = Files.readAllBytes(pathToFile);
		} catch(IOException e){
			System.out.println(e.getStackTrace());
		}
		for(int i = 0; i< 3;i++){
			ID[i] = mp3ByteFile[i];
		} 
		for(int i = 3; i< 5;i++){
			version[i -3] = mp3ByteFile[i];
		}
		flags[0] = mp3ByteFile[5];
		for(int i = 6; i< 10;i++){
			byteSize[i -6] = mp3ByteFile[i];
		}	
		tagSize = TagToInt(byteSize);
		setData(tagSize,mp3);
		return mp3;
		
	}
	

	private void setData(int tagSize, MP3File mp3){
		
		for(int i = 10; i < tagSize;){
			byte[] frameHeader = new byte[4];
			byte[] temp = new byte[4];
			int frameSize = 0;
			Charset charset = iso88591charset;
			for(int j = i;j< i+4;j++){
				frameHeader[j -i] = mp3ByteFile[j];
			} 
			if(new String(frameHeader).equals("TPE1")){
				int currentPos = i+4;
				for(int j = currentPos;j< currentPos+4;j++){
					temp[j -currentPos] = mp3ByteFile[j];
				}
				frameSize = FrameToInt(temp);
				currentPos += 6;
				if(mp3ByteFile[currentPos+1] == -1 && mp3ByteFile[currentPos+2] == -2){
					charset = iso88591charset;
					currentPos += 3;
				}
				byte[] frameData = new byte[frameSize];
				for(int j = currentPos; j <i+frameSize;j++){
						frameData[j - currentPos] = mp3ByteFile[j];		
				}
				mp3.setArtist(new String(frameData,charset));
				i += frameSize;
			}
			else if(new String(frameHeader).equals("TIT2")){
				int currentPos = i+4;
				for(int j = currentPos;j< currentPos+4;j++){
					temp[j -currentPos] = mp3ByteFile[j];
				}
				frameSize = FrameToInt(temp);
				currentPos += 6;
				if(mp3ByteFile[currentPos+1] == -1 && mp3ByteFile[currentPos+2] == -2){
					charset = iso88591charset;
					currentPos += 3;
				}
				byte[] frameData = new byte[frameSize];
				for(int j = currentPos; j <i+frameSize;j++){
						frameData[j - currentPos] = mp3ByteFile[j];		
				}
				mp3.setSong(new String(frameData,charset));
				i += frameSize;
			}
			else if(new String(frameHeader).equals("TALB")){
				int currentPos = i+4;
				for(int j = currentPos;j< currentPos+4;j++){
					temp[j -currentPos] = mp3ByteFile[j];
				}
				frameSize = FrameToInt(temp);
				currentPos += 6;
				if(mp3ByteFile[currentPos+1] == -1 && mp3ByteFile[currentPos+2] == -2){
					charset = iso88591charset;
					currentPos += 3;
				}
				byte[] frameData = new byte[frameSize];
				for(int j = currentPos; j <i+frameSize;j++){
						frameData[j - currentPos] = mp3ByteFile[j];		
				}
				mp3.setAlbum(new String(frameData,charset));
				i += frameSize;
			}
			else if(new String(frameHeader).equals("TYER")){
				int currentPos = i+4;
				for(int j = currentPos;j< currentPos+4;j++){
					temp[j -currentPos] = mp3ByteFile[j];
				}
				frameSize = FrameToInt(temp);
				currentPos += 6;
				if(mp3ByteFile[currentPos+1] == -1 && mp3ByteFile[currentPos+2] == -2){
					charset = iso88591charset;
					currentPos += 3;
				}
				byte[] frameData = new byte[frameSize];
				for(int j = currentPos; j <i+frameSize;j++){
						frameData[j - currentPos] = mp3ByteFile[j];		
				}
				mp3.setYear(new String(frameData,charset));
				i += frameSize;
			}
			else if(new String(frameHeader).equals("APIC")){
				int currentPos = i+4;
				for(int j = currentPos;j< currentPos+4;j++){
					temp[j -currentPos] = mp3ByteFile[j];
				}
				frameSize = FrameToInt(temp);
				currentPos += 6;
				currentPos++;
				byte[] frameData = new byte[frameSize];
				for(int k = currentPos;0x00 != mp3ByteFile[k];k++){
					currentPos++;
				}
				currentPos += 2;
				for(int k = currentPos;0x00 != mp3ByteFile[k];k++){
					currentPos++;
				}
				currentPos++;
				for(int j = currentPos; j <i+frameSize;j++){
						frameData[j - currentPos] = mp3ByteFile[j];		
				}
				mp3.setCover(frameData);
				i += frameSize;
			} else {
				i++;
			}
				
		}
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MP3Parser TestParser = new MP3Parser();
		MP3File mp3 = new MP3File();
		mp3 = TestParser.readMP3(Paths.get("/Users/Tom/Dropbox/3 Semester/MPGI 4/Mp3 Sammlung/The Whind Whistles/Animals are people too/01_Turtle.mp3"));
		System.out.println(mp3.getSong());
	}

}
