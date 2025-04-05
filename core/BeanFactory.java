package core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    private static final Map<String, Object> beans = new ConcurrentHashMap<>();


    public static void register(Class<?> bean) {
        try {
            registerBean(bean);
            inject(beans.get(bean.getSimpleName()));
        } catch (Exception e) {
            System.err.println("Failed to instantiate bean: " + bean.getName());
        }
    }

    private static void registerBean(Class<?> bean) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> declaredConstructor = bean.getDeclaredConstructor();
        Object instance = declaredConstructor.newInstance();
        beans.put(bean.getSimpleName(), instance);
    }

    private static void inject(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType() == AutoWired.class) {
                    BeanFactory.register(field.getType());
                    field.setAccessible(true);
                    try {
                        field.set(bean, BeanFactory.getBean(field.getType().getSimpleName()));
                    } catch (Exception e) {
                        System.err.println("Failed to inject field: " + field.getName());
                    }
                    field.setAccessible(false);
                }
            }
        }
    }

    public static Object getBean(String beanName) {
        return beans.get(beanName);
    }

    public static void scan(String basePackage) {
        String path = basePackage.replace('.', '/');
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                scanDirectory(file, basePackage);
            }
        } catch (IOException e) {
            System.err.println("Failed to scan package: " + basePackage);
        }
    }

    private static void scanDirectory(File directory, String basePackage) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        register(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to load class: " + className);
                }
            }
        }
    }
}
