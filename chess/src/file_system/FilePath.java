package file_system;
import java.io.File;
/**
 * FilePath, contains path to project root, folder with images and saved games
 */
public class FilePath {

    /**
     * @return path to folder of project root (ends with "chess")
     */
    private final static String getCWD() {
        String cwd = new File("").getAbsolutePath() + "\\chess";
        // Check assets
        File assets = new File(cwd);
        if (!(assets.exists() && assets.isDirectory())) {
            System.out.println("Unable to initialize cwd, check project location, got: " + cwd);
            System.exit(1);
        }
        return cwd;
    }

    /**
     * @param path to file (absolute)
     * @return True if file exists, false otherwise
     */
    public final static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static final String CWD    = getCWD();
    public static final String ASSETS = CWD + "\\data\\assets";
    public static final String SAVES  = CWD + "\\data\\saves";
}

