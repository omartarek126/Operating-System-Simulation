package OS;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import Utilities.Process;
import Utilities.Scheduler;

public class Kernel {

	public static void handleNewProcess(Process newProcess, Scheduler scheduler) {
		if (getValueFromMemory(0) == null) {
			writeValueToMemory(0, "" + newProcess.getId());
			newProcess.setState("Ready");
			newProcess.setProgramCounter(0);
			newProcess.setMemoryBoundaries("0,19");
			writeValueToMemory(1, newProcess.getState());
			writeValueToMemory(2, "" + newProcess.getProgramCounter());
			writeValueToMemory(3, "" + newProcess.getMemoryBoundaries());
			for (int i = 0; i < 13; i++) {
				writeValueToMemory(i + 7, newProcess.getInstructions()[i]);
			}
		} else if (getValueFromMemory(20) == null) {
			writeValueToMemory(20, "" + newProcess.getId());
			newProcess.setState("Ready");
			newProcess.setProgramCounter(0);
			newProcess.setMemoryBoundaries("20,39");
			writeValueToMemory(21, newProcess.getState());
			writeValueToMemory(22, "" + newProcess.getProgramCounter());
			writeValueToMemory(23, "" + newProcess.getMemoryBoundaries());
			for (int i = 0; i < 13; i++) {
				writeValueToMemory(20 + i + 7, newProcess.getInstructions()[i]);
			}
		} else {
			memoryDiskSwapNew(newProcess, scheduler);
		}
	}

	public static void memoryDiskSwapNew(Process newProcess, Scheduler scheduler) {
		String dataToBeOnDisk = "";
		boolean condition1 = (Integer.parseInt(getValueFromMemory(0)) == Scheduler.getMostRecentlyUsedId()
				&& Scheduler.getMostRecentlyUsedId() != scheduler.getCurrentID());
		boolean condition2 = (Integer.parseInt(getValueFromMemory(20)) == Scheduler.getMostRecentlyUsedId()
				&& Scheduler.getMostRecentlyUsedId() == scheduler.getCurrentID());
		boolean condition = condition1 || condition2;
		if (condition) {
			for (int i = 0; i < 20; i++) {
				dataToBeOnDisk += getValueFromMemory(i) + ",,";
			}
			writeFile(getValueFromMemory(0) + "", dataToBeOnDisk);
			System.out.println("------------------");
			System.out.println("pID: " + getValueFromMemory(0) + " is saved on disk");
			for (int i = 0; i < 20; i++) {
				writeValueToMemory(i, null);
			}
			writeValueToMemory(0, newProcess.getId() + "");
			writeValueToMemory(1, "Ready");
			writeValueToMemory(2, 0 + "");
			writeValueToMemory(3, "0,19");
			for (int i = 0; i < 13; i++) {
				writeValueToMemory(i + 7, newProcess.getInstructions()[i]);
			}
		} else {
			for (int i = 20; i < 40; i++) {
				dataToBeOnDisk += getValueFromMemory(i) + ",,";
			}
			writeFile(getValueFromMemory(20) + "", dataToBeOnDisk);
			System.out.println("------------------");
			System.out.println("pID: " + getValueFromMemory(20) + " is saved on disk");
			for (int i = 20; i < 40; i++) {
				writeValueToMemory(i, null);
			}
			writeValueToMemory(20, newProcess.getId() + "");
			writeValueToMemory(21, "Ready");
			writeValueToMemory(22, 0 + "");
			writeValueToMemory(23, "20,39");
			for (int i = 0; i < 13; i++) {
				writeValueToMemory(i + 27, newProcess.getInstructions()[i]);
			}
		}
	}

