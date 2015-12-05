import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicInteger;

public class PMO_StartTest {
	private static AtomicIntegerArray threadsCounter;
	private static AtomicIntegerArray maxThreadsCounter;
	private static AtomicInteger lastJobFinished;
	private static DelayQueue<PMO_TaskImplementation> tasksToSubmit;
	private static SystemInterface si;
	private static int tasksCounter;
	private static long taskAddMethodExecutionSum;
	private static List<PMO_TaskReport> reports;
	private static List<PMO_TaskImplementation> tasksList;
	private static AtomicBoolean tasksInsertThreadOK;
	private static List<NotBefore> notBeforeTests;

	static class NotBefore {
		private final int queue;
		private final int thisTaskID;
		private final int notBeforeThatTaskID;

		public NotBefore(int queue, int thisTaskID, int notBeforeThatTaskID) {
			this.queue = queue;
			this.thisTaskID = thisTaskID;
			this.notBeforeThatTaskID = notBeforeThatTaskID;
		}
	}

	private static void init(int queues) {
		threadsCounter = new AtomicIntegerArray(queues);
		maxThreadsCounter = new AtomicIntegerArray(queues);
		lastJobFinished = new AtomicInteger(0);
		tasksToSubmit = new DelayQueue<>();
		tasksCounter = 0;
		taskAddMethodExecutionSum = 0;
		reports = new ArrayList<>();
		tasksList = new ArrayList<>();
		tasksInsertThreadOK = new AtomicBoolean(true);
		notBeforeTests = new ArrayList<PMO_StartTest.NotBefore>();
	}

