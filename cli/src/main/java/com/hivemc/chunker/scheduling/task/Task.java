package com.hivemc.chunker.scheduling.task;

import com.hivemc.chunker.scheduling.function.ThrowableConsumer;
import com.hivemc.chunker.scheduling.function.ThrowableFunction;
import com.hivemc.chunker.scheduling.function.ThrowableRunnable;
import com.hivemc.chunker.scheduling.function.ThrowableSupplier;
import com.hivemc.chunker.scheduling.task.executor.TaskExecutor;
import com.hivemc.chunker.util.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * A task is a function that can execute and then be chained with other tasks.
 * Task acts similar to CompletableFuture but allows asynchronous scheduling, defining of a name and weight.
 *
 * @param <O> the result this task produces.
 */
public interface Task<O> {
    /**
     * Create a task which is the combination of several tasks completing.
     *
     * @param tasks the tasks which should be waited for.
     * @param <T>   the type which the input tasks return.
     * @return a FutureTask which is completed when all the tasks provided complete, the results are provided in a list.
     */
    @SafeVarargs
    static <T> FutureTask<List<T>> join(Task<T>... tasks) {
        return join(List.of(tasks));
    }

    /**
     * Create a task which is the combination of several tasks completing.
     *
     * @param tasks the tasks which should be waited for.
     * @param <T>   the type which the input tasks return.
     * @return a FutureTask which is completed when all the tasks provided complete, the results are provided in a list.
     */
    @SuppressWarnings("unchecked")
    static <T> FutureTask<List<T>> join(Collection<? extends Task<? extends T>> tasks) {
        final CompletableFuture<? extends T>[] futures = new CompletableFuture[tasks.size()];
        int i = 0;
        for (Task<? extends T> task : tasks) {
            futures[i++] = task.future();
        }

        // Create a future task which runs the tasks then combines the results
        return new FutureTask<>(CompletableFuture.allOf(futures).thenApply((ignored) -> {
            ArrayList<T> results = new ArrayList<>(futures.length);
            for (CompletableFuture<? extends T> future : futures) {
                try {
                    results.add(future.get());
                } catch (Throwable t) {
                    SneakyThrows.throwException(t);
                }
            }
            return results;
        }));
    }

    /**
     * Create a task which is the combination of several tasks completing and being unwrapped.
     *
     * @param tasks the wrapped tasks which should be waited for.
     * @param <T>   the type which the input tasks return.
     * @return a FutureTask which is completed when all the tasks provided complete, the results are provided in a list.
     */
    @SuppressWarnings("unchecked")
    static <T> FutureTask<List<T>> flatJoin(Collection<? extends Task<? extends Collection<? extends T>>> tasks) {
        final CompletableFuture<? extends Collection<? extends T>>[] futures = new CompletableFuture[tasks.size()];
        int i = 0;
        for (Task<? extends Collection<? extends T>> task : tasks) {
            futures[i++] = task.future();
        }

        // Create a future task which runs the tasks then combines the results
        return new FutureTask<>(CompletableFuture.allOf(futures).thenApply((ignored) -> {
            ArrayList<T> results = new ArrayList<>();
            for (CompletableFuture<? extends Collection<? extends T>> future : futures) {
                try {
                    results.addAll(future.get());
                } catch (Throwable t) {
                    SneakyThrows.throwException(t);
                }
            }
            return results;
        }));
    }

