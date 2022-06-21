package OS;

public class Memory {
	private static String[] memory = new String[40];

	public static String[] getMemory() {
		return memory;
	}

}

//block 1
//0// ID
//1// State
//2// PC
//3// Memory Boundaries
//456// 3 variables
//7-19//instructions

//block 2
//20//ID
//21// State
//22// PC
//23// Memory Boundaries
//242526// 3 variables
//27-39//instructions

//Disk Format: pID,,State,,PC,,MemoryBoundaries,,VariableA,VariableB,,VariableC,,Instruction1,,Instruction2,...etc