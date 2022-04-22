import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    static Path systemPath = Paths.get(System.getProperty("user.dir"));

    static Path dest;

    public static void main(String[] args) throws IOException {

        dest = systemPath.resolve(Paths.get(args[args.length-1]));

        if(!Files.exists(dest)){
            new File(dest.toString()).mkdirs();
        }

        for (int i = 0; i < args.length-1; i++) {
            Path _path = systemPath.resolve(Paths.get(args[i]));
            _copyFilesRecursion(_path);
        }
    }

    private static void _copyFilesRecursion(Path _path, int _depth, Path... _prevPath) throws IOException {
        if(!Files.exists(_path)) return;
        printStarted(_path,_depth);
        if(Files.isDirectory(_path)){
            File tempDir = Arrays.stream(_prevPath).count()==0
                    ? new File(dest.resolve(_path.getFileName()).toString())
                    : new File(_prevPath[0].resolve(_path.getFileName()).toString());
            tempDir.mkdirs();
            for (var p:Files.list(_path).collect(Collectors.toList())) {
                printStarted(p,_depth+3);
                if(Files.isDirectory(p)){
                    try{
                        File _tempDir = new File(tempDir.toPath().resolve(p.getFileName()).toString());
                        _tempDir.mkdirs();
                        for (var a:Files.list(p).collect(Collectors.toList())) {
                            _copyFilesRecursion(a, _depth+6,_tempDir.toPath());
                        }
                    }
                    catch (Exception e){
                        continue;
                    }
                    printFinished(p,_depth+3,"FOLDER");
                }
                else {
                    Files.copy(p, tempDir.toPath().resolve(p.getFileName()));
                    printFinished(p,_depth+3,"FILE");
                    printTotal(p,_depth+3);
                }
            }
            printFinished(_path,_depth,"FOLDER");
        }
        else{
            Files.copy(_path, Arrays.stream(_prevPath).count()==0
                    ?dest.resolve(_path.getFileName())
                    :_prevPath[0].resolve(_path.getFileName()));
            printFinished(_path,_depth,"FILE");
            printTotal(_path, _depth);
        }
    }

    private static void _copyFilesRecursion(Path _path, Path... _prevPath) throws IOException {
        _copyFilesRecursion(_path,0,_prevPath);
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

    private static void printStarted(Path path, int depth){
        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }
        System.out.println(String.format(">Started: %s...",systemPath.relativize(path)));
    }

    private static void printFinished(Path path, int depth, String type){
        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }
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

    private static void printTotal(Path path, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }
        System.out.println(String.format(">Total %s were copied!", getReadableFileSize(Files.size(path))));
    }
}
