//package com.company;
import java.io.*;
import java.nio.channels.FileLock;
import java.util.Random;
import java.util.UUID;
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int count = 10;             // Колличестов созданных файлов
        int t = 0;                  // Время задержки между файлами
        int dim;                    // Размер создаваемого файла
        String dir;                 // Директория создания файла
        String nameFile;            // Имя файла
        Random rnd = new Random();
        dir = "D:\\TempPrograms\\";
        for (int i = 0; i<count; i++)
        {
            Thread.sleep(t);
            nameFile = generate(dir);
            FileOutputStream fos = new FileOutputStream(dir+nameFile);
            BufferedWriter rwChannel = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            FileLock lock = fos.getChannel().lock();
            dim = rnd.nextInt(0x8ffffff);
            for (int p = 0; p < dim; p++) {
                rwChannel.write((byte) (48 + rnd.nextInt(2)));
            }
            lock.release();
            rwChannel.close();
            fos.close();
            t = 10 + rnd.nextInt(600);
        }
    }
    private static String generate(String dir)
    {
        String name = null;
        UUID uuid = UUID.randomUUID();
        while(true)
        {
            name = uuid.toString()+".txt";
            if (!name.equals(dir))
            {
                break;
            }
        }
        return name;
    }
}
