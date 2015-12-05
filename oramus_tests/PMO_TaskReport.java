public class PMO_TaskReport {
	public int jobID;
	public long startTime;
	public long endTime;
	public int queue;
	public boolean keepOrder;

	public String toString() {
		return "Task " + jobID + ( keepOrder ? " ORDER" : " free" ) + " start from queue " + queue + " at "
				+ startTime + " finished at " + endTime;
	}

	public String toString( long start ) {
		return "Task " + jobID + ( keepOrder ? " ORDER" : " FREE " ) + " start from queue " + queue + " at "
				+ (startTime - start) + " finished at " + ( endTime - start );
	}

}
