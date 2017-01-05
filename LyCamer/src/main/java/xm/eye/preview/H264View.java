package xm.eye.preview;

public class H264View{
	static {
		System.loadLibrary("H264Android2");
	}

	public native int InitDecoder(int width, int height, int rate);
	public native int UninitDecoder();
	public native int DecoderNal(byte[] in, int insize, byte[] out);

}