	public static String memoryDiskSwapOld(Scheduler scheduler, int id) {
		String dataToBeOnDisk = "";
		if (Integer.parseInt(getValueFromMemory(0)) == Scheduler.getMostRecentlyUsedId()) {
			for (int i = 0; i < 20; i++) {
				dataToBeOnDisk += getValueFromMemory(i) + ",,";
			}
			writeFile(Scheduler.getMostRecentlyUsedId() + "", dataToBeOnDisk);
			System.out.println("------------------");
			System.out.println("pID: " + Scheduler.getMostRecentlyUsedId() + " is saved on disk");
			for (int i = 0; i < 20; i++) {
				writeValueToMemory(i, null);
			}
			String dataOnDisk = readFile(id + "");
			String[] processOnDisk = dataOnDisk.split(",,");
			for (int i = 0; i < 20; i++) {
				writeValueToMemory(i, processOnDisk[i]);
			}
			writeValueToMemory(3, "0,19");
			System.out.println("pID: " + scheduler.getCurrentID() + " is swapped out of disk");
			return "0,19";
		}
		for (int i = 20; i < 40; i++) {
			dataToBeOnDisk += getValueFromMemory(i) + ",,";
		}
		writeFile(Scheduler.getMostRecentlyUsedId() + "", dataToBeOnDisk);
		System.out.println("------------------");
		System.out.println("pID: " + Scheduler.getMostRecentlyUsedId() + " is saved on disk");
		for (int i = 20; i < 40; i++) {
			writeValueToMemory(i, null);
		}
		String dataOnDisk = readFile(id + "");
		String[] processOnDisk = dataOnDisk.split(",,");
		for (int i = 0; i < 20; i++) {
			writeValueToMemory(i + 20, processOnDisk[i]);
		}
		writeValueToMemory(23, "20,39");
		System.out.println("pID: " + scheduler.getCurrentID() + " is swapped out of disk");
		return "20,39";
	}

	public static String getValueFromMemory(int index) {
		return Memory.getMemory()[index];
	}

	public static void writeValueToMemory(int index, String value) {
		Memory.getMemory()[index] = value;
	}

	public static void writeInstructionToMemory(int currentPC, String readData, Scheduler scheduler) {
		if (scheduler.getRunningMemoryBoundaries().charAt(0) == '0') {
			Kernel.writeValueToMemory(currentPC + 7, readData);
		} else {
			Kernel.writeValueToMemory(currentPC + 27, readData);
		}

	}

	public static void print(String toBePrinted) {
		System.out.println(toBePrinted);
	}

	public static String getUserInput() {
		Scanner sc = new Scanner(System.in);
		System.out.print("Please Enter A Value ");
		String UserInput = sc.next();
		return UserInput;
	}

	public static void assign(String key, String value, int runningProcessId) {
		if (runningProcessId == Integer.parseInt(getValueFromMemory(0))) {
			if (key.equals("a")) {
				writeValueToMemory(4, value);
			} else if (key.equals("b")) {
				writeValueToMemory(5, value);
			} else {
				writeValueToMemory(6, value);
			}
		} else {
			if (key.equals("a")) {
				writeValueToMemory(24, value);
			} else if (key.equals("b")) {
				writeValueToMemory(25, value);
			} else {
				writeValueToMemory(26, value);
			}
		}
	}

	public static String readFromMemory(String key, int runningProcessId) {
		if (runningProcessId == Integer.parseInt(getValueFromMemory(0))) {
			if (key.equals("a"))
				return getValueFromMemory(4);
			if (key.equals("b"))
				return getValueFromMemory(5);
			return getValueFromMemory(6);
		}
		if (key.equals("a"))
			return getValueFromMemory(24);
		if (key.equals("b"))
			return getValueFromMemory(25);
		return getValueFromMemory(26);
	}

	public static void writeFile(String fileName, String data) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(data);
			fileWriter.close();
		} catch (Exception e) {
			System.out.println("Error while writing to file");
		}
	}

	public static String readFile(String fileName) {
		String line = "";
		try {
			File file = new File(fileName);
			Scanner sc = new Scanner(file);
			line = sc.nextLine();
			sc.close();
		} catch (Exception e) {
			System.out.println("File not found");
		}
		return line;
	}

}
