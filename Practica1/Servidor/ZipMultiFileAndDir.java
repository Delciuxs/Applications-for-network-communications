import java.io.*;
import java.util.zip.*;
import javax.swing.JFileChooser;
import java.util.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class ZipMultiFileAndDir {

    public static void main(String[] args) throws IOException{
        ArrayList<String> srcFiles = new ArrayList<String>();
        srcFiles.add("Archivo1.txt");
        srcFiles.add("HolaMundo.cpp");
        srcFiles.add("CarpetaPrueba");
        srcFiles.add("CarpetaPrueba2");
        ArrayList<File> allFiles = new ArrayList<File>();

        for(String srcFile : srcFiles){
            allFiles.add(new File(srcFile));
        }

        ZipMultiFileAndDir zipAux = new ZipMultiFileAndDir();

        zipAux.ZipThisFiles(allFiles, "TODO.zip");
    }



    public void ZipThisFiles(ArrayList<File> filesToZip, String nameZip) throws IOException{
        FileOutputStream fos = new FileOutputStream(nameZip);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (File fileToZip : filesToZip) {
            zipFile(fileToZip, fileToZip.getName(), zipOut);
        }
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
            System.out.print("\rZipping...");
        }
        fis.close();
    }
}