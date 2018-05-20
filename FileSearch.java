import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;
import java.util.*;
import java.nio.channels.*;

public class FileSearch {

    public static final int TYPE_FILE_OR_DIR = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_DIR = 3;

    public File[] listFiles(String directoryPath, String fileName) {
        // ワイルドカード文字として*を正規表現に変換
        if (fileName != null) {
            fileName = fileName.replace(".", "\\.");
            fileName = fileName.replace("*", ".*");
        }
        return listFiles(directoryPath, fileName, TYPE_FILE, true, 0);
    }

    
    public File[] listFiles(String directoryPath, 
            String fileNamePattern, int type, 
            boolean isRecursive, int period) {      //実際の検索部分
        
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
          throw new IllegalArgumentException
          (dir.getAbsolutePath());
        }
        File[] files = dir.listFiles();     // その出力
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try{
                addFile(type, fileNamePattern, set, file, period);      // 再帰的に検索＆ディレクトリならば再帰的にリストに追加
                if (isRecursive && file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), fileNamePattern, 
                                type, isRecursive, period);
                }
            }catch(NullPointerException e){
                String sp = " ";
                System.out.println("パス：" + file.getAbsolutePath() + " " + sp +"は、参照できません。");
            }
        }
        return (File[]) set.toArray(new File[set.size()]);
    }

    private void addFile(int type, String match, TreeSet set,
            File file,int period) {
        switch (type) {
        case TYPE_FILE:
            if (!file.isFile()) {
                return;
            }
            break;
        case TYPE_DIR:
            if (!file.isDirectory()) {
                return;
            }
            break;
        }
        if (match != null && !file.getName().matches(match)) {
            return;
        }
        // 全ての条件に該当する場合リストに格納
        set.add(file);

    }

    /** アルファベット順に並べるためTreeSetを使用。 */
    private TreeSet set = new TreeSet();

    public void clear(){
    	set.clear();
    }

    public static void main(String[] args){
        try{
            System.out.println("検索を行うファイルパスは？");
            Scanner scan = new Scanner(System.in);
            String path = scan.next();
            FileSearch search = new FileSearch();

            System.out.println("検索したい名称は？");
            scan = new Scanner(System.in);
            String Fname = scan.next();     //検索ワード
            Fname ="*"+ Fname + "*";
            File[] files = search.listFiles(path, Fname);
            printFileList(files);
            search.clear();
        }catch(PatternSyntaxException e){
            System.out.println("検索できない記号が含まれています。");
        }catch(IllegalArgumentException e){
            System.out.println("ディレクトリが見つかりません");
        }    

    }

    private static void printFileList(File[] files){    //ファイル検索部分
        if(files.length == 0 ){
            System.out.println("検索したファイルは存在しません");
        }else{
            System.out.println("コピー先のファイルパスは？");
            Scanner scan = new Scanner(System.in);
            String cpath = scan.next();
            String mine = System.getProperty("user.dir");
            File name = new File(mine + "\\copy.txt");
            for(int i = 0; i < files.length; i++){
                File file = files[i];
                try{
                    FileWriter fw = new FileWriter(name,true);
                    fw.write(file.getAbsolutePath() + "\r\n");
                    fw.close();
                }catch(IOException e){
                    System.out.println(e);
                }
            }
            try{
                FileReader fr = null;
                BufferedReader br = null;
                fr = new FileReader(name);
                br = new BufferedReader(fr); 
                String cfile;
                while ((cfile= br.readLine()) != null) {
                    Filecopy(cpath,cfile);
                }
                br.close();
                fr.close();
            }catch(IOException e){
                System.out.println(e);
            }
            name.delete();
        }
        
    }

    private static void Filecopy(String cpath , String cfile) {
        File in = new File(cfile);
        File out = new File(cpath + "\\" + in.getName());
        try {
            FileSearch.copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(),outChannel);
        } 
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
}