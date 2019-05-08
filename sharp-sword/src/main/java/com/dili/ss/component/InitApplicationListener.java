package com.dili.ss.component;

import com.dili.http.okhttp.java.JavaStringCompiler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Created by asiam on 2018/3/23 0023.
 */
@Component
public class InitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (JavaStringCompiler.isJarRun()) {
            String jarDirPath = new org.springframework.boot.system.ApplicationHome(InitApplicationListener.class).getDir().getAbsolutePath();
            String separator = System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0 ? "/" : "\\\\";
            List<String> paths = new java.util.ArrayList();
            paths.add(jarDirPath + separator + "BOOT-INF" + separator + "classes");
            paths.add(jarDirPath + separator + "BOOT-INF" + separator + "lib");
            paths.add(jarDirPath + separator + "META-INF");
            paths.add(jarDirPath + separator + "org");
            paths.add(jarDirPath + separator + "BOOT-INF");
            InitApplicationListener.delDirs(paths);
        }
    }

    public static void delDirs(List<String> paths){
        for(String path : paths){
            delDir(new File(path));
        }
    }

    private static void delDir(File file) {
        if (file.isDirectory()) {
            File zFiles[] = file.listFiles();
            for (File file2 : zFiles) {
                delDir(file2);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}