    /**
     * Create a task which represents when a nested task completes.
     *
     * @param wrappedTask the task which returns a task.
     * @param <T>         the type of the inner tasks result.
     * @return a future task which completes when the nested task completes.
     */
    static <T> FutureTask<T> unwrap(Task<Task<T>> wrappedTask) {
        return new FutureTask<>(wrappedTask.future().thenCompose(Task::future));
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param runnable the runnable to execute asynchronously.
     * @return a progressive task wrapping the runnable.
     */
    static ProgressiveTask<Void> async(String name, TaskWeight weight, ThrowableRunnable runnable) {
        WrappedTask<Object, Void> task = new WrappedTask<>(name, weight, runnable);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return task;
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param supplier the supplier to execute asynchronously.
     * @param <T>      the type which is returned by the supplier.
     * @return a progressive task wrapping the supplier.
     */
    static <T> ProgressiveTask<T> async(String name, TaskWeight weight, ThrowableSupplier<T> supplier) {
        WrappedTask<Object, T> task = new WrappedTask<>(name, weight, supplier);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return task;
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param function the function to execute asynchronously.
     * @param constant a constant parameter to provide as input to the function.
     * @param <T>      the type which is provided as an input.
     * @param <U>      the type which is returned by the function.
     * @return a progressive task wrapping the function.
     */
    static <T, U> ProgressiveTask<U> async(String name, TaskWeight weight, ThrowableFunction<T, U> function, T constant) {
        WrappedTask.WrappedTaskConstant<T, U> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return task;
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param consumer the consumer to execute asynchronously.
     * @param constant a constant parameter to provide as input to the function.
     * @param <T>      the type which is provided as an input.
     * @return a progressive task wrapping the consumer.
     */
    static <T> ProgressiveTask<Void> asyncConsume(String name, TaskWeight weight, ThrowableConsumer<? super T> consumer, T constant) {
        WrappedTask.WrappedTaskConstant<? super T, Void> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, consumer);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return task;
    }

    /**
     * Create and run several asynchronous tasks based on a collection.
     *
     * @param name       the name for the task.
     * @param taskWeight the weight of each task relative to its siblings.
     * @param consumer   the consumer to use for each input.
     * @param inputs     the inputs to provide to the consumer, generating a task for each input.
     * @param <T>        the type of the input.
     * @return a task which is completed when all the input tasks have completed.
     */
    static <T> FutureTask<Void> asyncConsumeForEach(String name, TaskWeight taskWeight, ThrowableConsumer<? super T> consumer, Collection<T> inputs) {
        List<Task<Void>> tasks = new ArrayList<>(inputs.size());

        // Create the tasks and schedule them
        for (T input : inputs) {
            tasks.add(asyncConsume(name, taskWeight, consumer, input));
        }

        // Join the results and combine them
        return join(tasks).then(null);
    }

    /**
     * Create and run several asynchronous tasks based on an array.
     *
     * @param name       the name for the task.
     * @param taskWeight the weight of each task relative to its siblings.
     * @param consumer   the consumer to use for each input.
     * @param inputs     the inputs to provide to the consumer, generating a task for each input.
     * @param <T>        the type of the input.
     * @return a task which is completed when all the input tasks have completed.
     */
    static <T> FutureTask<Void> asyncConsumeForEach(String name, TaskWeight taskWeight, ThrowableConsumer<? super T> consumer, T[] inputs) {
        List<Task<Void>> tasks = new ArrayList<>(inputs.length);

        // Create the tasks and schedule them
        for (T input : inputs) {
            tasks.add(asyncConsume(name, taskWeight, consumer, input));
        }

        // Join the results and combine them
        return join(tasks).then(null);
    }

    /**
     * Create and run several asynchronous tasks based on a collection.
     *
     * @param name       the name for the task.
     * @param taskWeight the weight of each task relative to its siblings.
     * @param function   the function to use for each input.
     * @param inputs     the inputs to provide to the consumer, generating a task for each input.
     * @param <T>        the type of the input.
     * @return a task which is completed when all the input tasks have completed, providing the results as a list.
     */
    static <T, U> FutureTask<List<U>> asyncForEach(String name, TaskWeight taskWeight, ThrowableFunction<T, U> function, Collection<T> inputs) {
        List<Task<U>> tasks = new ArrayList<>(inputs.size());

        // Create the tasks and schedule them
        for (T input : inputs) {
            tasks.add(async(name, taskWeight, function, input));
        }

        // Join the results and combine them
        return join(tasks);
    }

    /**
     * Create and run several asynchronous tasks based on an array.
     *
     * @param name       the name for the task.
     * @param taskWeight the weight of each task relative to its siblings.
     * @param function   the function to use for each input.
     * @param inputs     the inputs to provide to the consumer, generating a task for each input.
     * @param <T>        the type of the input.
     * @return a task which is completed when all the input tasks have completed, providing the results as a list.
     */
    @SuppressWarnings("unchecked")
    static <T, U> FutureTask<U[]> asyncForEach(String name, TaskWeight taskWeight, ThrowableFunction<T, U> function, IntFunction<U[]> arrayConstructor, T... inputs) {
        Task<U>[] tasks = new WrappedTask.WrappedTaskConstant[inputs.length];

        // Create the tasks and schedule them
        for (int i = 0; i < inputs.length; i++) {
            tasks[i] = async(name, taskWeight, function, inputs[i]);
        }

        // Join the results and combine them
        return new FutureTask<>(join(tasks).future().thenApply((results) -> results.toArray(arrayConstructor)));
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param supplier the supplier to execute asynchronously.
     * @param <T>      the type which is returned by the supplier.
     * @return a future task which is completed when the returned supplier task has been completed.
     */
    static <T> FutureTask<T> asyncUnwrap(String name, TaskWeight weight, ThrowableSupplier<Task<T>> supplier) {
        WrappedTask<Object, Task<T>> task = new WrappedTask<>(name, weight, supplier);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return unwrap(task);
    }

    /**
     * Create and run an asynchronous task as a child of the current task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to its siblings.
     * @param constant the constant to provide to the function.
     * @param function the supplier to execute asynchronously.
     * @param <T>      the type which is provided to the function.
     * @param <U>      the type which is returned by the function.
     * @return a future task which is completed when the returned function task has been completed.
     */
    static <T, U> FutureTask<U> asyncUnwrap(String name, TaskWeight weight, ThrowableFunction<T, Task<U>> function, T constant) {
        WrappedTask.WrappedTaskConstant<T, Task<U>> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run

        // Schedule the task
        TaskExecutor taskExecutor = TaskExecutor.currentExecutor();
        task.setupFuture(taskExecutor.execute(task, task.getPriority()));
        return unwrap(task);
    }

    /**
     * Create a new environment for scheduling tasks also setting the executor for the current thread.
     *
     * @param name             the name to use for the environment.
     * @param workerThreads    the number of threads to use.
     * @param exceptionHandler the exception handler to use if a task produces an exception.
     * @param signalConsumer   a consumer which can accept signals from tasks.
     * @return the new environment which has been started.
     */
    static Environment environment(String name, int workerThreads, @Nullable Consumer<Throwable> exceptionHandler, @Nullable BiConsumer<String, Object> signalConsumer) {
        Environment environment = new Environment(name, workerThreads, exceptionHandler, signalConsumer);

        // Start the environment (this isn't a real task)
        environment.start();

        // When the try-catch is over, it should call free
        return environment;
    }

    /**
     * Get the future which is backing this task, it is recommended to only use this when the behaviour is not otherwise
     * provided as other methods of Task can provide functionality like progress tracking and scheduling.
     *
     * @return the future which can be chained.
     */
    CompletableFuture<O> future();

    /**
     * Send a signal to the environment.
     *
     * @param signalName  the name of the signal.
     * @param signalValue the value that is being signalled.
     */
    static void signal(String signalName, Object signalValue) {
        // Send the signal
        TaskExecutor.currentExecutor().signal(signalName, signalValue);
    }

    /**
     * Run a runnable after this task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param runnable the runnable to call when this task completes.
     * @return a progressive task wrapping the runnable.
     */
    default ProgressiveTask<Void> then(String name, TaskWeight weight, ThrowableRunnable runnable) {
        // This uses the same thread as the context
        WrappedTask<Object, Void> task = new WrappedTask<>(name, weight, runnable);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Run a supplier after this task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param supplier the supplier to call when this task completes.
     * @param <T>      the type which the supplier returns.
     * @return a progressive task wrapping the supplier.
     */
    default <T> ProgressiveTask<T> then(String name, TaskWeight weight, ThrowableSupplier<T> supplier) {
        // This uses the same thread as the context
        WrappedTask<Object, T> task = new WrappedTask<>(name, weight, supplier);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Return a constant value after this task.
     *
     * @param constant the constant value.
     * @param <T>      the type of the constant.
     * @return a future task wrapping the constant.
     */
    default <T> FutureTask<T> then(T constant) {
        return new FutureTask<>(future().thenApply(ignored -> constant));
    }

    /**
     * Run a function after this task.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param function the function to be called after this task completes.
     * @param <T>      the type which the function produces.
     * @return a progressive task wrapping the function.
     */
    default <T> ProgressiveTask<T> then(String name, TaskWeight weight, ThrowableFunction<? super O, T> function) {
        // This uses the same thread as the context
        WrappedTask<? super O, T> task = new WrappedTask<>(name, weight, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Run a function after this task with a constant input.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param function the function to be called after this task completes.
     * @param constant the constant to provide to the function as an input.
     * @param <T>      the input type of the function.
     * @param <U>      the type which the function produces.
     * @return a progressive task wrapping the function.
     */
    default <T, U> ProgressiveTask<U> then(String name, TaskWeight weight, ThrowableFunction<T, U> function, T constant) {
        WrappedTask.WrappedTaskConstant<T, U> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Consume the output of this task after it completes.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param consumer the consumer to be called after this task completes.
     * @return a progressive task wrapping the consumer.
     */
    default ProgressiveTask<Void> thenConsume(String name, TaskWeight weight, ThrowableConsumer<? super O> consumer) {
        // This uses the same thread as the context
        WrappedTask<? super O, Void> task = new WrappedTask<>(name, weight, consumer);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Consume an output after this task completes.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param consumer the consumer to be called after this task completes.
     * @param constant the constant to consume.
     * @param <T>      the type of the constant.
     * @return a progressive task wrapping the consumer.
     */
    default <T> ProgressiveTask<Void> thenConsume(String name, TaskWeight weight, ThrowableConsumer<T> consumer, T constant) {
        WrappedTask.WrappedTaskConstant<T, Void> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, consumer);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return task;
    }

    /**
     * Run a supplier after this task and unwrap the result.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param supplier the supplier to call when this task completes.
     * @param <T>      the type which is unwrapped from the task that the supplier returns.
     * @return a future task which is completed when the task is unwrapped (after the current task and supplier).
     */
    default <T> FutureTask<T> thenUnwrap(String name, TaskWeight weight, ThrowableSupplier<Task<T>> supplier) {
        // This uses the same thread as the context
        WrappedTask<Object, Task<T>> task = new WrappedTask<>(name, weight, supplier);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return unwrap(task);
    }

    /**
     * Run a function after this task and unwrap the result.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param function the function to call when this task completes.
     * @param <T>      the type which is unwrapped from the task that the function returns.
     * @return a future task which is completed when the task is unwrapped (after the current task and function).
     */
    default <T> FutureTask<T> thenUnwrap(String name, TaskWeight weight, ThrowableFunction<? super O, Task<T>> function) {
        // This uses the same thread as the context
        WrappedTask<? super O, Task<T>> task = new WrappedTask<>(name, weight, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return unwrap(task);
    }

    /**
     * Run a function after this task with a constant input and unwrap the result.
     *
     * @param name     the name for the task.
     * @param weight   the weight of the task relative to the current task.
     * @param function the function to call when this task completes.
     * @param constant the constant to provide to the function.
     * @param <T>      the type of the constant which the function takes as an input.
     * @param <U>      the type which is unwrapped from the task that the function returns.
     * @return a future task which is completed when the task is unwrapped (after the current task and function).
     */
    default <T, U> FutureTask<U> thenUnwrap(String name, TaskWeight weight, ThrowableFunction<T, Task<U>> function, T constant) {
        WrappedTask.WrappedTaskConstant<T, Task<U>> task = new WrappedTask.WrappedTaskConstant<>(name, weight, constant, function);
        task.setupParent(); // Setup parent before it's possible for the task to be run
        task.setupFuture(future().thenApply(task));
        return unwrap(task);
    }

    /**
     * The current state of the task.
     */
    enum TaskStatus {
        PENDING,
        STARTED,
        DONE
    }
}
