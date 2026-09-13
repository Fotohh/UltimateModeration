package me.xaxis.ultimatemoderation.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.*;

public class AtomicWrite {

    private AtomicWrite() {
        throw new IllegalArgumentException("Do not instantiate Atomic Write, it is a utility class.");
    }

    public static void save(Path path, String data) throws IOException {

        Path target = path.toAbsolutePath();
        Path parent = target.getParent();

        if(parent == null) {
            throw new IOException("Target path has no parent directory: " + target);
        }

        Files.createDirectories(parent);

        Path temp = Files.createTempFile(
                parent,
                target.getFileName().toString() + "." ,
                ".tmp"
        );

        boolean moved = false;

        try {

            writeDataToTemp(temp, data);

            try(FileChannel channel = FileChannel.open(
                    temp,
                    StandardOpenOption.SYNC
            )) {
                channel.force(true);
            }

            try {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch(AtomicMoveNotSupportedException e) {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            moved = true;

        } finally {
            if(!moved) {
                Files.deleteIfExists(temp);
            }
        }
    }

    private static void writeDataToTemp(Path temp, String data) throws IOException {
        Files.writeString(
                temp,
                data,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }


}
