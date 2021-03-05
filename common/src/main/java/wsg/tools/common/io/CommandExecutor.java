package wsg.tools.common.io;

import java.io.IOException;

/**
 * An executor of command lines for a given executable application.
 *
 * @author Kingen
 * @since 2020/12/1
 */
public class CommandExecutor {

    /**
     * path of the executable.
     */
    final String executablePath;

    public CommandExecutor(String executablePath) {
        // todo if executable
        this.executablePath = executablePath;
    }

    /**
     * Create a task with given arguments.
     */
    public CommandTask createTask(String... args) {
        return new CommandTask(this, args);
    }

    /**
     * Create a task and execute the task.
     */
    public int execute(String... args) throws IOException {
        CommandTask task = new CommandTask(this, args);
        try {
            task.execute();
            return task.exitValue();
        } finally {
            task.destroy();
        }
    }
}
