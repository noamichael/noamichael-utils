package org.noamichael.utils.se;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AccessibleObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author Michael
 */
public abstract class ClassPathScanner {

    public static final String CLASS_FOLDER_NAME = "\\classes\\";
    public static final String FOLDER_SEPARATOR = "\\";
    public static final String INNER_CLASS_SEPARATOR = "$";
    public static final String PACKAGE_NAME_SEPARATOR = ".";
    public static final String CLASSPATH_SCANNER_XML = "META-INF/classpath-scanner.xml";
    public static final Predicate<File> FILE_IS_CLASS = (file) -> file.getName().endsWith(".class");

    public static void scanProjectForAnnotation(Class<? extends Annotation> annotation, ElementType... elementTypes) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(CLASSPATH_SCANNER_XML);
        int endOfProjectRootString = url.getFile().indexOf(CLASSPATH_SCANNER_XML);
        String rootProjectFolder = url.getFile().substring(0, endOfProjectRootString);
        System.out.println(rootProjectFolder);
        scanForAnnotationAtFolder(annotation, rootProjectFolder, elementTypes);
    }

    public static void scanForAnnotationAtFolder(Class<? extends Annotation> annotation, String startingFolder, ElementType... elementTypes) {
        File file = new File(startingFolder);
        List<File> foundClassFiles = new ArrayList();
        List<ScannerSearchResult> annotatedClasses = new ArrayList();
        recursivelyTraverseFile(file, FILE_IS_CLASS, foundClassFiles);
        for (File foundFile : foundClassFiles) {
            String packageName = getPackageNameFromPath(foundFile.getPath());
            if (!isInnerClass(foundFile.getName())) {
                String fullyQualifiedClassName = formatClassName(packageName, foundFile.getName());
                try {
                    Class c = Class.forName(fullyQualifiedClassName);
                    for (ElementType elementType : elementTypes) {
                        switch (elementType) {
                            case CONSTRUCTOR: {
                                AccessibleObject result = isAnnotationPresent(annotation, c.getDeclaredConstructors());
                                if (result != null) {
                                    annotatedClasses.add(new ScannerSearchResult(c, result, elementType));
                                }
                                break;
                            }
                            case FIELD: {
                                AccessibleObject result = isAnnotationPresent(annotation, c.getDeclaredFields());
                                if (result != null) {
                                    annotatedClasses.add(new ScannerSearchResult(c, result, elementType));
                                }
                                break;
                            }
                            case METHOD: {
                                AccessibleObject result = isAnnotationPresent(annotation, c.getDeclaredMethods());
                                if (result != null) {
                                    annotatedClasses.add(new ScannerSearchResult(c, result, elementType));
                                }
                                break;
                            }
                            case TYPE: {
                                if (c.isAnnotationPresent(annotation)) {
                                    annotatedClasses.add(new ScannerSearchResult(c, c, elementType));
                                }
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static boolean isInnerClass(String name) {
        return name.contains(INNER_CLASS_SEPARATOR);
    }

    private static AccessibleObject isAnnotationPresent(Class<? extends Annotation> clazz, AccessibleObject... accessibleObjects) {
        for (AccessibleObject accessibleObject : accessibleObjects) {
            if (accessibleObject.isAnnotationPresent(clazz)) {
                return accessibleObject;
            }
        }
        return null;
    }

    private static String formatClassName(String packageName, String fileName) {
        return packageName + PACKAGE_NAME_SEPARATOR + fileName.replace(INNER_CLASS_SEPARATOR, "").replace(".class", "");
    }

    public static String getPackageNameFromPath(String path) {
        int packageNameStartIndex = path.indexOf(CLASS_FOLDER_NAME) + CLASS_FOLDER_NAME.length();
        String pathWithSlashesAndFileName = path.substring(packageNameStartIndex);
        int endOfPackageName = pathWithSlashesAndFileName.lastIndexOf(FOLDER_SEPARATOR);
        return pathWithSlashesAndFileName.substring(0, endOfPackageName).replace(FOLDER_SEPARATOR, PACKAGE_NAME_SEPARATOR);

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
    }
}
