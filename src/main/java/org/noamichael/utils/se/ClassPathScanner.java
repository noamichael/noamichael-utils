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

    /**
     * When searching all the entire project for classes, cache results.
     */
    private static List<ScannerSearchResult> CACHED_SEARCHES = new ArrayList();
    /**
     * The folder under which classes reside
     */
    public static final String CLASS_FOLDER_NAME = "classes";
    /**
     * The character which separates inner classes from parent class.
     */
    public static final String INNER_CLASS_SEPARATOR = "$";
    /**
     * The character which separates each folder of a package.
     */
    public static final String PACKAGE_NAME_SEPARATOR = ".";
    /**
     * The XML file used for location reference when scanning.
     */
    public static final String CLASSPATH_SCANNER_XML = "META-INF/classpath-scanner.xml";
    /**
     * A predicate for determining whether a file is a {@link Class}.
     * (Extension)
     */
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
     * Scans for classes at starting at the given {@link File folder}
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

    /**
     * Scans a project for objects which are annotated with the given annotation
     *
     * @param annotation The annotation to search for
     * @param elementTypes The elements where to search on
     * @return
     */
    public static List<ScannerSearchResult> scanProjectForAnnotation(Class<? extends Annotation> annotation, ElementType... elementTypes) {
        if (CACHED_SEARCHES.isEmpty()) {
            CACHED_SEARCHES = scanForAnnotationAtFolder(annotation, getRootProjectFolder(), elementTypes);
        }
        return CACHED_SEARCHES;
    }

    /**
     * Scans a folder/subfolder for objects which are annotated with the given
     * annotation
     *
     * @param annotation The annotation to search for
     * @param startingFolder
     * @param elementTypes The elements where to search on
     * @return
     */
    public static List<ScannerSearchResult> scanForAnnotationAtFolder(Class<? extends Annotation> annotation, String startingFolder, ElementType... elementTypes) {
        File file = new File(startingFolder);
        List<File> foundClassFiles = new ArrayList();
        List<ScannerSearchResult> annotatedClasses = new ArrayList();
        recursivelyTraverseFile(file, FILE_IS_CLASS, foundClassFiles);
        doScanForAnnotations(annotation, elementTypes, annotatedClasses);
        return annotatedClasses;
    }

    /**
     * Performs the actually scan for annotations
     *
     * @param annotation
     * @param elementTypes
     * @param annotatedClasses
     */
    private static void doScanForAnnotations(Class<? extends Annotation> annotation, ElementType[] elementTypes, List<ScannerSearchResult> annotatedClasses) {
        scanForClasses((Consumer<Class<?>>) clazz -> {
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

    /**
     * Returns true if the class name contains the
     * {@link #INNER_CLASS_SEPARATOR}
     *
     * @param name
     * @return
     */
    private static boolean isInnerClass(String name) {
        return name.contains(INNER_CLASS_SEPARATOR);
    }

    /**
     * Returns a list of all {@link AccessibleObject} where the annotation is
     * present.
     *
     * @param clazz The annotation
     * @param accessibleObjects The array of {@link AccessibleObject} to scan
     * @return
     */
    private static List<AccessibleObject> isAnnotationPresent(Class<? extends Annotation> clazz, AccessibleObject... accessibleObjects) {
        List<AccessibleObject> list = new ArrayList<>();
        for (AccessibleObject accessibleObject : accessibleObjects) {
            if (accessibleObject.isAnnotationPresent(clazz)) {
                list.add(accessibleObject);
            }
        }
        return list;
    }

    /**
     * Returns a list of all annotation classes who have the given annotation
     * present.
     *
     * @param clazz
     * @param annotations
     * @return
     */
    private static List<Class<? extends Annotation>> isAnnotationPresent(Class<? extends Annotation> clazz, Annotation... annotations) {
        List<Class<? extends Annotation>> list = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(clazz)) {
                list.add(annotation.annotationType());
            }
        }
        return list;
    }

    /**
     * Formats the path of a class with the name of the class to make the fully
     * qualified class name.
     *
     * @param packageName
     * @param fileName
     * @return
     */
    private static String formatClassName(String packageName, String fileName) {
        return packageName + PACKAGE_NAME_SEPARATOR + fileName.replace(INNER_CLASS_SEPARATOR, "").replace(".class", "");
    }

    /**
     * Finds the root project folder if the required XML file
     * ({@link #CLASSPATH_SCANNER_XML}) is present.
     *
     * @return
     */
    private static String getRootProjectFolder() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(CLASSPATH_SCANNER_XML);
        if (url == null) {
            throw new RuntimeException("Cannot find " + CLASSPATH_SCANNER_XML);
        }
        int endOfProjectRootString = url.getFile().indexOf(CLASSPATH_SCANNER_XML);
        String rootProjectFolder = url.getFile().substring(0, endOfProjectRootString);
        return rootProjectFolder;
    }

    /**
     * Converts a path into a package name
     *
     * @param path
     * @return
     */
    public static String getPackageNameFromPath(String path) {
        int packageNameStartIndex = path.indexOf(getClassFolderName()) + getClassFolderName().length();
        String pathWithSlashesAndFileName = path.substring(packageNameStartIndex);
        int endOfPackageName = pathWithSlashesAndFileName.lastIndexOf(getFileSeparator());
        return pathWithSlashesAndFileName.substring(0, endOfPackageName).replace(getFileSeparator(), PACKAGE_NAME_SEPARATOR);

    }

    /**
     * Recursively traverses a file structure and adds the current file to the
     * given list of results if it passes the predicate.
     *
     * @param file The starting file.
     * @param filePredicate The predicate to test against
     * @param listToAddResultsTo The instantiated list to add results to
     * @throws NullPointerException if the list of results is null
     */
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

    /**
     * Returns the system file system.
     *
     * @return
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * Get the name of the folder (with the file separator) where classes are
     * housed.
     *
     * @return
     */
    public static String getClassFolderName() {
        return getFileSeparator() + CLASS_FOLDER_NAME + getFileSeparator();
    }

    /**
     * A class which holds the information surrounding the result of a search
     */
    public static class ScannerSearchResult {

        /**
         * The class of where the result was found
         */
        private final Class<?> resultClass;
        /**
         * The result of the search
         */
        private final Object result;
        /**
         * The {@link ElementType} of the result
         */
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
