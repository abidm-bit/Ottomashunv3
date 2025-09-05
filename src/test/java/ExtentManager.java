import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

// singleton class eg. test report
public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports createInstance(String file){
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(file);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        return extent;
    }




}