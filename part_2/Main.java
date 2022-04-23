import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        Path systemPath = Paths.get(System.getProperty("user.dir"));

        if(args.length<=1){
            System.out.println("You should enter at least 2 arguments!");
            return;
        }

        Path dest = systemPath.resolve(Paths.get(args[args.length-1]));

        if(!Files.exists(dest)){
            new File(dest.toString()).mkdirs();
        }

        for (int i = 0; i < args.length-1; i++) {
            Path _path = systemPath.resolve(Paths.get(args[i]));
            FileManager.copyFilesRecursion(_path, dest);
        }
    }
}


class FileManager{

    private static Path dest;

    private static void copyFilesRecursion(Path _path, int _depth, Path _prevPath)  {
        if(dest==null){
            System.out.println("No destination provided!");
            return;
        }
        try{
            if(!Files.exists(_path)){
                PrintManager.printNoSuchDirectoryOrFile(_path, _depth);
                return;
            }
            PrintManager.printStarted(_path,_depth);
            if(Files.isDirectory(_path)){

                File tempDir = _prevPath == null
                        ? new File(dest.resolve(_path.getFileName()).toString())
                        : new File(_prevPath.resolve(_path.getFileName()).toString());

                tempDir.mkdirs();
                for (var p:Files.list(_path).collect(Collectors.toList())) {
                    try{
                        PrintManager.printStarted(p,_depth+3);
                        if(Files.isDirectory(p)){
                            File _tempDir = new File(tempDir.toPath().resolve(p.getFileName()).toString());
                            _tempDir.mkdirs();
                            for (var a:Files.list(p).collect(Collectors.toList())) {
                                copyFilesRecursion(a, _depth+6,_tempDir.toPath());
                            }
                            PrintManager.printFinished(p,_depth+3,"FOLDER");
                        }
                        else {
                            Files.copy(p, tempDir.toPath().resolve(p.getFileName()));
                            PrintManager.printFinished(p,_depth+3,"FILE");
                            PrintManager.printTotal(p,_depth+3);
                        }
                    }
                    catch(FileAlreadyExistsException e){
                        PrintManager.printFileAlreadyExists(p,_depth);
                        continue;
                    }
                    catch (Exception e){
                        PrintManager.printException(p,_depth);
                        continue;
                    }
                }
                PrintManager.printFinished(_path,_depth,"FOLDER");
            }
            else{
                Files.copy(_path, _prevPath==null
                        ?dest.resolve(_path.getFileName())
                        :_prevPath.resolve(_path.getFileName()));

                PrintManager.printFinished(_path,_depth,"FILE");
                PrintManager.printTotal(_path, _depth);
            }
        }catch (FileAlreadyExistsException e){
            PrintManager.printFileAlreadyExists(_path,_depth);
        }
        catch (Exception e){
            PrintManager.printException(_path,_depth);
        }
    }

    public static void copyFilesRecursion(Path _path, Path _dest){
        dest = _dest;
        copyFilesRecursion(_path, 0, null);
    }
}

class PrintManager{

    private static Path systemPath = Paths.get(System.getProperty("user.dir"));

    private static void generateSpace(int depth){
        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }
    }

    public static void printNoSuchDirectoryOrFile(Path path, int depth){
        generateSpace(depth);
        System.out.println(String.format(">There is no such file or directory: %s...",systemPath.relativize(path)));
    }

    public static void printStarted(Path path, int depth){
        generateSpace(depth);
        System.out.println(String.format(">Started: %s...",systemPath.relativize(path)));
    }

    public static void printFinished(Path path, int depth, String type){
        generateSpace(depth);
        switch (type){
            case "FILE":
                System.out.println(String.format(">Finished: FILE %s",systemPath.relativize(path)));
                break;
            case "FOLDER":
                System.out.println(String.format(">Finished FOLDER: %s",systemPath.relativize(path)));
                System.out.println();
                break;
            default:
                break;
        }
    }

    public static void printTotal(Path path, int depth) throws IOException {
        generateSpace(depth);
        System.out.println(String.format(">Total %s were copied!", getReadableFileSize(Files.size(path))));
    }

    public static void printFileAlreadyExists(Path path, int depth){
        generateSpace(depth);
        System.out.println(String.format(">FILE '%s' already exists!", path));
    }

    public static void printException(Path path, int depth){
        generateSpace(depth);
        System.out.println(String.format(">An unexpected error occurred while copying FILE: %s. Continuing...", path));
    }

    private static String getReadableFileSize(long bytes){
        if(bytes/1024<1){
            return String.format("%,dB", bytes);
        }
        else if(bytes/1000000<1){
            return String.format("%,dKB", bytes/1024);
        }
        else{
            return String.format("%,dMB", bytes/1000000);
        }
    }
}
