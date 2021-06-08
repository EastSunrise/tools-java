package wsg.tools.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
     * A process killer to kill the process with a shutdown hook. It's useful when the jvm execution
     * is shut down during an ongoing process.
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
        this.args = args.clone();
    }

    /**
     * Executes the process with the given arguments.
     *
     * @throws IOException If the process call fails.
     */
    public void execute() throws IOException {
        String[] cmd = new String[args.length + 1];
        cmd[0] = executor.getExecutablePath();
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
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException t) {
                log.warn("Error closing input stream", t);
            }
            inputStream = null;
        }
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException t) {
                log.warn("Error closing output stream", t);
            }
            outputStream = null;
        }
        if (null != errorStream) {
            try {
                errorStream.close();
            } catch (IOException t) {
                log.warn("Error closing error stream", t);
            }
            errorStream = null;
        }
        if (null != process) {
            process.destroy();
            process = null;
        }
        if (null != killer) {
            Runtime.getRuntime().removeShutdownHook(killer);
            killer = null;
        }
    }

    /**
     * Return the exit value of the process If the process is not yet terminated, it waits for the
     * termination of the process
     *
     * @return exit value
     */
    public int exitValue() {
        Objects.requireNonNull(process, "The process has been destroyed.");
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

    private static class KillThread extends Thread {

        private final Process process;

        KillThread(Process process) {
            super();
            this.process = process;
        }

        @Override
        public void run() {
            process.destroy();
        }
    }
}
