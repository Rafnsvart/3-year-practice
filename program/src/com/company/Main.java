package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int flag_count = 1;
        int t_pause = 1;
        String dirOut = "D:\\TempOutPrograms\\";
        String dirIn = "D:\\TempPrograms\\";
        File fileConf = new File("D:\\Temp_Log\\Conf.txt");
        ArrayList<String> outputDirName = new ArrayList<>();
        ArrayList<Long> outputDirSpace = new ArrayList<>();
        int flag_count_temp[] = new int[2], t_pause_temp [] = new int[2];
        Date date = new Date();
        writeLog("      Start program");
        while (flag_count>0)
        {
            flag_count--;
            File out = new File(dirOut);
            File in = new File(dirIn);
            File[] filesOut = out.listFiles();
            File[] filesIn = in.listFiles();
            /*считывание файла конфигурации*/
            try(FileReader sc = new FileReader(fileConf)){
                String line;
                int i = 0;
                ArrayList<String> temp = new ArrayList<>();
                BufferedReader reader = new BufferedReader(sc);
                while((line=reader.readLine())!=null&&i<2)
                {
                    temp.add(line);
                    i++;
                }
                try{
                    flag_count_temp[0] = Integer.parseInt(temp.get(0)
                            .replaceAll("\\s", "").split("=")[1]);
                    t_pause_temp[0] = Integer.parseInt(temp.get(1)
                            .replaceAll("\\s", "").split("=")[1]);
                    if(flag_count_temp[0]==flag_count_temp[1]&&t_pause_temp[0]==t_pause_temp[1])
                    {
                        String temp_mess = "Считан файл конфигурации "+System.lineSeparator()
                                +"      Значения параметров не изменились";
                        writeLog(temp_mess);
                        if (flag_count == 0)
                            break;
                    }
                    else {
                        flag_count_temp[1] = flag_count_temp[0];
                        flag_count = flag_count_temp[0];
                        t_pause_temp[1] = t_pause_temp[0];
                        t_pause = t_pause_temp[0];
                        String temp_mess = "Считан файл конфигурации " + System.lineSeparator()
                                + "      Значения параметров: t_pause = " + t_pause + " flag_count = " + flag_count;
                        writeLog(temp_mess);
                        if (flag_count == 0)
                            break;
                    }
                }catch(Exception e)
                {
                    String temp_mess = "Ошибка в считывании значени "+System.lineSeparator()+"       "
                            +e.getMessage()+System.lineSeparator()+"      Значения параметров: t_pause = "
                            + t_pause + " flag_count = "+flag_count+System.lineSeparator()+"     "+e.getMessage();
                    writeLog(temp_mess);
                }
            }catch (Exception e)
            {
                String temp_mess = "Ошибка в открытии файла конфигурации "+System.lineSeparator()+"       "
                        +e.getMessage()+System.lineSeparator()+"      Значения параметров: t_pause = "
                        + t_pause + " flag_count = "+flag_count+System.lineSeparator()+"     "+e.getMessage();
                writeLog(temp_mess);
            }
            ///*** Если каталог "куда" производится копирования пуст, то производится перенос всех файлов
            if (filesOut.length == 0) {
                for (File i :
                        filesIn) {
                    if (isFileClosedTrue(i)) {
                        String tempString = dirOut + i.getName();
                        copy(i, tempString);
                    }
                }
            }
            else{
                for (File p : /// Параметры файлов конечной директории
                        filesOut) {
                    outputDirName.add(p.getName());
                    outputDirSpace.add(p.length());
                }
                for (File i :
                        filesIn) {
                    ///*** Если найдена копия файла
                    if (isFileClosedTrue(i)) {
                        if (contCopy(outputDirName, outputDirSpace, i)) {
                        }
                        ///*** Если файл был измене, то сохраняется копия с другим именем
                        else if (outputDirName.contains(i.getName())) {
                            String tempString = dirOut + i.getName();
                            tempString = createName(tempString, dirOut);
                            copy(i, tempString);
                        } else {
                            String tempString = dirOut + i.getName();
                            copy(i, tempString);                        }
                    }
                }
                ///*** Поиск файлов бэкапа в исходном каталоге, при их отсутсвии и старости происходит удаление
                filesOut = out.listFiles();
                for (File i:
                        filesOut){
                    if (!findCopyOutIn(i, filesIn)&&(date.getTime()-i.lastModified()>36000))
                    {
                        try{
                            String tempName = i.getAbsolutePath();
                            Long tempTime = (date.getTime()-i.lastModified())/60000;
                            i.delete();
                            String temp_mess = "Удален файл "+tempName+" по старости. Он был создан "
                                    +tempTime+" минут назад";
                            writeLog(temp_mess);
                        }catch (Exception e)
                        {
                            String temp_mess = "Файл "+i.getAbsolutePath()+" не удалось удалить"
                                    +flag_count+System.lineSeparator()+"      "+e.getMessage();
                            writeLog(temp_mess);
                        }
                    }
                }
            }
            Thread.sleep(t_pause);
            System.out.print(true);
        }
        writeLog("      End program");
    }
    ///*** Производит копирование фала "i" в директорию с именем "tempString"
    private static void copy (File i, String tempString) throws IOException {
        try(InputStream is = new FileInputStream(i))
        {
            try(OutputStream os = new FileOutputStream(tempString))
            {
                try {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    String temp_mess = "Произведено копирование файла " + i.getAbsolutePath() + " как " + tempString;
                    writeLog(temp_mess);
                }catch(Exception e)
                {
                    String temp_mess = "Не удалось произвести копирование из "+i.getAbsolutePath()+" в "
                            +tempString+System.lineSeparator()+"      "+e.getMessage();
                    writeLog(temp_mess);
                }           }
           catch(Exception e)           {
            String temp_mess = "Не удалось создать поток для конечного файла "+tempString+System.lineSeparator()
                    +"      "+e.getMessage();
            writeLog(temp_mess);
        }
        }catch(Exception e)
        {
            String temp_mess = "Не удалось открыть копируемый файл "+i.getAbsolutePath()+System.lineSeparator()
                    +"      "+e.getMessage();;
            writeLog(temp_mess);
        }
    }
    ///*** Проверяет файл на его доступность пытаясь из него прочитать фрагмент текста
    //     Возращает "true" - если доступен, "false" - если нет
    private static boolean isFileClosedTrue(File file) throws IOException {
        try(FileInputStream abc = new FileInputStream(file)) {
            int temp;
            byte[] b = new byte[1];
            temp = abc.read(b, 0, 1);
            return true;
        }catch (IOException e)
        {
            String temp_mess = "Файл " + file.getAbsolutePath()+" закрыт"+System.lineSeparator()+"      " +e.getMessage();
            writeLog(temp_mess);
            return false;
        }
    }
    ///*** Создает строку имени для уникальности названия в директории
    private static String createName(String str, String dir)
    {
        String name;
        String soname;
        if ((str.contains("."))) {
            name = str.substring(0, str.indexOf("."));
            soname = str.substring(str.indexOf("."));
        } else {
            name = str;
            soname = null;
        }
        String ret;
        int i = 1;
        while(true)
        {
            ret = name + "(" + i + ")" + ((soname!=null)? soname:"");
            File tempFile = new File(ret);
            if(!tempFile.exists())
            {
                break;
            }
            i++;
        }
        return ret;
    }
    ///*** Производит поиск наличия копии файла в дирректории
    private static boolean contCopy(ArrayList<String> dirName, ArrayList<Long> dirSpace, File file) {
        for (int i = 0; i<dirName.size(); i++) {
            String tempfile = file.getName();
            String tempDir = dirName.get(i);
            if (file.getName().contains(".")) {
                tempfile = tempfile.substring(0, tempfile.indexOf("."));
                tempDir = tempDir.substring(0, tempDir.indexOf("."));
                if ((tempfile.startsWith(tempDir)
                        ||((tempDir.lastIndexOf("(")>0)?(tempfile.startsWith(tempDir.substring(0, tempDir.lastIndexOf("(")))):(false))
                        ||(((tempDir.lastIndexOf("(")>0)&&(tempfile.lastIndexOf("(")>0))?(tempfile.substring(0, tempfile.lastIndexOf("(")).startsWith(tempDir.substring(0, tempDir.lastIndexOf("(")))):(false)))
                        &&dirSpace.get(i)==file.length())
                {
                    return true;
                }
            }
        }
        return false;
    }
    ///*** Проводит поиск на наличие файлов, от корых он мог стать копией
    private static boolean findCopyOutIn(File file, File[] arrayFiles)
    {
        ArrayList<String> filesName = new ArrayList<>();
        for (File i :
                arrayFiles) {
            filesName.add(i.getName());
        }
        for(int i = 0; i<filesName.size(); i++)
        {
            if(!filesName.get(i).contains("."))
            {
                break;
            }
            filesName.set(i, filesName.get(i).substring(0, filesName.get(i).indexOf(".")));
        }
        String tempName = file.getName().contains(".")? file.getName().substring(0, file.getName().indexOf(".")):file.getName();
        for (int i = 0; i<filesName.size();i++) {
            if (tempName.startsWith(filesName.get(i)))
                return true;
        }
        return false;
    }
    private static void writeLog(String message) throws IOException
    {
        Date date = new Date();
        FileWriter writer = new FileWriter(new File("D:\\Temp_Log\\log.txt"),true);
        BufferedWriter buff = new BufferedWriter(writer);
        buff.write(date.toString() + System.lineSeparator()+"      " + message+System.lineSeparator());
        buff.close();
    }
}
