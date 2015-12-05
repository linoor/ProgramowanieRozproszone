import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class PMO_TaskImplementation implements TaskInterface, Delayed {

	private AtomicIntegerArray threadsCounter;
	private AtomicIntegerArray maxThreadsCounter;
	// id ostatnio zakonczonej pracy
	private AtomicInteger lastJobFinished;
	private long[] sleepTimes;
	private long initialDelay;
	private List<PMO_TaskReport> reports;

	final private int firstQueue;
	final private int lastQueue;
	final private int taskID;
	final private boolean keepOrder;
	private AtomicBoolean taskDone;
	private AtomicBoolean criticalMistake;

	void setHelpers(AtomicIntegerArray threadsCounter,
			AtomicIntegerArray maxThreadsCounter,
			AtomicInteger lastJobFinished, long[] sleepTimes,
			List<PMO_TaskReport> reports ) {
		this.threadsCounter = threadsCounter;
		this.maxThreadsCounter = maxThreadsCounter;
		this.lastJobFinished = lastJobFinished;
		this.sleepTimes = sleepTimes;
		this.reports = reports;
	}

	public PMO_TaskImplementation(int firstQueue, int lastQueue, int taskID,
			boolean keepOrder, AtomicBoolean criticalMistake, AtomicBoolean taskDone ) {
		this( firstQueue, lastQueue, taskID, keepOrder );
		this.criticalMistake = criticalMistake;
		this.taskDone = taskDone;
	}

	public PMO_TaskImplementation(int firstQueue, int lastQueue, int taskID,
			boolean keepOrder ) {
		this.firstQueue = firstQueue;
		this.lastQueue = lastQueue;
		this.taskID = taskID;
		this.keepOrder = keepOrder;
		this.criticalMistake = new AtomicBoolean( false );
		this.taskDone = new AtomicBoolean( false );
	}
	
	
	public void setInitialDelay(long msec) {
		this.initialDelay = System.currentTimeMillis() + msec;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(initialDelay - System.currentTimeMillis(),
				TimeUnit.MICROSECONDS);
	}

	@Override
	public int getFirstQueue() {
		return firstQueue;
	}

	@Override
	public int getLastQueue() {
		return lastQueue;
	}

	@Override
	public int getTaskID() {
		return taskID;
	}

	@Override
	public boolean keepOrder() {
		return keepOrder;
	}

	@Override
	public TaskInterface work(int queue) {
		PMO_TaskReport report;

		synchronized (reports) {
			report = new PMO_TaskReport();
			report.jobID = taskID;
			report.keepOrder = keepOrder;
			report.queue = queue;
			report.startTime = System.currentTimeMillis();

			reports.add(report);
		}

		if (queue != firstQueue) {
			PMO_SOUT.println("Blad krytyczny : work( " + queue
					+ " ), wykonany przy firstQueue = " + firstQueue);
			criticalMistake.set(true);
		}
		if (queue > lastQueue) {
			PMO_SOUT.println("Blad krytyczny : work( " + queue
					+ " ), lastQueue = " + lastQueue);
			criticalMistake.set(true);
		}
		if (queue < firstQueue) {
			PMO_SOUT.println("Blad krytyczny : work( " + queue
					+ " ), firstQueue = " + firstQueue);
			criticalMistake.set(true);
		}

		int threads = threadsCounter.incrementAndGet(queue);
		if (threads > maxThreadsCounter.get(queue)) {
			synchronized (maxThreadsCounter) {
				if (threads > maxThreadsCounter.get(queue)) {
					maxThreadsCounter.set(queue, threads);
				}
			}
		}

		try {
			Thread.sleep(sleepTimes[queue]);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PMO_TaskImplementation ti = null;
		if (queue != lastQueue) {
			ti = new PMO_TaskImplementation(queue + 1, lastQueue, taskID,
					keepOrder, criticalMistake, taskDone );
			ti.setHelpers(threadsCounter, maxThreadsCounter, lastJobFinished,
					sleepTimes, reports );
		} else {
			if (keepOrder) {
				int lastJobID = lastJobFinished.get();
				if (lastJobID > taskID) {
					PMO_SOUT.println("Blad krytyczny : jeszcze pracuje zadanie "
							+ taskID
							+ ", a juz zakonczono zadanie "
							+ lastJobID);
					criticalMistake.set(true);
				}
				lastJobFinished.set(taskID);
			}
			taskDone.set( true ); // potwierdzenie zakonczenia tego zadania
		}

		report.endTime = System.currentTimeMillis();
		threadsCounter.decrementAndGet(queue);

		return ti;
	}

	public boolean report() {
		// wykonano zadanie do konca nie popelniajac przy tym krytycznych
		// pomylek
		
		if ( ! taskDone.get() ) {
			PMO_SOUT.println( "Blad krytyczny: nie wykonano do konca zadania" );
		}
		
		return taskDone.get() && (!criticalMistake.get());
	}

	@Override
	public int compareTo(Delayed o) {
		if (this.initialDelay < ((PMO_TaskImplementation) o).initialDelay) {
			return -1;
		}
		if (this.initialDelay > ((PMO_TaskImplementation) o).initialDelay) {
			return 1;
		}
		return 0;
	}

}
