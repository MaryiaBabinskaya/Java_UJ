package uj.wmii.pwj.anns;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;

    public static void main(String[] args) {
        displayAsciiArt();
        displayEngineAsciiArt();
        displayAsciiArt();
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }
        String className = args[0].trim();
        System.out.printf("Testing class: %s\n", className);
        System.out.println("=============================================================");
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int successCount = 0;
        int failCount = 0;
        int errorCount = 0;
        for (Method m: testMethods) {
            TestResult result = launchSingleMethod(m, unit);
            if (result == TestResult.SUCCESS) successCount++;
            else if (result == TestResult.FAIL) failCount++;
            else errorCount++;
        }
        System.out.printf("\033[34m" + "Engine launched %d tests.\n" + "\033[0m", testMethods.size());
        System.out.printf("\033[32m%d of them passed, \033[31m%d failed, \033[0m%d resulted with exception.\n", successCount, failCount, errorCount);
    }

    private TestResult launchSingleMethod(Method m, Object unit) {
        try {
            MyTest annotation = m.getAnnotation(MyTest.class);
            int[] intExpectedWynik = (annotation != null && annotation.intExpected() != null) ? annotation.intExpected() : new int[]{};
            int[] intParams = (annotation != null && annotation.intParams() != null) ? annotation.intParams() : new int[]{};
            String[] strExpectedWynik = (annotation != null && annotation.strExpected() != null) ? annotation.strExpected() : new String[]{};
            String[] strParams = (annotation != null && annotation.strParams() != null) ? annotation.strParams() : new String[]{};

            if (intExpectedWynik.length == 0 && strExpectedWynik.length == 0 && m.getReturnType() != Void.class) {
                handleTestError(m, "is void, but has expected values");
                return TestResult.ERROR;
            }

            if (strParams.length == 0 && intParams.length == 0 && m.getParameterCount() != 0) {
                handleTestError(m, "requests more parameters to provide");
                return TestResult.ERROR;
            }

            if (strParams.length == 0 && intParams.length == 0) return testWithNOParams(m, unit, intExpectedWynik, strExpectedWynik);
            else return testWithParams(m, unit, intExpectedWynik, strExpectedWynik, strParams, intParams);

        } catch (ReflectiveOperationException e) {
            if (e.getCause().toString().startsWith("java.lang.AssertionError")) {
                printTestResult(m, TestResult.FAIL);
                System.out.println(e.getCause().getMessage());
            } else {
                printTestResult(m, TestResult.ERROR);
                e.printStackTrace();
            }
            return TestResult.ERROR;
        }
    }

    private TestResult testWithNOParams(Method m, Object unit, int[] intExpectedWynik, String[] strExpectedWynik) throws ReflectiveOperationException {
        if (intExpectedWynik.length == 0) {
            return StringResult(m, unit, strExpectedWynik);
        } else {
            return IntResult(m, unit, intExpectedWynik);
        }
    }

    private TestResult testWithParams(Method m, Object unit, int[] intExpectedWynik, String[] strExpectedWynik, String[] strParams, int[] intParams) throws ReflectiveOperationException {
        if (intParams.length == 0) {
            return StringParam(m, unit, intExpectedWynik, strExpectedWynik, strParams);
        } else {
            return IntParam(m, unit, intExpectedWynik, strExpectedWynik, intParams);
        }
    }

    private TestResult StringResult(Method m, Object unit, String[] strExpectedWynik) throws ReflectiveOperationException {
        if (m.getReturnType() != String.class) {
            handleTestError(m, "has not String return type, but has expected values of String type");
            return TestResult.ERROR;
        }
        if (strExpectedWynik.length > 1) {
            handleTestError(m, "has more expected values than count of values to test");
            return TestResult.ERROR;
        }
        Object result = m.invoke(unit);
        return compareResults(m, result, strExpectedWynik[0]);
    }

    private TestResult IntResult(Method m, Object unit, int[] intExpectedWynik) throws ReflectiveOperationException {
        if (m.getReturnType() != Integer.class) {
            handleTestError(m, "has not Integer return type, but has expected values of Integer type");
            return TestResult.ERROR;
        }
        if (intExpectedWynik.length > 1) {
            handleTestError(m, "has more expected values than count of values to test");
            return TestResult.ERROR;
        }
        Object result = m.invoke(unit);
        return compareResults(m, result, intExpectedWynik[0]);
    }

    private TestResult StringParam(Method m, Object unit, int[] intExpectedWynik, String[] strExpectedWynik, String[] strParams) throws ReflectiveOperationException {
        int j = 0;
        boolean fail = false;
        if (strParams.length != strExpectedWynik.length) {
            handleTestError(m, "has not the same count of expected values and values to test");
            return TestResult.ERROR;
        }
        for (String param : strParams) {
            Object result = m.invoke(unit, param);
            if (!result.equals(strExpectedWynik[j])) {
                fail = true;
                printTestResult(m, TestResult.FAIL);
                String message = createTestResultMessage(param, String.valueOf(strExpectedWynik[j]), String.valueOf(result));
                System.out.println(message);
            }
            j++;
        }
        return fail ? TestResult.FAIL : TestResult.SUCCESS;
    }

    private TestResult IntParam(Method m, Object unit, int[] intExpectedWynik, String[] strExpectedWynik, int[] intParams) throws ReflectiveOperationException {
        int j = 0;
        boolean fail = false;
        if (intParams.length != intExpectedWynik.length) {
            handleTestError(m, "has not the same count of expected values and values to test");
            return TestResult.ERROR;
        }
        for (int param : intParams) {
            Object result = m.invoke(unit, param);
            if ((int) result != intExpectedWynik[j]) {
                fail = true;
                printTestResult(m, TestResult.FAIL);
                String message = createTestResultMessage(String.valueOf(param), String.valueOf(intExpectedWynik[j]), String.valueOf(result));
                System.out.println(message);
            }
            j++;
        }
        return fail ? TestResult.FAIL : TestResult.SUCCESS;
    }

    private TestResult compareResults(Method m, Object result, Object expected) {
        if (result.equals(expected)) {
            printTestResult(m, TestResult.SUCCESS);
            return TestResult.SUCCESS;
        } else {
            printTestResult(m, TestResult.FAIL);
            String message = createTestResultMessage("", String.valueOf(expected), String.valueOf(result));
            System.out.println(message);
            return TestResult.FAIL;
        }
    }

    private void handleTestError(Method m, String message) {
        System.out.println("=============================================================");
        System.out.println("Tested method: " + m.getName() + " " + message);
        printTestResult(m, TestResult.ERROR);
        System.out.println("=============================================================");
    }

    private static void printTestResult(Method testedMethod, TestResult result) {
        System.out.println("Tested method: " + testedMethod.getName() + ", result: " + result);
    }

    private String createTestResultMessage(String str, String expected, String actual) {
        return "[" + str + "]: expected: " + expected + ", actual: " + actual;
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(
                m -> m.getAnnotation(MyTest.class) != null).collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Object();
        }
    }

    public static void displayEngineAsciiArt(){
        System.out.println("\033[35m" +
        "ooooooooooo                         o8        ooooooooooo                         o88                          \n" +
        "88  888  88 ooooooooo8  oooooooo8 o888oo       888    88  oo oooooo     oooooooo8 oooo  oo oooooo   ooooooooo8 \n" +
        "    888    888oooooo8  888ooooooo  888         888ooo8     888   888  888    88o   888   888   888 888oooooo8  \n" +
        "    888    888                 888 888         888    oo   888   888   888oo888o   888   888   888 888         \n" +
        "   o888o     88oooo888 88oooooo88   888o      o888ooo8888 o888o o888o 888     888 o888o o888o o888o  88oooo888 \n" +
        "                                                                       888ooo888                               " + "\033[0m");
    }

    public static void displayAsciiArt(){
        System.out.println( "\033[36m" +
                " ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ \n" +
                "|______|______|______|______|______|______|______|______|______|______|______|______|______|______|______|______|\n" +
                "                                                                                                                 " + "\033[0m");
    }
}