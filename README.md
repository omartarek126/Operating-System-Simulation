# Operating System Simulation

This project is a simulation of an operating system that parses and interprets different instructions, which are then bundled into different processes and executed. The OS has many features such as mutual exclusion over critical resources using mutexes, memory and disk management and process scheduling.

## Technologies Used:
- Java with an OOP approach

## OS Components:
- 5 Queues:
  - Ready Queue (Contains processes ready to execute)
  - General Blocked Queue (Contains all processes trying to access any currently busy critical resource)
  - File Access Blocked Queue (Contains processes trying to access a file, to read or to write while it is already being used by another process)
  - Input Blocked Queue (Contains processes trying to take an input from the user while another process is trying to do so)
  - Output Blocked Queue (Contains processes trying to output something on the screen while another process is trying to do so)
- Scheduler (Uses a scheduling algorithm to figure out which process should run)
- Code Parser and Interpreter (Reads instructions from the text files and executes them)
- Mutex (Once the currently running process requests access to a critical resource, it checks whether the resource is available or not, if it is available, the mutex assigns the process as the current owner of this resource and marks the resource as busy until the same process releases the resource, if it is not available, the process is placed in the general blocked queue and the blocked queue of this resource until the resource is released by its current owner)
- Main Memory (40 blocks)
- Disk

## Instructions Available:
Each instruction executes in only 1 clock cycle except if it is a nested instruction it executes in 2 clock cycles
- print: to print the output on the screen. Example: print x
- assign: to initialize a new variable and assign a value to it. Example: assign x y, where x is the variable and y is the value assigned. The value could be an integer number, or a string. If y is input, it first prints to the screen "Please enter a value", then the value is taken as an input from the user
- writeFile: to write data to a file. Example: writeFile x y, where x is the filename and y is the data
- readFile: to read data from a file. Example: readFile x, where x is the filename
- printFromTo: to print all numbers between 2 numbers. Example: printFromTo x y, where x is the first number, and y is the second number
- semWait: to acquire a resource. Example: semWait x, where x is the resource name
- semSignal: to release a resource. Example: semSignal x, where x is the resource name

## Process Control Block (PCB):
The process control block is used by the operating system to store all the information about a process such as:
- Process ID (Unique and assigned once a process is created)
- Process State (Running, Ready, Blocked)
- Program Counter (Keep track of the next instruction to be executed within a process)
- Memory Boundaries (Block address range assigned for the process in memory)

## Scheduling Algorithm:
Round robin is the scheduling algorithm used, which operates as follows:
1. Once a process is created, it is placed in the ready queue
2. The first process that arrived is dequeued from the ready queue and assigned a fixed time slice (Which its value can be modified from the scheduler class)
3. If the currently running process completed its time slice or completed all its instructions or was blocked for trying to access a busy critical resource, then dequeue the next process from the ready queue and assign to it a time slice
4. If the currently running process completed its time slice and still has some instructions to be executed and no other processes are waiting in the ready queue, then reassign it a new time slice
5. If the currently running process completed all its instructions and no other processes are in the ready queue, then the OS has completed and terminates

## Memory and Disk Management:
For any process to be able to execute, it must be currently saved on memory, but since the memory consists of only 40 blocks and each process occupies 20 blocks, Most Recently Finished (MRF) swapping algorithm is used to be able to swap different processes between memory and disk

## Memory<->Disk Swapping Algorithm:
Since round robin scheduling algorithm is used where processes are executed in time slices in order of arrival, the most efficient swapping algorithm to be used is Most Recently Finished (MRF) which operates as follows:
1. Once a process arrives, it is placed in the memory and assigned 20 blocks, if there is no available space, the most recently finished process is swapped out of the memory and placed on disk since it is the least likely process to run again soon
2. If a process is scheduled to run by the scheduler and it is not available on memory, the most recently finished process is swapped out of the memory and placed on disk
