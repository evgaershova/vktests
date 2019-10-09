package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);


    public void onTestStart(ITestResult result) {
       LOG.info(String.format("Starting test [%s]", result.getMethod().getMethodName()));
    }

    public void onTestSuccess(ITestResult result) {
        LOG.info(String.format("Test [%s]: PASSED", result.getMethod().getMethodName()));
    }

    public void onTestFailure(ITestResult result) {
        LOG.info(String.format("Test [%s]: FAILED", result.getMethod().getMethodName()));
    }

    public void onTestSkipped(ITestResult result) {
        LOG.info(String.format("Test [%s]: SKIPPED", result.getMethod().getMethodName()));
    }

}
