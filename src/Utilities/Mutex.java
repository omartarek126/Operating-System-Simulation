package Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Mutex {

	private int available;
	private int ownerID;
	public static ArrayList<Integer> generalBlockedQueue = new ArrayList<>();
	private Queue<Integer> specificQueue;

	public Mutex() {
		this.available = 1;
		this.specificQueue = new LinkedList<>();
	}

	public void semWait(int id, Scheduler S) {
		if (this.available == 1) {
			this.ownerID = id;
			this.available = 0;
		} else {
			generalBlockedQueue.add(id);
			this.specificQueue.add(id);
			S.setState("Blocked");
		}
	}

	public void semSignal(int id, Scheduler S) {
		if (id == this.ownerID) {
			if (this.specificQueue.isEmpty()) {
				this.available = 1;
			} else {
				int released = this.specificQueue.poll();
				S.getReadyQueue().add(released);
				S.setStateForReleased(released);
				this.ownerID = released;
				generalBlockedQueue.remove(generalBlockedQueue.indexOf(released));
			}
		}
	}

}
