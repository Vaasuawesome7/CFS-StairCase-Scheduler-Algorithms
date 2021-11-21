import javafx.util.Pair;

import java.util.*;

public class CFS {

    // quantum time slice
    static double time_quantum_slice = 2;

    static class Process {
        final int process_id;
        double burst_time;
        double arrival_time;
        final int nice;
        double v_runtime;

        Process(int id, double bt, double at, int n) {
            process_id = id;
            burst_time = bt;
            arrival_time =  at;
            v_runtime = 0;
            nice = n;
        }
    }

    // this Comparator compares the arrival time of processes p1, p2 and uses nice value for tie-breaker
    static Comparator<Process> inputQueueComparator = (p1, p2) -> {
        if (p1.arrival_time == p2.arrival_time) {
            return p1.nice-p2.nice;
        } else if (p1.arrival_time > p2.arrival_time) {
            return 1;
        }
        return -1;
    };

    // this Comparator compares and returns the smaller v_runtime of the 2 processes, assigning priority
    // in case of tie-breaker (v_runtime is same for both processes, we allocate priority using nice value) - smaller
    // the nice value, higher the priority it gets
    static Comparator<Pair<Double, Integer>> RBTComp = (o1, o2) -> {
        if (o1.getKey() - o2.getKey() == 0) {
            return o1.getValue()-o2.getValue();
        }
        if (o1.getKey() > o2.getKey()) {
            return 1;
        } else {
            return -1;
        }


    };

    // the function that schedules the processes. returns an array telling the order in which processes are scheduled
    static int[] CFSAlgorithm(PriorityQueue<Process> inputQueue) {
        if (inputQueue.isEmpty())
            return null;

        // the variable keeping track at what time any process is being executed
        double current_time = inputQueue.peek().arrival_time;

        // the array storing the order in which process is scheduled
        int[] scheduledProcesses = new int[100000];
        int i = 0;

        // pair<v_runtime, nice>;
        TreeMap<Pair<Double, Integer>, Process> processQueue = new TreeMap<>(RBTComp);

        while (!inputQueue.isEmpty() || !processQueue.isEmpty()) {
            // this if is executed when all arrived processes are scheduled and there are still some more left yet
            // to arrive, so we fast-forward current_time there
            if (processQueue.isEmpty()) {
                current_time = inputQueue.peek().arrival_time;
            }

            // adds all process to queue that have arrived
            while (!inputQueue.isEmpty() && current_time >= inputQueue.peek().arrival_time) {
                Pair<Double, Integer> pair = new Pair<>(inputQueue.peek().v_runtime, inputQueue.peek().nice);
                processQueue.put(pair, inputQueue.peek());
                inputQueue.poll();
            }

            Pair<Double, Integer> deletePair = processQueue.firstKey();
            // we remove the process from the queue while it is executing
            Process currentProcess = processQueue.remove(deletePair);
            scheduledProcesses[i++] = currentProcess.process_id;

            double process_time_slice = time_quantum_slice / (processQueue.size() + 1);

            if (currentProcess.burst_time <= process_time_slice) {
                // this case process is fully executed and is already removed from process queue
                current_time += currentProcess.burst_time;
            } else {
                // in this case again only part of process will run and remaining part is added back to the process queue
                currentProcess.burst_time = currentProcess.burst_time - process_time_slice;
                currentProcess.v_runtime += process_time_slice * Math.pow(1.25, currentProcess.nice);
                current_time += process_time_slice;
                Pair<Double, Integer> newPair = new Pair<>(currentProcess.v_runtime, currentProcess.nice);
                processQueue.put(newPair, currentProcess);
                // process's partial execution ends, and it is added back to the queue
            }
        }

        scheduledProcesses[i] = -1;

        return scheduledProcesses;
    }

    public static void main(String[] args) {
        System.out.println("Input number of processes");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        // the queue arranged by arrival time (increasing order)
        PriorityQueue<Process> inputQueue = new PriorityQueue<>(inputQueueComparator);

        // nice values can be negative as well, do note that these values must not be repeated in any process
        // as it serves as tiebreaker
        for (int i = 0; i < n; i++) {
            System.out.print("Process ID: ");
            int id = scanner.nextInt();
            System.out.print("Nice value: ");
            int nice = scanner.nextInt();
            System.out.print("Arrival time: ");
            int at = scanner.nextInt();
            System.out.print("Burst time: ");
            int bt = scanner.nextInt();
            Process process = new Process(id, bt, at, nice);
            inputQueue.add(process);
            System.out.println();
        }

        System.out.println("Enter time quantum");
        time_quantum_slice = scanner.nextInt();

        int[] processHistory = CFSAlgorithm(inputQueue);

        System.out.println("Process details");

        if (processHistory!=null) {
            for (int process : processHistory) {
                if (process == -1)
                    break;
                String output = "Process" + process + " ";
                System.out.print(output);
            }
        } else {
            System.out.println("Error");
        }
    }
}
