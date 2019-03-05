package x86diagnostic.vm;

public enum DataType {

    BYTE("b","byte",1), WORD("w","word",2), LONG("l","long",4), QUAD("q","quad",8);

    public static boolean isDataType(String str){
        for(DataType t : values())
            if(t.suffix.equals(str))
                return true;
        return false;
    }

    private int bytes;
    private String suffix, full;
    DataType(String str, String full, int bytes){
        suffix = str;
        this.full = full;
        this.bytes = bytes;
    }
    public byte[] arr(long x){
        byte[] b = new byte[bytes];
        for(int i=0; i<bytes; i++) {
            b[bytes - 1 - i] = (byte)(x % 256);
            x /= 256;
        }
        return b;
    }
    public String full() { return full; }
    public int bytes(){
        return bytes;
    }
    public String toString(){
        return suffix;
    }
}
