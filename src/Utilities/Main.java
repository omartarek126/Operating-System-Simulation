package Utilities;

import java.util.ArrayList;
import java.util.Queue;

import OS.Kernel;

public class Main {

	Interpreter interpreter;
	Scheduler scheduler;
	Mutex fileAccessMutex;
	Mutex inputMutex;
	Mutex outputMutex;

	public Main() {
		this.interpreter = new Interpreter();
		this.scheduler = new Scheduler();
		this.fileAccessMutex = new Mutex();
		this.inputMutex = new Mutex();
		this.outputMutex = new Mutex();
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.scheduler.schedule(main.interpreter);
		String runningProcessMemoryBoundaries = main.scheduler.getRunningMemoryBoundaries();
		printDiskFormat();
		printMemory(main.scheduler.getCurrentTime());
		while (!runningProcessMemoryBoundaries.equals("")) {
			printStatus(main.scheduler.getCurrentTime(), main.scheduler.getReadyQueue(), Mutex.generalBlockedQueue,
					main.scheduler.getCurrentID(), main.scheduler.fetchInstruction());
			main.interpreter.interpret(main.fileAccessMutex, main.inputMutex, main.outputMutex, main.scheduler);
			main.scheduler.schedule(main.interpreter);
			printMemory(main.scheduler.getCurrentTime());
			runningProcessMemoryBoundaries = main.scheduler.getRunningMemoryBoundaries();
		}
		System.out.println("------------------");
		System.out.println("Processes Completed Successfully!");
	}

	public static void printStatus(int currentTime, Queue<Integer> readyQueue, ArrayList<Integer> generalBlockedQueue,
			Integer runningProcessId, String runningInstruction) {
		System.out.println("------------------");
		System.out.println("Current Time: " + currentTime);
		System.out.println("Ready Queue: " + readyQueue);
		System.out.println("Blocked Queue: " + generalBlockedQueue);
		System.out.println("Executing Process ID: " + runningProcessId);
		System.out.println("Executing Instruction: " + runningInstruction);

	}

	public static void printDiskFormat() {
		System.out
				.println("Disk Format: pID,,State,,PC,,MemoryBoundaries,,VariableA,VariableB,,VariableC,,Instructions");
	}

	public static void printMemory(int currentTime) {
		String instructions1 = "";
		for (int i = 0; i < 6; i++) {
			instructions1 += Kernel.getValueFromMemory(i + 7) + " | ";
		}
		instructions1 += "\n";
		for (int i = 6; i < 13; i++) {
			instructions1 += Kernel.getValueFromMemory(i + 7) + " | ";
		}
		String instructions2 = "";
		for (int i = 0; i < 6; i++) {
			instructions2 += Kernel.getValueFromMemory(i + 27) + " | ";
		}
		instructions2 += "\n";
		for (int i = 6; i < 13; i++) {
			instructions2 += Kernel.getValueFromMemory(i + 27) + " | ";
		}
		if (currentTime == 0) {
			System.out.println("------------------");

		} else {
			System.out.println(
					"---------------------------------------------------------------------------------------------------------------------------------");
		}
		if (Kernel.getValueFromMemory(0) != null) {

			System.out.println("Block 1: ");
			System.out.println("pID: " + Kernel.getValueFromMemory(0) + " | " + "State: " + Kernel.getValueFromMemory(1)
					+ " | " + "PC: " + Kernel.getValueFromMemory(2) + " | " + "MemoryBoundaries: "
					+ Kernel.getValueFromMemory(3).split(",")[0] + "->" + Kernel.getValueFromMemory(3).split(",")[1]
					+ " | " + "VariableA: " + Kernel.getValueFromMemory(4) + " | " + "VariableB: "
					+ Kernel.getValueFromMemory(5) + " | " + "VariableC: " + Kernel.getValueFromMemory(6) + " | " + "\n"
					+ "Instructions: " + instructions1);
			System.out.println("------------------");
		} else {
			System.out.println("------------------");
			System.out.println("Block 1: ");
			System.out.println("Empty");
			System.out.println("------------------");
		}
		if (Kernel.getValueFromMemory(20) != null) {
			System.out.println("Block 2: ");
			System.out.println("pID: " + Kernel.getValueFromMemory(20) + " | " + "State: "
					+ Kernel.getValueFromMemory(21) + " | " + "PC: " + Kernel.getValueFromMemory(22) + " | "
					+ "MemoryBoundaries: " + Kernel.getValueFromMemory(23).split(",")[0] + "->"
					+ Kernel.getValueFromMemory(23).split(",")[1] + " | " + "VariableA: "
					+ Kernel.getValueFromMemory(24) + " | " + "VariableB: " + Kernel.getValueFromMemory(25) + " | "
					+ "VariableC: " + Kernel.getValueFromMemory(26) + " | " + "\n" + "Instructions: " + instructions2);
		} else {
			System.out.println("Block 2: ");
			System.out.println("Empty");
		}

	}

}