package Utilities;

import java.util.HashMap;

public class Process {

	private int id;
	private String state;
	private int programCounter;
	private String memoryBoundaries;
	private HashMap<String, String> variables;
	private String[] instructions;

	public Process(int id) {
		this.id = id;
		this.variables = new HashMap<>();
		this.instructions = new String[13];
	}

	public int getId() {
		return this.id;
	}

	public String getState() {
		return this.state;
	}

	public int getProgramCounter() {
		return this.programCounter;
	}

	public String getMemoryBoundaries() {
		return this.memoryBoundaries;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public void setMemoryBoundaries(String memoryBoundaries) {
		this.memoryBoundaries = memoryBoundaries;
	}

	@Override
	public String toString() {
		return "pID: " + this.id;
	}

	public String[] getInstructions() {
		return this.instructions;
	}

	public void setInstructions(String[] instructions) {
		this.instructions = instructions;
	}

}