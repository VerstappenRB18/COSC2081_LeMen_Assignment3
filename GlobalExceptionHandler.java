public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Caught " + e);
        System.out.println("Caught exception in thread: " + t.getName());
        e.printStackTrace();
    }
}