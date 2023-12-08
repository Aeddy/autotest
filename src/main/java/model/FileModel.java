package model;

import java.io.File;

public class FileModel {

     public File file;

     public String md5;

     public FileModel(File file, String md5) {

         this.file = file;

         this.md5 = md5;

     }

     public File getFile() {

         return file;

     }

     public void setFile(File file) {

         this.file = file;

     }

     public String getMd5() {

         return md5;

     }

     public void setMd5(String md5) {

         this.md5 = md5;

     }

 }