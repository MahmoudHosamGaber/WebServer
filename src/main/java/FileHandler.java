import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

  private static String rootPath;

  public static void setRootPath(String rootPath) {
    FileHandler.rootPath = rootPath;
  }

  public FileHandler(String rootPath) {
    FileHandler.rootPath = rootPath;
  }

  public static String readAll(String fileName) {
    try {
      return Files.readString(Paths.get(rootPath, fileName));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void writeFile(String fileName, String content) {
    try {
      // File file = new File(rootPath + "/" + fileName);
      // file.createNewFile();
      PrintWriter writer = new PrintWriter(rootPath + "/" + fileName);
      writer.print(content);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
