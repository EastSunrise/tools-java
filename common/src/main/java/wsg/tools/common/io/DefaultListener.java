package wsg.tools.common.io;

import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.MultimediaInfo;

/**
 * Default implementation of {@link EncoderProgressListener}.
 *
 * @author Kingen
 * @since 2020/10/13
 */
@Slf4j
class DefaultListener implements EncoderProgressListener {

    private long start = System.currentTimeMillis();

    @Override
    public void sourceInfo(MultimediaInfo info) {
        log.info("Source info: {}", info);
        start = System.currentTimeMillis();
    }

    @Override
    public void progress(int permil) {
        if (permil == 0) {
            log.info("Starting...");
            return;
        }
        long left = (System.currentTimeMillis() - start) * (1000 - permil) / permil;
        log.info(String.format("Encoding: %.1f%%, left: %s", permil / 10F, printTime(left)));
    }

    private String printTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes %= 60;
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("h");
        }
        if (minutes > 0) {
            builder.append(minutes).append("min");
        }
        if (seconds > 0) {
            builder.append(seconds).append("s");
        }
        return builder.toString();
    }

    @Override
    public void message(String message) {
        log.error("Msg: {}", message);
    }
}
