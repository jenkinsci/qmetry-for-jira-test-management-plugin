package com.qmetry;

public class QTM4JConstants {

    public final static String JUNIT_FORMAT_TYPE = "junit/xml";
    public final static String TESTNG_FORMAT_TYPE = "testng/xml";

    // Default setting of AutomationHierarchy & AppendTestName field for JUnit/TestNG Framework
    public final static String QMETRY_AUTOMATION_SETTINGS_DEFAULT = "Default Settings - Will refer to QMetry > Automation > Automation API > Settings.";

    // AutomationHierarchy Options for JUnit Framework
    public final static String JUNIT_AUTOMATION_HIERARCHY_OPTION_1 = "1 - Test Suite Name Tag is created as Test Case & Test Case Name Tag is created as Test Step.";
    public final static String JUNIT_AUTOMATION_HIERARCHY_OPTION_2 = "2 - Test Suite Name Tag is created as Test Cycle & Test Case Name Tag is created as Test Case.";
    public final static String JUNIT_AUTOMATION_HIERARCHY_OPTION_3 = "3 - Test Cycle Summary will be auto-generated & Test Case Name Tag is created as Test Case.";

    // AutomationHierarchy Options for TestNG Framework
    public final static String TESTNG_AUTOMATION_HIERARCHY_OPTION_1 = "1 - Test Name Tag is created as Test Case & Test Method Name Tag is created as Test Step.";
    public final static String TESTNG_AUTOMATION_HIERARCHY_OPTION_2 = "2 - Test Name Tag is created as Test Cycle & Test Method Name Tag is created as Test Case.";
    public final static String TESTNG_AUTOMATION_HIERARCHY_OPTION_3 = "3 - Suite Name Tag is created as Test Cycle & Test Method Name Tag is created as Test Case.";

    // AppendTestName Options for JUnit Framework
    public final static String JUNIT_APPEND_TESTNAME_OPTION_YES = "Yes - Append Test Suite Name to Test Case Name while creating the Test Case Summary.";
    public final static String JUNIT_APPEND_TESTNAME_OPTION_NO = "No - Create the Test Case Summary as per the Test Case Name present in the result file.";

    // AppendTestName Options for TestNG Framework
    public final static String TESTNG_APPEND_TESTNAME_OPTION_YES = "Yes - Append Test Name to Test Method Name while creating the Test Case Summary.";
    public final static String TESTNG_APPEND_TESTNAME_OPTION_NO = "No - Create the Test Case Summary as per the Test Method Name in the result file.";

    public final static String OPTION_DEFAULT = "default";
    public final static String OPTION_TRUE = "true";
    public final static String OPTION_FALSE = "false";
    public final static String OPTION_1 = "1";
    public final static String OPTION_2 = "2";
    public final static String OPTION_3 = "3";

    // Region Options
    public final static String REGION_USA = "USA";
    public final static String REGION_AUSTRALIA = "Australia";

    // Support Emails
    public static final String SUPPORT_EMAIL_3X = "qtmforjira3support@smartbear.com";
    public static final String SUPPORT_EMAIL_4X = "qtmforjira4support@smartbear.com";

}