package Utilities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import OS.Kernel;

public class Scheduler {

	private Queue<Integer> readyQueue;
	private Queue<Integer> timeOfArrival;
	private Queue<Integer> orderOfArrival;
	private int currentTime;
	private int timeSlice;
	private int currentSlice;
	private static int mostRecentlyUsedId;
	private String runningMemoryBoundaries = "";

	public Scheduler() {
		this.readyQueue = new LinkedList<>();
		this.timeOfArrival = new LinkedList<>(Arrays.asList(0, 1, 4));
		this.orderOfArrival = new LinkedList<>(Arrays.asList(1, 2, 3));
		this.timeSlice = 2;
		this.currentSlice = 0;
		this.currentTime = -1;
	}

	public void schedule(Interpreter interpreter) {
		mostRecentlyUsedId = this.runningMemoryBoundaries != "" ? this.getCurrentID() : -1;
		this.setCurrentTime(++this.currentTime);
		this.currentSlice++;
		Process y = null;
		if (!this.orderOfArrival.isEmpty()) { // Check if there are processes yet to arrive
			if (this.currentTime == this.timeOfArrival.peek()) { // Check if it is the time for it to arrive
				this.timeOfArrival.poll();
				int x = this.orderOfArrival.poll();
				y = new Process(x);
				interpreter.loadProgram(y);
				Kernel.handleNewProcess(y, this);
				this.readyQueue.add(x);
			}
		}

		if (this.runningMemoryBoundaries != "") {
			int currentPC = this.getCurrentPC();
			int boundaryPC = this.getBoundaryPC();
			if (!this.readyQueue.isEmpty()) {
				// Check if the runningProc completed all its instructions
				if (currentPC > boundaryPC || this.fetchInstruction() == null
						|| this.fetchInstruction().equals("null")) {
					this.setState("Finished");
					this.runningMemoryBoundaries = this.fetchMemoryBoundaries(this.readyQueue.poll());
					this.setState("Running");
					this.currentSlice = 0;
				}
				// Check if the runningProc completed its time slice
				else if (this.timeSlice == this.currentSlice) {
					/*
					 * Check if the runningProc completed its time slice and is not blocked so we
					 * can return it to the ready queue
					 */
					if (!Mutex.generalBlockedQueue.contains(this.getCurrentID())) {
						this.setState("Ready");
						this.readyQueue.add(this.getCurrentID());
					}
					this.runningMemoryBoundaries = this.fetchMemoryBoundaries(this.readyQueue.poll());
					this.setState("Running");
					this.currentSlice = 0;
				}
				// Check if the runningProc was blocked before completing its time slice
				else if (Mutex.generalBlockedQueue.contains(this.getCurrentID())) {
					this.runningMemoryBoundaries = this.fetchMemoryBoundaries(this.readyQueue.poll());
					this.setState("Running");
					this.currentSlice = 0;
				}
			}
			/*
			 * Check if the final runningProc completed all its instructions and the program
			 * ended
			 */
			else if (currentPC > boundaryPC || this.fetchInstruction() == null
					|| this.fetchInstruction().equals("null")) {
				this.setState("Finished");
				this.runningMemoryBoundaries = "";
				this.currentSlice = 0;
			}
		}
		// Check if the runningProc completed its time slice and the ready queue is
		// empty
		if (this.timeSlice == this.currentSlice) {
			this.currentSlice = 0;
		}
		// For the first process to be chosen
		if (this.runningMemoryBoundaries == "" && !this.readyQueue.isEmpty()) {
			this.runningMemoryBoundaries = this.fetchMemoryBoundaries(this.readyQueue.poll());
			this.setState("Running");
			this.currentSlice = 0;
		}

	}

	public String fetchInstruction() {
		int pc = this.getCurrentPC();
		if (this.runningMemoryBoundaries.charAt(0) == '0')
			return Kernel.getValueFromMemory(pc + 7);
		return Kernel.getValueFromMemory(pc + 27);
	}

	public String fetchMemoryBoundaries(int id) {
		if (id == Integer.parseInt(Kernel.getValueFromMemory(0)))
			return "0,19";
		if (id == Integer.parseInt(Kernel.getValueFromMemory(20)))
			return "20,39";
		return Kernel.memoryDiskSwapOld(this, id);
	}

	public void incrementPC() {
		int indexOfPC = Integer.parseInt(this.runningMemoryBoundaries.split(",")[0]) + 2;
		int pc = Integer.parseInt(Kernel.getValueFromMemory(indexOfPC));
		Kernel.writeValueToMemory(indexOfPC, pc + 1 + "");

	}

	public int getCurrentPC() {
		return Integer
				.parseInt(Kernel.getValueFromMemory(Integer.parseInt(this.runningMemoryBoundaries.split(",")[0]) + 2));
	}

	public int getBoundaryPC() {
		if (this.runningMemoryBoundaries.charAt(0) == '0')
			return 19;
		return 39;
	}

	public int getCurrentID() {
		return Integer
				.parseInt(Kernel.getValueFromMemory(Integer.parseInt(this.runningMemoryBoundaries.split(",")[0])));
	}

	public String getRunningMemoryBoundaries() {
		return this.runningMemoryBoundaries;
	}

	public Queue<Integer> getReadyQueue() {
		return this.readyQueue;
	}

	public int getCurrentTime() {
		return this.currentTime;
	}

	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public int getTimeSlice() {
		return this.timeSlice;
	}

	public static int getMostRecentlyUsedId() {
		return mostRecentlyUsedId;
	}

	public void setState(String newState) {
		Kernel.writeValueToMemory(Integer.parseInt(this.runningMemoryBoundaries.split(",")[0]) + 1, newState);
	}

	public void setStateForReleased(int id) {
		if (id == Integer.parseInt(Kernel.getValueFromMemory(0))) {
			Kernel.writeValueToMemory(1, "Ready");
		} else if (id == Integer.parseInt(Kernel.getValueFromMemory(20))) {
			Kernel.writeValueToMemory(21, "Ready");
		}
	}

}