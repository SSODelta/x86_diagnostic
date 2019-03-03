package oram.vm;

public enum DataType {

    BYTE("b",1), WORD("w",2), LONG("l",4), QUAD("q",8);

    public static boolean isDataType(String str){
        for(DataType t : values())
            if(t.suffix.equals(str))
                return true;
        return false;
    }

    private int bytes;
    private String suffix;
    DataType(String str, int bytes){
        suffix = str;
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
    public int bytes(){
        return bytes;
    }
    public String toString(){
        return suffix;
    }
}
