package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFMigrationClass;
import net.fasolato.jfmigrate.Migration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by fasolato on 16/03/2017.
 */
public class ReflectionHelper {
    private static Logger log = LogManager.getLogger(ReflectionHelper.class);

    public static List<JFMigrationClass> getAllMigrations(String pkg) throws Exception {
        if (pkg == null || pkg.trim().length() == 0) {
            throw new Exception("Null or empty package passed");
        }
//        Reflections reflections = new Reflections(pkg);
//        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Migration.class);
        List<String> classNames = listClassesInPackage(pkg);

        List<JFMigrationClass> migrations = new ArrayList<JFMigrationClass>();
        for (String n : classNames) {
            Class<?> c = Class.forName(pkg + "." + n);
            if (!JFMigrationClass.class.isAssignableFrom(c)) {
                log.debug("class {} does not implement IMigration. Class is ignored", c.getSimpleName());
            } else {
                log.debug("class {} implements IMigration. Class is added as a migration", c.getSimpleName());
                migrations.add((JFMigrationClass) c.newInstance());
            }
        }

        return migrations;
    }

    //    https://stackoverflow.com/a/7461653
    public static List<String> listClassesInPackage(String packageName) throws IOException, URISyntaxException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url;
        List<String> toReturn = new ArrayList<String>();

        packageName = packageName.replaceAll("\\.", "/");
        url = cl.getResource(packageName);

        if (url.getProtocol().equals("jar")) {
            //Jar files
            String jarFile = URLDecoder.decode(url.getFile(), "UTF-8");
            String fileName = jarFile.substring(5, jarFile.indexOf("!"));
            JarFile jf = new JarFile(fileName);
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry jar = entries.nextElement();
                String entryName = jar.getName();
                if (entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) {
                    entryName = entryName.substring(packageName.length(), entryName.lastIndexOf('.'));
                    toReturn.add(entryName);
                }
            }
        } else {
            //class files
            File dir = new File(new URI(url.toString()).getPath());
            for (File f : dir.listFiles()) {
                if (f.isFile()) {
                    toReturn.add(f.getName().substring(0, f.getName().lastIndexOf('.')));
                }
            }
        }

        return toReturn;
    }
}
