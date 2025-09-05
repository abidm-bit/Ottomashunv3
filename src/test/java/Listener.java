import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Listener implements ITestListener {
    private static final ExtentReports extent = ExtentManager.createInstance("report.html");
    private static final ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();
    private static final String SCREENSHOT_DIR = "src/test/java/screenshots/failedTests/";

    private ExtentTest getTest(ITestResult result) {
        return methodTest.get();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        ExtentTest test = extent.createTest(methodName);
        methodTest.set(test);
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        try {
            // Get the current test method's WebDriver instance from TestBase
            TestBase testInstance = (TestBase) result.getInstance();

            // Take screenshot
            File screenshot = ((TakesScreenshot) testInstance.getDriver()).getScreenshotAs(OutputType.FILE);

            // Create timestamp for unique filename
            DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());

            // Define screenshot path using existing directory
            String screenshotName = result.getMethod().getMethodName() + "_" + timestamp + ".png";
            File screenshotFile = new File(SCREENSHOT_DIR + screenshotName);

            // Save screenshot file
            FileUtils.copyFile(screenshot, screenshotFile);

            // Convert screenshot to Base64 for embedding in report
            String base64Screenshot = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(screenshot));

            // Add failure details and screenshot to report
            getTest(result)
                    .fail("Test failed due to: " + result.getThrowable().getMessage())
                    .fail("Screenshot: ", MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build())
                    .fail("Screenshot saved to: " + screenshotFile.getAbsolutePath());

        } catch (IOException e) {
            getTest(result).fail("Failed to capture screenshot: " + e.getMessage());
        } finally {
            extent.flush();
        }
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        getTest(result).pass("Test passed");
        extent.flush();
    }
}