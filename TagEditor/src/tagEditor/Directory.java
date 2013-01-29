package tagEditor;

import java.nio.file.Paths;

/** This class represents a directory
 */
class Directory {
	String path;
	
	
	/**Constructor-Method of the directory, takes the name as parameter
	 * 
	 * @param name The name of the directory
	 */
	public Directory(String path) {
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}
	
	@Override
	public String toString() {
		return Paths.get(this.path).getFileName().toString();
	}
}
