import functools
import time
import threading
import statistics
import json

def bench(n_threads=1,seq_iter=1,iter=1):
    exec_times = []
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            def seq_iter_loop(func, *args, **kwargs):
                for _ in range (seq_iter):
                    func(*args, **kwargs)

            for _ in range(iter):
                workers = []
                start_time = time.perf_counter()
                for i in range(n_threads):
                    t_args = (func,) + args
                    workers.append(threading.Thread(target=seq_iter_loop,args=t_args,kwargs=kwargs))
                    workers[i].start()

                for t in workers:
                    t.join()

                end_time = time.perf_counter()
                run_time = end_time - start_time
                exec_times.append(run_time)
            
            mean = statistics.mean(exec_times)
            variance = statistics.variance(exec_times)

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


def test(fun, args, iter):
    
    def write_to_file(n_threads,seq_iter,iter):
        res = bench(n_threads=n_threads,seq_iter=seq_iter,iter=iter)(fun)(*args)
        filename = f"out/{fun.__name__}_{args}_{n_threads}_{seq_iter}.json"
        with open(filename, 'w') as file:
            file.write(str(json.dumps(res, indent=3)))

    print("Start test for " + fun.__name__)
    write_to_file(1,16,iter)

    write_to_file(2,8,iter)

    write_to_file(4,4,iter)

    write_to_file(8,2,iter)
    print("End test for " + fun.__name__)


def just_wait(n):
    time.sleep(n*0.1)

def grezzo(n):
    for i in range(2**n):
        pass

def example(n,k):
    for i in range(n):
        for j in range(k):
            time.sleep(0.0001)

def main():
    
    test(just_wait,(1,),5)
    
    test(grezzo,(10,),5)
    
    test(example,(10,6),5)

if __name__ == "__main__":
    main()