import functools
import time
import threading
import statistics
import json

# Bench function to create a decorator that benchmarks a function's performance.
# Parameters:
# n_threads: Number of threads to use for parallel execution.
# seq_iter: Number of sequential iterations each thread will execute the function.
# iter: Number of times the whole threading process is repeated.
def bench(n_threads=1, seq_iter=1, iter=1):
    exec_times = []  # List to store execution times for each run.
    
    # Decorator function to wrap the target function.
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            # Function to execute the target function sequentially for a specified number of iterations.
            def seq_iter_loop(func, *args, **kwargs):
                for _ in range(seq_iter):
                    func(*args, **kwargs)

            for _ in range(iter):  # Repeat the whole process for a specified number of iterations.
                workers = []  # List to store threads.
                start_time = time.perf_counter()  # Record the start time.
                
                # Create and start threads.
                for i in range(n_threads):
                    t_args = (func,) + args
                    workers.append(threading.Thread(target=seq_iter_loop, args=t_args, kwargs=kwargs))
                    workers[i].start()

                # Wait for all threads to finish.
                for t in workers:
                    t.join()

                end_time = time.perf_counter()  # Record the end time.
                run_time = end_time - start_time  # Calculate the execution time for this run.
                exec_times.append(run_time)  # Store the execution time.
            
            # Calculate mean and variance of the execution times.
            mean = statistics.mean(exec_times)
            variance = statistics.variance(exec_times)

            # Return a dictionary with benchmark results.
            return {
                'fun': func.__name__,
                'args': args,
                'n_threads': n_threads,
                'seq_iter': seq_iter,
                'iter': iter,
                'mean': mean,
                'variance': variance,
            }
        return wrapper
    return decorator


# Test function to run benchmarks and write results to files.
def test(fun, args, iter):
    
    # Function to run benchmarks with specified parameters and write results to a file.
    def write_to_file(n_threads, seq_iter, iter):
        res = bench(n_threads=n_threads, seq_iter=seq_iter, iter=iter)(fun)(*args)
        filename = f"out/{fun.__name__}_{args}_{n_threads}_{seq_iter}.json"
        with open(filename, 'w') as file:
            file.write(str(json.dumps(res, indent=3)))

    print("Start test for " + fun.__name__)
    write_to_file(1, 16, iter)  # Benchmark with 1 thread and 16 sequential iterations.
    write_to_file(2, 8, iter)   # Benchmark with 2 threads and 8 sequential iterations.
    write_to_file(4, 4, iter)   # Benchmark with 4 threads and 4 sequential iterations.
    write_to_file(8, 2, iter)   # Benchmark with 8 threads and 2 sequential iterations.
    print("End test for " + fun.__name__)


# Example function that just waits for a specified duration.
def just_wait(n):
    time.sleep(n * 0.1)

# Example function that performs a number of empty iterations.
def grezzo(n):
    for i in range(2 ** n):
        pass

# Example function that performs nested loops with a short sleep.
def example(n, k):
    for i in range(n):
        for j in range(k):
            time.sleep(0.001)

def main():
    test(just_wait, (1,), 5)
    test(grezzo, (10,), 5)
    test(example, (10, 6), 5)

if __name__ == "__main__":
    main()

# Experimentation Results:
# The benchmarking experiments produced varied results depending on the nature of the function being tested.
# 
# 1. just_wait(n): Since this function involves sleeping, the execution time scales linearly with the sleep duration.
#    With increased threading, there might be slight variations in time due to the overhead of managing threads.
#
# 2. grezzo(n): This function performs computational work in a loop. The execution time increases exponentially with
#    the input size. The multithreading benefits are limited due to the Global Interpreter Lock (GIL) in Python, which
#    restricts concurrent execution of threads, especially for CPU-bound tasks.
#
# 3. example(n, k): This function combines looping and sleeping. The execution time depends on both loop counts and 
#    sleep duration. Similar to just_wait, threading can have a marginal impact due to the GIL and the nature of the
#    task (mixed I/O and CPU-bound).
#
# Overall, multithreading showed limited improvement in performance for CPU-bound tasks due to the GIL. I/O-bound tasks
# like sleeping demonstrated better utilization of threading capabilities.