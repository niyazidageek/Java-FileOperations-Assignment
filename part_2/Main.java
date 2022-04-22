package com.company;
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
            copyFilesRecursion(_path);
        }
    }

    private static void copyFilesRecursion(Path _path, Path... _prevPath) throws IOException {
        if(!Files.exists(_path)) return;

        if(Files.isDirectory(_path)){
            File tempDir = Arrays.stream(_prevPath).count()==0
                    ? new File(dest.resolve(_path.getFileName()).toString())
                    : new File(_prevPath[0].resolve(_path.getFileName()).toString());
            tempDir.mkdirs();
            for (var p:Files.list(_path).collect(Collectors.toList())) {
                if(Files.isDirectory(p)){
                    try{
                        File _tempDir = new File(tempDir.toPath().resolve(p.getFileName()).toString());
                        _tempDir.mkdirs();
                        for (var a:Files.list(p).collect(Collectors.toList())) {
                            copyFilesRecursion(a, _tempDir.toPath());
                        }
                    }
                    catch (Exception e){
                        continue;
                    }
                }
                else {
                    Files.copy(p, tempDir.toPath().resolve(p.getFileName()));
                }
            }
        }
        else{
            Files.copy(_path, Arrays.stream(_prevPath).count()==0
                    ?dest.resolve(_path.getFileName())
                    :_prevPath[0].resolve(_path.getFileName()));
        }
    }
}