	private static int[] createTest1() {
		int[] threadsLimits = new int[] { 2, 2, 2, 2 };

		init(threadsLimits.length);
		si = new SystemExec();
		si.setNumberOfQueues(threadsLimits.length);
		si.setThreadsLimit(threadsLimits);
		PMO_TaskImplementation t01 = new PMO_TaskImplementation(0, 2, 1, true);
		PMO_TaskImplementation t02 = new PMO_TaskImplementation(0, 3, 2, true);
		PMO_TaskImplementation t03 = new PMO_TaskImplementation(1, 1, 3, true);
		t01.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 1000, 1000, 1000, -1 }, reports);
		t02.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 500, 500, 500, 500 }, reports);
		t03.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { -1, 250, -1, -1 }, reports);

		t01.setInitialDelay(1000);
		t02.setInitialDelay(1100);
		t03.setInitialDelay(1200);

		tasksToSubmit.add(t01);
		tasksToSubmit.add(t02);
		tasksToSubmit.add(t03);
		tasksList.add(t01);
		tasksList.add(t02);
		tasksList.add(t03);

		PMO_TaskImplementation tend = new PMO_TaskImplementation(-1, 2, 5, true);
		tend.setInitialDelay(1900);
		tasksToSubmit.add(tend);

		return threadsLimits;
	}

	private static int[] createTest2() {
		int[] threadsLimits = new int[] { 1, 2 };
		init(threadsLimits.length);
		si = new SystemExec();
		si.setNumberOfQueues(threadsLimits.length);
		si.setThreadsLimit(threadsLimits);
		PMO_TaskImplementation t00 = new PMO_TaskImplementation(0, 0, 0, true);

		// to zadanie nie przestrzega kolejnosci - nie moze ruszyc przed 3
		PMO_TaskImplementation t01 = new PMO_TaskImplementation(0, 1, 1, false);

		PMO_TaskImplementation t02 = new PMO_TaskImplementation(0, 0, 2, true);
		PMO_TaskImplementation t03 = new PMO_TaskImplementation(0, 0, 3, true);
		t00.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 500, 20000 }, reports);
		t01.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 400, 1000 }, reports);
		t02.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 300, 20000 }, reports);
		t03.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
				new long[] { 200, 20000 }, reports);

		t00.setInitialDelay(950);
		t01.setInitialDelay(1000);
		t02.setInitialDelay(1050);
		t03.setInitialDelay(1100);

		// zadanie 1 nie moze wystartowac przed zadaniem 2 i 3
		notBeforeTests.add(new NotBefore(0, 1, 2));
		notBeforeTests.add(new NotBefore(0, 1, 3));

		tasksToSubmit.add(t00);
		tasksToSubmit.add(t01);
		tasksToSubmit.add(t02);
		tasksToSubmit.add(t03);
		tasksList.add(t00);
		tasksList.add(t01);
		tasksList.add(t02);
		tasksList.add(t03);

		PMO_TaskImplementation tend = new PMO_TaskImplementation(-1, 2, 5, true);
		tend.setInitialDelay(1500);
		tasksToSubmit.add(tend);
		return threadsLimits;
	}

	private static void showThreadUtlizationReport(int[] threadsLimits) {
		for (int i = 0; i < threadsLimits.length; i++) {
			PMO_SOUT.println("Queue : " + i + " threads limit "
					+ threadsLimits[i] + " found " + maxThreadsCounter.get(i));
		}
	}

	private static Thread startTaskInsertThread() {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean flag = true;
				long t0, tf;
				TaskInterface ti = null;
				while (flag) {
					try {
						ti = tasksToSubmit.take();
					} catch (InterruptedException e) {
						flag = false;
					}
					if (ti != null) {
						if (ti.getFirstQueue() == -1) {
							PMO_SOUT.println("Last task");
							break; // druga metoda zakoczenia przesylania zadan
						}
						PMO_SOUT.println("Task " + ti.getTaskID()
								+ " ready to submit");
						t0 = System.currentTimeMillis();
						si.addTask(ti);
						tf = System.currentTimeMillis();
						PMO_SOUT.println("Task " + ti.getTaskID()
								+ " submitted @ " + tf);
						tasksCounter++;
						taskAddMethodExecutionSum += tf - t0;
					}
				}
				PMO_SOUT.println("Tasks inserter thread has ended its job. Jobs submitted # "
						+ tasksCounter);

				if (tasksCounter != tasksList.size()) {
					PMO_SOUT.println("Blad w dodawaniu zadan : nie zgadza sie liczba zadan counter = "
							+ tasksCounter
							+ " rozmiar listy zadan "
							+ tasksList.size());
					tasksInsertThreadOK.set(false);
				}

				if (tasksCounter == 0) {
					PMO_SOUT.println("Blad w dodawaniu zadan : nie dodano zadnego zadania");
					tasksInsertThreadOK.set(false);
				} else {
					long avg = taskAddMethodExecutionSum / tasksCounter;
					if (avg > 100) {
						PMO_SOUT.println("BLAD: Sredni czas dodania zadania za dlugi "
								+ avg + " msec");
						tasksInsertThreadOK.set(false);
					} else {
						PMO_SOUT.println("Sredni czas dodania zadania do Systemu to "
								+ avg + " msec");
					}
				}
			}
		});
		th.setDaemon(true);
		th.start();
		return th;
	}

	private static boolean tasksOK() {
		boolean result = true;

		for (PMO_TaskImplementation ti : tasksList) {
			result &= ti.report();
		}

		return result;
	}

	private static boolean tasksInsertOK() {
		if (!tasksInsertThreadOK.get()) {
			PMO_SOUT.println("** Watek dodajacy zadania do systemu zglosil blad");
		}
		return tasksInsertThreadOK.get();
	}

	private static void showTasksTimeReport() {
		
		if ( reports.size() > 25 ) {
			PMO_SOUT.println( "Wykryto ponad 25 raportow zadan - wydruk raportu bedzie ograniczony");
		}
		
		long start = Long.MAX_VALUE;
		for (PMO_TaskReport report : reports) {
			if (start > report.startTime)
				start = report.startTime;
		}
		int counter = 0;
		for (PMO_TaskReport report : reports) {
			if ( counter++ < 25 )
				PMO_SOUT.println(report.toString(start));
		}
	}

	private static boolean notBeforeTest() {

		if (notBeforeTests.size() == 0)
			return true;

		for (NotBefore nb : notBeforeTests) {
			boolean thisTaskStarted = false;

			for (PMO_TaskReport report : reports) {
				
				if ( report.queue == nb.queue ) {
					if ((nb.notBeforeThatTaskID == report.jobID) && thisTaskStarted) {
						PMO_SOUT.println("Blad krytyczny: wykryto bledna kolejnosc zadan w kolejce " + nb.queue );
						PMO_SOUT.println("Blad krytyczny: zadanie " + nb.thisTaskID +
								" uruchomiono przed zadaniem " + nb.notBeforeThatTaskID );
						return false;
					}

					if ((nb.thisTaskID == report.jobID)) {
						thisTaskStarted = true;
					}					
				}
				
			}
		}

		return true;
	}

	private static void test1() {
		PMO_SOUT.println("Start testu 1");

		int threadsLimits[] = createTest1();
		startTaskInsertThread();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		showTasksTimeReport();
		showThreadUtlizationReport(threadsLimits);
		
		boolean resultT = tasksOK();

		PMO_SOUT.println("** Test na poziomie zadan " + ( resultT ? "OK" : " CRITICAL ERROR"));
		
		boolean threadsT = true;
		if ( ( maxThreadsCounter.get( 0 ) < 2 ) || ( maxThreadsCounter.get(1) < 2 ) ) {
			PMO_SOUT.println( "** Blad krytyczny: Nie wykryto jednoczesnego uzycia wymaganej liczby watkow" );
			threadsT = false;
		}

		boolean taskInsertT = tasksInsertOK();
		
		if ( taskInsertT && threadsT && resultT ) {
			PMO_SOUT.println( "*******************************************************");
			PMO_SOUT.println( "*** OK OK OK OK OK OK OK OK OK OK OK OK OK OK OK OK ***");
			PMO_SOUT.println( "*******************************************************");
		} else {
			PMO_SOUT.println( "*******************************************************");
			PMO_SOUT.println( "***        TEST PIERWSZY NIE ZOSTAL ZALICZONY       ***");
			PMO_SOUT.println( "*******************************************************");			
		}
		
	}

	private static void test2() {
		PMO_SOUT.println("Start testu 2");

		int threadsLimits[] = createTest2();
		startTaskInsertThread();

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		showTasksTimeReport();
		showThreadUtlizationReport(threadsLimits);
		
		boolean resultT = tasksOK();

		PMO_SOUT.println("** Test na poziomie zadan " + ( resultT ? "OK" : " CRITICAL ERROR"));
		
		boolean threadsT = notBeforeTest();

		boolean taskInsertT = tasksInsertOK();
		
		if ( taskInsertT && threadsT && resultT ) {
			PMO_SOUT.println( "*******************************************************");
			PMO_SOUT.println( "*** OK OK OK OK OK OK OK OK OK OK OK OK OK OK OK OK ***");
			PMO_SOUT.println( "*******************************************************");
		} else {
			PMO_SOUT.println( "*******************************************************");
			PMO_SOUT.println( "***          TEST DRUGI NIE ZOSTAL ZALICZONY        ***");
			PMO_SOUT.println( "*******************************************************");			
		}
	}

	public static void main(String[] args) {
		test1();
		test2();
		System.exit(0);
	}
}
