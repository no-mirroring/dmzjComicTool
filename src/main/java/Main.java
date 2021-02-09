import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/***
 * 注意：
 * 1.被选择的文件夹名即为漫画名
 * 2.文件夹内应该是xxx_xxx.zip文件（动漫之家漫画下载的文件就是这个格式）
 * 3.每一个zip就是一话
 * 4.生成结果保存在目标文夹同的同级文件夹
 */
public class Main {
    public static int perHua = 5;//每5话合成一卷

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
     * @throws IOException err
     */
    private static void domain(File dirS) throws IOException {
        int num = 0;//图片移动次数,用来命名图片
        /*int dirSize = Hua;//漫画话数*/
        int cishu = 1;//当前执行的话数，记录话数

        //这两个变量用来分割卷
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
            sortDir(dirs);
            File dir = dirs.get(i);//一个文件夹,即当前话

            //移动一个文件夹里图片
            for (int j = 0; j < dirs.size(); j++) {
                List<File> picList = getList(dir);//一个文件夹所有图片
                sortImageFile(picList);//排序

                //一个图片
                for (File pic : picList) {
                    File newPic = new File(desDir.getAbsolutePath() + "\\" + num + ".jpg");
                    FileUtils.moveFile(pic, newPic);
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
     * @param dir dir
     * @return fileList
     */
    public static List<File> getList(File dir) {
        List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, Objects.requireNonNull(dir.listFiles()));
        return fileList;
    }

    /**
     * 解压文件集
     *
     * @param fileList fileList
     */
    public static void unzipFiles(List<File> fileList) {
        for (File f :
                fileList) {
            ZipUtil.explode(f);
            hua++;
        }
    }

    /**
     * 排序xxx_xxx.zip
     * @param fileList fileList
     */
    public static void sortDir(List<File> fileList) {
        fileList.sort((o1, o2) -> {
            String s1 = o1.getName();
            String s2 = o2.getName();
            String s11 = s1.substring(s1.indexOf("_") + 1, s1.indexOf("."));
            String s22 = s2.substring(s2.indexOf("_") + 1, s2.indexOf("."));
            int i1 = Integer.parseInt(s11);
            int i2 = Integer.parseInt(s22);
            return i1 - i2;
        });
    }

    /**
     * 给图片文件排序
     * @param fileList fileList
     */
    public static void sortImageFile(List<File> fileList) {
        fileList.sort((o1, o2) -> {
            String s1 = o1.getName().substring(0, o1.getName().indexOf("."));
            String s2 = o2.getName().substring(0, o2.getName().indexOf("."));
            return Integer.parseInt(s1)-Integer.parseInt(s2);
        });
    }
}
