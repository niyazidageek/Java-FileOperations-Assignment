package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    static Path systemPath = Paths.get(System.getProperty("user.dir"));

    static Path dest = systemPath.resolve(Paths.get("dest"));

    public static void main(String[] args) throws IOException {

        if(!Files.exists(dest)){
            new File(dest.toString()).mkdirs();
        }

        for (var path:args) {
            Path _path = systemPath.resolve(Paths.get(path));
            if(!Files.exists(_path) || Files.isDirectory(_path)) continue;
            System.out.println(String.format(">Started: %s...", path));
            try{
                Files.copy(_path, dest.resolve(_path.getFileName()));
            }
            catch (FileAlreadyExistsException e){
                System.out.println(String.format(">FILE '%s' already exists!", path));
                continue;
            }
            catch (Exception e){
                System.out.println(String.format(">An unexpected error occurred while copying FILE: %s. Continuing...", path));
                continue;
            }
            System.out.println(String.format(">Finished FILE: %s", path));
            System.out.println(String.format(">Total %s were copied!", getReadableFileSize(Files.size(_path))));
        }
    }

    private static String getReadableFileSize(long bytes){
        if(bytes/1024<1){
            return String.format("%,d B", bytes);
        }
        else if(bytes/1000000<1){
            return String.format("%,d KB", bytes/1024);
        }
        else{
            return String.format("%,d MB", bytes/1000000);
        }
    }
}
