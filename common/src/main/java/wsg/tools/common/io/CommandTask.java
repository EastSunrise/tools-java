package wsg.tools.common.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A task created by an executor with the given arguments.
 *
 * @author Kingen
 * @since 2020/12/1
 */
@Slf4j
public class CommandTask {

    /**
     * The executor to execute the task.
     */
    private final CommandExecutor executor;

    /**
     * Arguments of the task.
     */
    private final String[] args;

    /**
     * The process representing the execution.
     */
    private Process process;

    /**
     * A process killer to kill the process with a shutdown hook.
     * It's useful when the jvm execution is shut down during
     * an ongoing process.
     */
    private KillThread killer;

    /**
     * A stream writing in the process standard input channel.
     */
    private OutputStream outputStream;

    /**
     * A stream reading from the process standard output channel.
     */
    private InputStream inputStream;

    /**
     * A stream reading from the process standard error channel.
     */
    private InputStream errorStream;

    public CommandTask(CommandExecutor executor, String... args) {
        this.executor = executor;
        this.args = args;
    }

    /**
     * Executes the process with the given arguments.
     *
     * @throws IOException If the process call fails.
     */
    public void execute() throws IOException {
        String[] cmd = new String[args.length + 1];
        cmd[0] = executor.executablePath;
        System.arraycopy(args, 0, cmd, 1, args.length);
        if (log.isDebugEnabled()) {
            log.debug("Start to execute '{}'.", StringUtils.join(cmd, ' '));
        }
        Runtime runtime = Runtime.getRuntime();
        process = runtime.exec(cmd);
        killer = new KillThread(process);
        runtime.addShutdownHook(killer);
        inputStream = process.getInputStream();
        outputStream = process.getOutputStream();
        errorStream = process.getErrorStream();
    }

    /**
     * If there's a task in progress, it kills it.
     */
    public void destroy() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable t) {
                log.warn("Error closing input stream", t);
            }
            inputStream = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Throwable t) {
                log.warn("Error closing output stream", t);
            }
            outputStream = null;
        }
        if (errorStream != null) {
            try {
                errorStream.close();
            } catch (Throwable t) {
                log.warn("Error closing error stream", t);
            }
            errorStream = null;
        }
        if (process != null) {
            process.destroy();
            process = null;
        }
        if (killer != null) {
            Runtime.getRuntime().removeShutdownHook(killer);
            killer = null;
        }
    }

    /**
     * Return the exit value of the process
     * If the process is not yet terminated, it waits for the termination
     * of the process
     *
     * @return exit value
     */
    public int exitValue() {
        if (process == null) {
            throw new NullPointerException("The process has been destroyed.");
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            log.warn("Interrupted during waiting on process, forced shutdown?", ex);
        }
        return process.exitValue();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public InputStream getErrorStream() {
        return errorStream;
    }

    static class KillThread extends Thread {
        private final Process process;

        KillThread(Process process) {this.process = process;}

        @Override
        public void run() {
            process.destroy();
        }
    }
}
