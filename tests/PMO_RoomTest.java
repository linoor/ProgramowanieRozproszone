
public class PMO_RoomTest {

	public static int test() {
		synchronized (PMO_RoomTest.class) {
			try {
				return 1;
			}
			finally {
				System.out.println("sekcja finally");
				PMO_RoomTest.class.notifyAll();
			}			
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		Thread th = new Thread( new Runnable() {
			
			@Override
			public void run() {
				synchronized ( PMO_RoomTest.class ) {
					try {
						PMO_RoomTest.class.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println( "Po zakonczeniu metody");
				}
			}
		});
		th.start();
		Thread.sleep( 100 );
		
		System.out.println( test() );
	}
	
}
