import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

public class App {
    public static void main(String[] args) throws Exception {
        File directoryPath = new File("C:/Users/" + System.getProperty("user.name") + "/Pictures/VRChat");
        File[] directories = directoryPath.listFiles(File::isDirectory);
        File[] rootFiles = directoryPath.listFiles(File::isFile);

        for (File directory : directories) {
            File[] dirFiles = directory.listFiles(File::isFile);
            for (File dirFile : dirFiles) {
                if (!dirFile.getName().endsWith(".png")) {
                    continue;
                }
                HandleImage(directoryPath, dirFile);
            }
            directory.delete();
        }

        for (File rootFile : rootFiles) {
            if (!rootFile.getName().endsWith(".png")) {
                continue;
            }
            HandleImage(directoryPath, rootFile);
        }
    }

    public static void HandleImage(File rootDir, File file) throws Exception {
        Dimension dimension = GetDimension(file);
        int width = dimension.getWidth();
        int height = dimension.getHeight();
        long date = file.lastModified();

        String newFileName = "VRChat_" + date + "_" + width + "x" + height + ".png";
        String currentFileName = file.getName();
        if (currentFileName.equals(newFileName)) {
            System.out.println("File already renamed: " + file.getName());
            return;
        }

        Path temp = Files.move(Paths.get(file.getPath()), Paths.get(rootDir.getPath() + "/" + newFileName));
        if (temp == null) {
            System.out.println("Failed to move file: " + file.getPath());
            return;
        }
        System.out.println("Moved file: " + file.getPath() + " to " + rootDir.getPath() + "/" + newFileName);
    }

    public static Dimension GetDimension(File file) throws Exception {
        try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
            return new Dimension(0, 0);
        } catch (Exception e) {
            return new Dimension(0, 0);
        }
    }
}