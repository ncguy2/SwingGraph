package net.ncguy.graph.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Guy on 14/01/2017.
 */
public class RuntimeCartographer {

    private static RuntimeCartographer instance;

    public static RuntimeCartographer getInstance() {
        if (instance == null)
            instance = new RuntimeCartographer();
        return instance;
    }

    private String externalDirectory;
    private ClassLoader runtimeLoader;
    private List<URL> urls;

    private RuntimeCartographer() {
        externalDirectory = "plugins";
        urls = new ArrayList<>();
    }

    public File[] searchDirectories() {
        File dir = new File(externalDirectory);
        if(!dir.exists()) {
            System.err.println("Specified runtime directory, \""+dir.getAbsolutePath()+"\", does not exist, creating...");
            if(!dir.mkdirs()) {
                System.err.println("Unable to create runtime directory.");
            }
            return null;
        }
        File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".jar"));
        if(files == null) {
            System.err.println("Unable to find external jar files inside specified runtime directory, \"" + dir.getAbsolutePath() + "\"");
            return null;
        }
        if(files.length <= 0) {
            System.err.println("No external jar files found in specified runtime directory, \"" + dir.getAbsolutePath() + "\"");
            return null;
        }
        return files;
    }

    public URL[] convertToUrls(File[] files) throws MalformedURLException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++)
            urls[i] = files[i].toURI().toURL();
        return urls;
    }

    public void registerFilesToClassLoader(File[] files) {
        URL[] urls = new URL[files.length];
        List<Enumeration<JarEntry>> enumerations = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            String path = files[i].getAbsolutePath();
            try {
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> e = jarFile.entries();
                enumerations.add(e);
                urls[i] = new URL("jar:file:" + path+"!/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        registerURLsInClassLoader(urls);

        for (Enumeration<JarEntry> e : enumerations) {
            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    Class c = runtimeLoader.loadClass(className);
                    System.out.println(c);
                    for (Annotation annotation : c.getAnnotations()) {
                        System.out.println("\t"+annotation.toString());
                    }
                    System.out.println(c.getClassLoader());
                    System.out.println(runtimeLoader);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void registerURLsInClassLoader(URL[] urls) {
        Collections.addAll(this.urls, urls);
        runtimeLoader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());
    }

    public URL[] removeInvalidUrls(URL[] urls) {
        Set<URL> valids = new LinkedHashSet<>();
        for (int i = 0; i < urls.length; i++) {
            URL u = urls[i];
            if(u != null) valids.add(u);
            else System.out.println("Invalid URL found, removing...");
        }
        URL[] validUrls = new URL[valids.size()];
        valids.toArray(validUrls);
        return validUrls;
    }

    public void executeProcess() {
        File[] files = searchDirectories();
        if(files == null) return;
        registerFilesToClassLoader(files);
//        URL[] urls = null;
//        try {
//            urls = convertToUrls(files);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        if(urls == null) return;
//        urls = removeInvalidUrls(urls);
//        registerURLsInClassLoader(urls);
    }

    public ClassLoader getClassLoader() {
        return runtimeLoader;
    }

    public Collection<URL> getUrls() {
        return this.urls;
    }
}

