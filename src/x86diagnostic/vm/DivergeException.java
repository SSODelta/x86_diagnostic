package x86diagnostic.vm;

public class DivergeException extends Throwable {
    private long limit;
    public DivergeException(long limit) {
        super("diverged. required >"+limit+" instructions");
    }
}
