package Utilities;

import java.io.File;
import java.util.Scanner;

import OS.Kernel;

public class Interpreter {

	public Interpreter() {
	}

	public void loadProgram(Process processToBeLoaded) {

		String[] instructions = new String[13];
		try {
			File file = new File("Programs/Program_" + processToBeLoaded.getId() + ".txt");
			Scanner sc = new Scanner(file);
			int i = 0;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				instructions[i++] = line;
			}
			processToBeLoaded.setInstructions(instructions);
			sc.close();
		} catch (Exception e) {
			System.out.println("Program not found");
		}
	}

	public void interpret(Mutex fileAccessMutex, Mutex inputMutex, Mutex outputMutex, Scheduler scheduler) {
		String CurrentInstruction = scheduler.fetchInstruction();
		int currentId = scheduler.getCurrentID();
		String[] AfterSplit = CurrentInstruction.split("\\s+");
		if (AfterSplit[0].equals("semWait")) {
			if (AfterSplit[1].equals("userInput")) {
				inputMutex.semWait(currentId, scheduler);
			} else if (AfterSplit[1].equals("userOutput")) {
				outputMutex.semWait(currentId, scheduler);
			} else {
				fileAccessMutex.semWait(currentId, scheduler);
			}
			scheduler.incrementPC();
		} else if (AfterSplit[0].equals("semSignal")) {
			if (AfterSplit[1].equals("userInput")) {
				inputMutex.semSignal(currentId, scheduler);
			} else if (AfterSplit[1].equals("userOutput")) {
				outputMutex.semSignal(currentId, scheduler);
			} else {
				fileAccessMutex.semSignal(currentId, scheduler);
			}
			scheduler.incrementPC();
		} else if (AfterSplit[0].equals("writeFile")) {
			String memoryData1 = this.readFromMemory(AfterSplit[1], currentId);
			String memoryData2 = this.readFromMemory(AfterSplit[2], currentId);
			if (memoryData1 == null && memoryData2 == null) {
				this.writeFile(AfterSplit[1], AfterSplit[2]);
			} else if (memoryData1 == null) {
				this.writeFile(AfterSplit[1], memoryData2);
			} else if (memoryData2 == null) {
				this.writeFile(memoryData1, AfterSplit[2]);
			} else {
				this.writeFile(memoryData1, memoryData2);
			}
			scheduler.incrementPC();
		} else if (AfterSplit[0].equals("readFile")) {
			String memoryData = this.readFromMemory(AfterSplit[1], currentId);
			if (memoryData == null) {
				this.readFile(AfterSplit[1]);
			} else {
				this.readFile(memoryData);
			}
			scheduler.incrementPC();
		} else if (AfterSplit[0].equals("print")) {
			String memoryData = this.readFromMemory(AfterSplit[1], currentId);
			if (memoryData == null) {
				this.print(AfterSplit[1]);
			} else {
				this.print(memoryData);
			}
			scheduler.incrementPC();
		} else if (AfterSplit[0].equals("printFromTo")) {
			String memoryData1 = this.readFromMemory(AfterSplit[1], currentId);
			String memoryData2 = this.readFromMemory(AfterSplit[2], currentId);
			int intMemoryData1;
			int intMemoryData2;
			if (memoryData1 == null && memoryData2 == null) {
				intMemoryData1 = Integer.parseInt(AfterSplit[1]);
				intMemoryData2 = Integer.parseInt(AfterSplit[2]);
			} else if (memoryData1 == null) {
				intMemoryData1 = Integer.parseInt(AfterSplit[1]);
				intMemoryData2 = Integer.parseInt(memoryData2);
			} else if (memoryData2 == null) {
				intMemoryData1 = Integer.parseInt(memoryData1);
				intMemoryData2 = Integer.parseInt(AfterSplit[2]);
			} else {
				intMemoryData1 = Integer.parseInt(memoryData1);
				intMemoryData2 = Integer.parseInt(memoryData2);
			}
			this.printFromTo(intMemoryData1, intMemoryData2);
			scheduler.incrementPC();
		} else {
			if (!AfterSplit[2].equals("input") && !AfterSplit[2].equals("readFile")) {
				String ReadData = this.readFromMemory(AfterSplit[2], currentId);
				if (ReadData == null || ReadData.equals("null")) {
					ReadData = AfterSplit[2];
				}
				this.assign(AfterSplit[1], ReadData, currentId);
				scheduler.incrementPC();
			} else {
				if (AfterSplit[2].equals("input")) {
					String ReadData = Kernel.getUserInput();
					ReadData = AfterSplit[0] + " " + AfterSplit[1] + " " + ReadData;
					Kernel.writeInstructionToMemory(scheduler.getCurrentPC(), ReadData, scheduler);
				} else if (AfterSplit[2].equals("readFile")) {
					String ReadData = this.readFromMemory(AfterSplit[3], currentId);
					String FileData = "";
					if (ReadData == null) {
						FileData = this.readFile(AfterSplit[3]);
					} else {
						FileData = this.readFile(ReadData);
					}
					FileData = AfterSplit[0] + " " + AfterSplit[1] + " " + FileData;
					Kernel.writeInstructionToMemory(scheduler.getCurrentPC(), FileData, scheduler);
				}
			}
		}
	}

	public void print(String toBePrinted) {
		toBePrinted = "the Output For Your Program Is: " + toBePrinted;
		Kernel.print(toBePrinted);
	}

	public String getUserInput() {
		return Kernel.getUserInput();
	}

	public void assign(String key, String value, int runningProcessId) {
		Kernel.assign(key, value, runningProcessId);
	}

	public void printFromTo(int firstNumber, int secondNumber) {
		Kernel.print("START:");
		for (int i = firstNumber + 1; i < secondNumber; i++) {
			Kernel.print(i + "");
		}
		Kernel.print("END");
	}

	public String readFromMemory(String key, int runningProcessId) {
		return Kernel.readFromMemory(key, runningProcessId);
	}

	public void writeFile(String fileName, String data) {
		Kernel.writeFile(fileName, data);
	}

	public String readFile(String fileName) {
		return Kernel.readFile(fileName);
	}

}
