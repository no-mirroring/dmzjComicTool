import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static int perHua = 5;//每三话合成一卷

    public static String comicName = "Name";//漫画名

    public static String workSpacePath = "F:\\comic\\";//默认目录

    public static int hua=0;//漫画话数

    public static void main(String[] args) throws IOException {
        //文件夹选择
        init();

        //获取zip
        List<File> comicList = getList(new File(workSpacePath));

        //zip解压
        unzipFiles(comicList);

        //遍历文件夹
        domain(new File(workSpacePath));
    }

    /**
     * 文件夹选择器
     */
    private static void init() {
        JFileChooser jFileChooser = new JFileChooser("F:\\comic\\");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = jFileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            comicName = jFileChooser.getSelectedFile().getName();
            System.out.println("选择了" + comicName);
            workSpacePath = jFileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.exit(0);
        }

    }

    /**
     * 把漫画分卷
     * @param dirS 这个文件夹内应该包括N个子文件夹，每一个字文件夹代表一话（其中为jpg文件）
     * @throws IOException
     */
    private static void domain(File dirS) throws IOException {
        int num = 0;//图片移动次数,用来命名图片
        /*int dirSize = Hua;//漫画话数*/
        int cishu = 1;//当前执行的话数，记录话数

        //这两个变量用来创建卷
        int a = 0;
        int vol = 1;

        //存放结果的目标dir
        File desDir = new File(new File(workSpacePath).getParent() + "\\"+comicName + "第" + vol + "卷");
        desDir.mkdirs();

        //开始操作每一话
        for (int i = 0; i < hua; i++) {

            //分割卷
            if (a == perHua) {
                a = 0;
                num = 0;
                vol++;
                desDir = new File(new File(workSpacePath).getParent() +"\\"+ comicName + "第" + vol + "卷");
                desDir.mkdirs();
            }

            List<File> dirs = getList(dirS);//所有文件夹

            //按名字排序
            Collections.sort(dirs);

            File dir = dirs.get(i);//一个文件夹,即当前话

            //移动一个文件夹里图片
            for (int j = 0; j < dirs.size(); j++) {
                List<File> picList = getList(dir);//一个文件夹所有图片
                for (int k = 0; k < picList.size(); k++) {
                    File pic = picList.get(k);//一个图片
                    FileUtils.moveFile(pic, new File(desDir.getAbsolutePath() + "\\" + num + ".jpg"));
                    System.out.println("正在移动图片" + num);
                    num++;
                }
            }

            System.out.println("------第" + cishu + "话移动完成了");
            a++;
            cishu++;
        }

        FileUtils.deleteDirectory(new File(workSpacePath));
        System.out.println("!!!!!!全部完成");
    }

    /**
     * 将目录内文件转为list
     *
     * @param dir
     * @return
     */
    public static List<File> getList(File dir) {
        List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, Objects.requireNonNull(dir.listFiles()));
        return fileList;
    }

    /**
     * 解压文件集
     *
     * @param fileList
     */
    public static void unzipFiles(List<File> fileList) {
        for (File f :
                fileList) {
            ZipUtil.explode(f);
            hua++;
        }
    }
}
