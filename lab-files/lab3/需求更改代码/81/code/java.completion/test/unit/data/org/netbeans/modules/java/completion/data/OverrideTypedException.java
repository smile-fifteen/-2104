package test;
import java.io.IOException;

public class Test<T extends Throwable> {
    
    public void test() throws T {
        
    }
    
    public static class Test1 extends Test<IOException> {
        
    }
    
    public static class Test2<E extends RuntimeException> extends Test<E> {
        
    }
    
}
