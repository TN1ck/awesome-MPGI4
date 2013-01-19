package tagEditor;

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
	
	@Override
	public String toString() {
		return name;
	}
}
