package tpiskorski.machinator.config.io;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileChecker {
    boolean notExists(String filePath) {
        return !Files.exists(Paths.get(filePath));
    }
}
