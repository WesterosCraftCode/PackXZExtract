package com.dscalzi.packxzextract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.regex.Pattern;

import org.tukaani.xz.XZInputStream;

public class PXZEMain {

    private static final Pattern xz = Pattern.compile("(?i)\\.xz");
    private static final Pattern pack = Pattern.compile("(?i)\\.pack");

    /**
     * Accepted arguments:<br><br>
     *
     * <strong>-packxz filepath1.pack.xz,filepath2.pack.xz,...</strong><br>
     * <strong>-xz filepath1.xz,filepath2.xz,...</strong><br>
     * <strong>-pack filepath1.pack,filepath2.pack,...</strong><br>
     */
    public static void main(String[] args){
        List<File> xzQueue = new ArrayList<>();
        List<File> unpackQueue = new ArrayList<>();
        List<File> packXZQueue = new ArrayList<>();

        for(int i=0; i<args.length; ++i){
            if(args[i].equalsIgnoreCase("-packxz")){
                if(i+1<args.length){
                    ++i;
                    String[] paths = args[i].split(",");
                    for(String s : paths){
                        packXZQueue.add(new File(s));
                    }
                }
            } else if(args[i].equalsIgnoreCase("-xz")){
                if(i+1<args.length){
                    ++i;
                    String[] paths = args[i].split(",");
                    for(String s : paths){
                        xzQueue.add(new File(s));
                    }
                }
            } else if(args[i].equalsIgnoreCase("-pack")){
                if(i+1<args.length){
                    ++i;
                    String[] paths = args[i].split(",");
                    for(String s : paths){
                        unpackQueue.add(new File(s));
                    }
                }
            }
        }

        for(File f : packXZQueue) unpack(extractXZ(f));
        for(File f : xzQueue) extractXZ(f);
        for(File f : unpackQueue) unpack(f);
    }

    private static File extractXZ(File compressedFile) {
        if(compressedFile == null) return null;
        File unpacked = new File(compressedFile.getParentFile(), xz.matcher(compressedFile.getName()).replaceAll(""));
        try(InputStream input = new XZInputStream(new FileInputStream(compressedFile));
            OutputStream output = new FileOutputStream(unpacked)){

            byte[] buf = new byte[65536];

            int read = input.read(buf);
            while (read >= 1) {
                output.write(buf,0,read);
                read = input.read(buf);
            }
        } catch (Exception e) {
            System.err.println("Unable to extract xz: " + e.getMessage());
            return null;
        } finally {
            compressedFile.delete();
        }
        System.out.println("Successfully extracted " + compressedFile.getName());
        return unpacked;
    }
    
    private static File unpack(File compressedFile) {
        if(compressedFile == null) return null;
        File unpacked = new File(compressedFile.getParentFile(), pack.matcher(compressedFile.getName()).replaceAll(""));
        try(JarOutputStream jarStream = new JarOutputStream(new FileOutputStream(unpacked))){
            Pack200.newUnpacker().unpack(compressedFile, jarStream);
        } catch (Exception e) {
            System.err.println("Unable to unpack: " + e.getMessage());
            return null;
        } finally {
            compressedFile.delete();
        }
        System.out.println("Successfully unpacked " + compressedFile.getName());
        return unpacked;
    }

}
