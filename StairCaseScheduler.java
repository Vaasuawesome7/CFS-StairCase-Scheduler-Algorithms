import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class StairCaseScheduler {

    // the time slice every process can execute in
    static double time_quantum_slice;

    // the max level to which priorities can enter in
    static double max_priority;

    // the Process class for StaircaseScheduler
    static class Process {
        public final int process_id;
        public final double arrival_time;
        public double burst_time;
        public int priority;
        public int priority_count;

        Process(int id, int at, int bt, int pr) {
            process_id = id;
            arrival_time = at;
            burst_time = bt;
            priority = pr;
            priority_count = priority;
        }
    }

    // this Comparator compares priority_count of 2 process and selects the minimum of the 2
    // to break the tie in case of same priority_count, we will prefer the process_id value whose value is over
    static Comparator<Process> processQueueComparator = (p1, p2) -> {
        if (p1.priority_count == p2.priority_count) {
            return p2.process_id - p1.process_id;
        }
        return p1.priority_count - p2.priority_count;
    };

    // this Comparator compares arrival_time of 2 processes and selects the higher of the two
    static Comparator<Process> inputQueueComparator = (p1, p2) -> {
        if (p1.arrival_time == p2.arrival_time) {
            return 0;
        } else if (p1.arrival_time > p2.arrival_time) {
            return 1;
        }
        return -1;
    };

    // the function for staircase algorithm
    static int[] stairCaseAlgorithm(PriorityQueue<Process> inputQueue) {

        // the array to store the order in which processes are scheduled
        int[] scheduledProcess = new int[10000];
        int i = 0;

        if (inputQueue.peek() == null)
            return null;

        // variable to store current time
        double current_time = inputQueue.peek().arrival_time;

        // initialising the process queue
        PriorityQueue<Process> processQueue = new PriorityQueue<>(processQueueComparator);

        while (!inputQueue.isEmpty() || !processQueue.isEmpty()) {
            // this if is executed when all arrived processes are scheduled and there are still some more left yet
            // to arrive, so we fast-forward current_time there
            if (processQueue.isEmpty()) {
                current_time = inputQueue.peek().arrival_time;
            }

            // adds all process to queue that have arrived
            while (!inputQueue.isEmpty() && current_time >= inputQueue.peek().arrival_time) {
                Process addedProcess = inputQueue.peek();
                processQueue.add(addedProcess);
                inputQueue.poll();
            }

            if (processQueue.isEmpty()) {
                continue;
            }

            // removing the first process in the process queue
            Process currentProcess = processQueue.poll();
            scheduledProcess[i++] = currentProcess.process_id;

            if (currentProcess.burst_time <= time_quantum_slice) {
                // if process's burst time is lesser, then it shall not be added back to the process queue
                current_time += currentProcess.burst_time;
            } else {
                // if burst time is larger than decrease burst, increase current time and change priority count
                currentProcess.burst_time -= time_quantum_slice;
                currentProcess.priority_count++;
                // this if executes when the process falls of the stair
                if (currentProcess.priority_count > max_priority) {
                    // we will keep the priority at one place below its initial starting point, there for priority++
                    currentProcess.priority++;
                    currentProcess.priority_count = currentProcess.priority;
                }
                processQueue.add(currentProcess);
                current_time += time_quantum_slice;
            }
        }

        scheduledProcess[i] = -1;

        return scheduledProcess;
    }

    public static void main(String[] args) {
        System.out.println("Input number of processes");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        // the queue arranged by arrival time (increasing order)
        PriorityQueue<Process> inputQueue = new PriorityQueue<>(inputQueueComparator);

        for (int i = 0; i < n; i++) {
            System.out.print("Process ID: ");
            int id = scanner.nextInt();
            System.out.print("Priority value: ");
            int pr = scanner.nextInt();
            System.out.print("Arrival time: ");
            int at = scanner.nextInt();
            System.out.print("Burst time: ");
            int bt = scanner.nextInt();
            Process process = new Process(id, at, bt, pr);
            inputQueue.add(process);
            System.out.println();
        }

        System.out.print("Enter time quantum: ");
        time_quantum_slice = scanner.nextInt();
        System.out.println();
        System.out.print("Enter max level: ");
        max_priority = scanner.nextInt();
        System.out.println();

        int[] scheduledProcess = stairCaseAlgorithm(inputQueue);

        if (scheduledProcess != null) {
            for (int x : scheduledProcess) {
                if (x == -1)
                    break;
                System.out.print("Process" + x + " ");
            }
        } else {
            System.out.println("Error");
        }
    }
}
