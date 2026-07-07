package com.hivemc.chunker.conversion.encoding.preview;

import java.io.File;
import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Helpers for opening a Bedrock world's LevelDB defensively while generating preview data.
 *
 * <p>iq80 LevelDB guards a database directory with a single {@code db/LOCK} file and refuses to
 * open it twice ({@link OverlappingFileLockException} / "Unable to acquire lock"). During preview
 * generation the database is opened repeatedly — once for the metadata pass and again for every
 * region tile — so a stale LOCK left by a crashed run, or the brief window where the OS has not yet
 * released the handle from the previously closed reader, can make an open fail. Clearing the stale
 * LOCK and retrying lets the preview survive that transient contention instead of failing the pass.
 */
final class PreviewLevelDb {
    /** Number of times to attempt the action before giving up. */
    private static final int MAX_ATTEMPTS = 4;
    /** Delay between attempts; long enough for the OS to release a just-closed file handle. */
    private static final long RETRY_DELAY_MS = 120L;

    private PreviewLevelDb() {
    }

    /**
     * Run an action that opens / reads the LevelDB at {@code dbDir}, clearing a stale {@code LOCK}
     * before each attempt and retrying when the failure is a lock-acquisition problem.
     *
     * @param dbDir  the {@code db} directory of the world.
     * @param action the work to perform (typically opening the database).
     * @param <T>    the result type.
     * @return the action's result.
     * @throws IOException if the action keeps failing or fails for a non-lock reason.
     */
    static <T> T withLockRetry(File dbDir, Callable<T> action) throws IOException {
        Throwable last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            // Clear a LOCK left behind by a crashed reader (or one still being released) before trying.
            new File(dbDir, "LOCK").delete();
            try {
                return action.call();
            } catch (Throwable t) {
                last = t;

                // Only a lock-acquisition failure is worth retrying; anything else (corrupt data, a
                // read timeout, ...) won't be fixed by waiting, so fail fast.
                if (attempt == MAX_ATTEMPTS || !isLockFailure(t)) break;

                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (last instanceof IOException io) throw io;
        throw new IOException("Failed to access LevelDB at " + dbDir, last);
    }

    /**
     * @return true if the throwable (or one of its causes) is an iq80 LevelDB lock-acquisition error.
     */
    static boolean isLockFailure(Throwable throwable) {
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            if (cause instanceof OverlappingFileLockException) return true;
            String message = cause.getMessage();
            if (message != null && message.toLowerCase(Locale.ROOT).contains("acquire lock")) return true;
        }
        return false;
    }
}
