package tagEditor;


import java.nio.charset.Charset;


class Frame {
	Charset utf16charset= Charset.forName("UTF-16");
	Charset iso88591charset = Charset.forName("ISO-8859-1");
	Charset utf8charset = Charset.forName("UTF-8");
	Charset ascii = Charset.forName("ASCII");
	
	private String ID;
	private byte[] body;
	private short flags;
	private byte encodingflag;
	
	private byte[] MIMEType;
	private byte[] imageDescription;
	private byte pictureType;
	

	/**
	 * Will return the body of the frame according to the encoding-flag. Shouldn't be used for non-text-based frames.
	 */
	@Override
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

	public byte[] getMIMEType() {
		return MIMEType;
	}

	public byte[] getImageDescription() {
		return imageDescription;
	}

	public byte getPictureType() {
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

	public void setMIMEType(byte[] mIMEType) {
		MIMEType = mIMEType;
	}

	public void setImageDescription(byte[] imageDescription) {
		this.imageDescription = imageDescription;
	}

	public void setPictureType(byte pictureType) {
		this.pictureType = pictureType;
	}
	
	/**
	 * 
	 * @return the size of the frame in byte
	 */
	public int getSize(){
		if(this.ID.equals("APIC")){
			// Encoding flg + 2 eofs
			return 1 + MIMEType.length + 1 + 1 + imageDescription.length + 1 + body.length;
		} else {
			// beware of the encoding flag!
			return body.length +1;
		}
	}
}

