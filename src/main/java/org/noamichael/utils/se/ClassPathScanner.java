package org.noamichael.utils.se;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AccessibleObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public abstract class ClassPathScanner {

    public static final String CLASS_FOLDER_NAME = "classes";
    public static final String INNER_CLASS_SEPARATOR = "$";
    public static final String PACKAGE_NAME_SEPARATOR = ".";
    public static final String CLASSPATH_SCANNER_XML = "META-INF/classpath-scanner.xml";
    public static final Predicate<File> FILE_IS_CLASS = (file) -> file.getName().endsWith(".class");

    /**
     * Scans for classes, starting at the project root and fits the predicate
     * Does not include inner classes. Inner classes can be obtained via
     * {@link Class#getDeclaredClasses()}
     *
     * @param predicate
     * @return
     */
    public static List<ScannerSearchResult> scanForClasses(Predicate<Class<?>> predicate) {
        return scanForClasses(predicate, null);
    }

    /**
     * Scans for classes, starting at the project root, and applies the consumer
     * on all classes. Does not include inner classes. Inner classes can be
     * obtained via {@link Class#getDeclaredClasses()}
     *
     * @param consumer
     * @return
     */
    public static List<ScannerSearchResult> scanForClasses(Consumer<Class<?>> consumer) {
        return scanForClasses(null, consumer);
    }

    /**
     * Scans for classes at the project root
     *
     * @param predicate
     * @param consumer
     * @return
     */
    public static List<ScannerSearchResult> scanForClasses(Predicate<Class<?>> predicate, Consumer<Class<?>> consumer) {
        List<ScannerSearchResult> searchResults = new ArrayList();
        List<File> foundFiles = new ArrayList();
        File root = new File(getRootProjectFolder());
        recursivelyTraverseFile(root, FILE_IS_CLASS, foundFiles);
        for (File file : foundFiles) {
            String packageName = getPackageNameFromPath(file.getPath());
            if (!isInnerClass(file.getName())) {
                try {
                    String fullyQualifiedClassName = formatClassName(packageName, file.getName());
                    Class c = Class.forName(fullyQualifiedClassName);
                    if (predicate != null && predicate.test(c)) {
                        searchResults.add(new ScannerSearchResult(c, c, ElementType.TYPE));
                    }
                    if (consumer != null) {
                        consumer.accept(c);
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClassPathScanner.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return searchResults;
    }

    /**
     * Scans for classes at the given file
     *
     * @param predicate
     * @param consumer
     * @param root
     * @return
     */
    public static List<ScannerSearchResult> scanForClasses(Predicate<Class<?>> predicate, Consumer<Class<?>> consumer, File root) {
        List<ScannerSearchResult> searchResults = new ArrayList();
        for (File file : root.listFiles()) {
            String packageName = getPackageNameFromPath(file.getPath());
            if (!isInnerClass(file.getName())) {
                try {
                    String fullyQualifiedClassName = formatClassName(packageName, file.getName());
                    Class c = Class.forName(fullyQualifiedClassName);
                    if (predicate != null && predicate.test(c)) {
                        searchResults.add(new ScannerSearchResult(c, c, null));
                    }
                    if (consumer != null) {
                        consumer.accept(c);
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClassPathScanner.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return searchResults;
    }

    public static List<ScannerSearchResult> scanProjectForAnnotation(Class<? extends Annotation> annotation, ElementType... elementTypes) {
        return scanForAnnotationAtFolder(annotation, getRootProjectFolder(), elementTypes);
    }

    public static List<ScannerSearchResult> scanForAnnotationAtFolder(Class<? extends Annotation> annotation, String startingFolder, ElementType... elementTypes) {
        File file = new File(startingFolder);
        List<File> foundClassFiles = new ArrayList();
        List<ScannerSearchResult> annotatedClasses = new ArrayList();
        recursivelyTraverseFile(file, FILE_IS_CLASS, foundClassFiles);
        doScanForAnnotations(annotation, elementTypes, annotatedClasses);
        return annotatedClasses;
    }

    private static void doScanForAnnotations(Class<? extends Annotation> annotation, ElementType[] elementTypes, List<ScannerSearchResult> annotatedClasses) {
        scanForClasses(clazz -> {
            for (ElementType elementType : elementTypes) {
                switch (elementType) {
                    case CONSTRUCTOR: {
                        List<AccessibleObject> result = isAnnotationPresent(annotation, clazz.getDeclaredConstructors());
                        if (!result.isEmpty()) {
                            annotatedClasses.add(new ScannerSearchResult(clazz, result, elementType));
                        }
                        break;
                    }
                    case FIELD: {
                        List<AccessibleObject> result = isAnnotationPresent(annotation, clazz.getDeclaredFields());
                        if (!result.isEmpty()) {
                            annotatedClasses.add(new ScannerSearchResult(clazz, result, elementType));
                        }
                        break;
                    }
                    case METHOD: {
                        List<AccessibleObject> result = isAnnotationPresent(annotation, clazz.getDeclaredMethods());
                        if (!result.isEmpty()) {
                            annotatedClasses.add(new ScannerSearchResult(clazz, result, elementType));
                        }
                        break;
                    }
                    case ANNOTATION_TYPE: {
                        if (clazz.isAnnotation()) {
                            List<Class<? extends Annotation>> result = isAnnotationPresent(annotation, clazz.getDeclaredAnnotations());
                            if (!result.isEmpty()) {
                                annotatedClasses.add(new ScannerSearchResult(clazz, result, elementType));
                            }
                        }
                        break;
                    }
                    case TYPE: {
                        if (clazz.isAnnotationPresent(annotation)) {
                            annotatedClasses.add(new ScannerSearchResult(clazz, clazz, elementType));
                        }
                        break;
                    }
                }
            }
        });
    }

    private static boolean isInnerClass(String name) {
        return name.contains(INNER_CLASS_SEPARATOR);
    }

    private static List<AccessibleObject> isAnnotationPresent(Class<? extends Annotation> clazz, AccessibleObject... accessibleObjects) {
        List<AccessibleObject> list = new ArrayList<>();
        for (AccessibleObject accessibleObject : accessibleObjects) {
            if (accessibleObject.isAnnotationPresent(clazz)) {
                list.add(accessibleObject);
            }
        }
        return list;
    }

    private static List<Class<? extends Annotation>> isAnnotationPresent(Class<? extends Annotation> clazz, Annotation... annotations) {
        List<Class<? extends Annotation>> list = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(clazz)) {
                list.add(annotation.annotationType());
            }
        }
        return list;
    }

    private static String formatClassName(String packageName, String fileName) {
        return packageName + PACKAGE_NAME_SEPARATOR + fileName.replace(INNER_CLASS_SEPARATOR, "").replace(".class", "");
    }

    private static String getRootProjectFolder() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(CLASSPATH_SCANNER_XML);
        if (url == null) {
            throw new RuntimeException("Cannot find " + CLASSPATH_SCANNER_XML);
        }
        int endOfProjectRootString = url.getFile().indexOf(CLASSPATH_SCANNER_XML);
        String rootProjectFolder = url.getFile().substring(0, endOfProjectRootString);
        return rootProjectFolder;
    }

    public static String getPackageNameFromPath(String path) {
        int packageNameStartIndex = path.indexOf(getClassFolderName()) + getClassFolderName().length();
        String pathWithSlashesAndFileName = path.substring(packageNameStartIndex);
        int endOfPackageName = pathWithSlashesAndFileName.lastIndexOf(getFileSeparator());
        return pathWithSlashesAndFileName.substring(0, endOfPackageName).replace(getFileSeparator(), PACKAGE_NAME_SEPARATOR);

    }

    public static void recursivelyTraverseFile(File file, Predicate<File> filePredicate, List<File> listToAddResultsTo) {
        if (file == null) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            if (filePredicate.test(file)) {
                listToAddResultsTo.add(file);
            }
            return;

        }
        for (File child : files) {
            recursivelyTraverseFile(child, filePredicate, listToAddResultsTo);
        }
    }

    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getClassFolderName() {
        return getFileSeparator() + CLASS_FOLDER_NAME + getFileSeparator();
    }

    public static class ScannerSearchResult {

        private final Class<?> resultClass;
        private final Object result;
        private final ElementType elementType;

        public ScannerSearchResult(Class<?> resultClass, Object result, ElementType elementType) {
            this.resultClass = resultClass;
            this.result = result;
            this.elementType = elementType;
        }

        /**
         * @return the result
         */
        public Object getResult() {
            return result;
        }

        /**
         * @return the elementType
         */
        public ElementType getElementType() {
            return elementType;
        }

        public Class<?> getResultClass() {
            return resultClass;
        }

        @Override
        public String toString() {
            return "ScannerSearchResult{" + "resultClass=" + resultClass + ", result=" + result + ", elementType=" + elementType + '}';
        }

    }
}
