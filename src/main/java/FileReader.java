import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

  private static String rootPath;

  public static void setRootPath(String rootPath) {
    FileReader.rootPath = rootPath;
  }

  public FileReader(String rootPath) {
    this.rootPath = rootPath;
  }

  public static String readAll(String fileName) {
    try {
      return Files.readString(Paths.get(rootPath, fileName));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }
}
