package wsg.tools.common.io;

import java.io.IOException;

/**
 * An executor for command lines.
 *
 * @author Kingen
 * @since 2020/12/1
 */
public class CommandExecutor {

    /**
     * path of the executable.
     */
    protected final String executablePath;

    public CommandExecutor(String executablePath) {
        // todo if executable
        this.executablePath = executablePath;
    }

    public CommandTask createTask(String... args) {
        return new CommandTask(this, args);
    }

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
