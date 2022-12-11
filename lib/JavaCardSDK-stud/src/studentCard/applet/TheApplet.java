package applet;


import javacard.framework.*;




public class TheApplet extends Applet {


	static final byte UPDATECARDKEY				= (byte)0x14;
	static final byte UNCIPHERFILEBYCARD			= (byte)0x13;
	static final byte CIPHERFILEBYCARD			= (byte)0x12;
	static final byte CIPHERANDUNCIPHERNAMEBYCARD		= (byte)0x11;
	static final byte READFILEFROMCARD			= (byte)0x10;
	static final byte WRITEFILETOCARD			= (byte)0x09;
	static final byte UPDATEWRITEPIN			= (byte)0x08;
	static final byte UPDATEREADPIN				= (byte)0x07;
	static final byte DISPLAYPINSECURITY			= (byte)0x06;
	static final byte DESACTIVATEACTIVATEPINSECURITY	= (byte)0x05;
	static final byte ENTERREADPIN				= (byte)0x04;
	static final byte ENTERWRITEPIN				= (byte)0x03;
	static final byte READNAMEFROMCARD			= (byte)0x02;
	static final byte WRITENAMETOCARD			= (byte)0x01;
	final static short NVRSIZE      = (short)1024;
	final static short SW_VERIFIACTION_FAILED 			= 0x6300;
	static byte[] NVR                          = new byte[NVRSIZE];
	OwnerPIN pinWrite ;
	OwnerPIN pinRead ;
	
	protected TheApplet() {
		
	    byte[] pincodeWrite = {(byte)0x30,(byte)0x30,(byte)0x30,(byte)0x30}; // PIN code "0000"
	    byte[] pincodeRead 	= {(byte)0x31,(byte)0x31,(byte)0x31,(byte)0x31}; // PIN code "1111"
		pinWrite = new OwnerPIN((byte)3,(byte)8);  				// 3 tries 8=Max Size
		pinWrite.update(pincodeWrite,(short)0,(byte)4); 	
		pinRead = new OwnerPIN((byte)3,(byte)8);  				// 3 tries 8=Max Size
		pinRead.update(pincodeRead,(short)0,(byte)4); 
		
		this.register();
	}


	public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {
		new theApplet();
	} 


	public boolean select() {
		return true;
	} 


	public void deselect() {
	}


	public void process(APDU apdu) throws ISOException {
		if( selectingApplet() == true )
			return;

		byte[] buffer = apdu.getBuffer();

		switch( buffer[1] ) 	{
			case UPDATECARDKEY: updateCardKey( apdu ); break;
			case UNCIPHERFILEBYCARD: uncipherFileByCard( apdu ); break;
			case CIPHERFILEBYCARD: cipherFileByCard( apdu ); break;
			case CIPHERANDUNCIPHERNAMEBYCARD: cipherAndUncipherNameByCard( apdu ); break;
			case READFILEFROMCARD: readFileFromCard( apdu ); break;
			case WRITEFILETOCARD: writeFileToCard( apdu ); break;
			case UPDATEWRITEPIN: updateWritePIN( apdu ); break;
			case UPDATEREADPIN: updateReadPIN( apdu ); break;
			case DISPLAYPINSECURITY: displayPINSecurity( apdu ); break;
			case DESACTIVATEACTIVATEPINSECURITY: desactivateActivatePINSecurity( apdu ); break;
			case ENTERREADPIN: enterReadPIN( apdu ); break;
			case ENTERWRITEPIN: enterWritePIN( apdu ); break;
			case READNAMEFROMCARD: readNameFromCard( apdu ); break;
			case WRITENAMETOCARD: writeNameToCard( apdu ); break;
			default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}


	void updateCardKey( APDU apdu ) {
	}


	void uncipherFileByCard( APDU apdu ) {
	}


	void cipherFileByCard( APDU apdu ) {
	}


	void cipherAndUncipherNameByCard( APDU apdu ) {
	}


	void readFileFromCard( APDU apdu ) {
	}


	void writeFileToCard( APDU apdu ) {
	}


	void updateWritePIN( APDU apdu ) {
	}


	void updateReadPIN( APDU apdu ) {
	}


	void displayPINSecurity( APDU apdu ) {
	}


	void desactivateActivatePINSecurity( APDU apdu ) {
	}


	void enterReadPIN( APDU apdu ) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		if (!pinRead.check ( buffer ,(byte)5,buffer[(byte)4]))
			ISOException.throwIt( SW_VERIFIACTION_FAILED);
	}


	void enterWritePIN( APDU apdu ) {
		apdu.setIncomingAndReceive();
		byte[] buffer = apdu.getBuffer();
		if (!pinWrite.check ( buffer ,(byte)5,buffer[(byte)4]))
			ISOException.throwIt( SW_VERIFIACTION_FAILED);
	}


	void readNameFromCard( APDU apdu ) {
		byte[] buffer = apdu.getBuffer();
		Util.arrayCopy(NVR,(byte)1,buffer,(byte)0,NVR[0]);
		apdu.setOutgoingAndSend((short)0,NVR[0]);
	}


	void writeNameToCard( APDU apdu ) {
		byte[] buffer = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		Util.arrayCopy(buffer,(byte)4,NVR,(byte)0,(short)(buffer[4]+1));
	}


}
