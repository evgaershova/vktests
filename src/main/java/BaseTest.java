import client.ApiWrapper;
import com.vk.api.sdk.client.actors.UserActor;
import io.qameta.allure.Attachment;
import listeners.TestListener;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.IOException;

@Listeners(TestListener.class)
public class BaseTest {

    protected static int userId;
    protected static int groupId;

    protected static String accessToken;

    protected static UserActor actor;

    private static ApiWrapper apiWrapper;

    private static String fileSeparator;

    @BeforeSuite(description = "Get all system properties and create API client instance")
    public void initApi() {
        userId = Integer.valueOf(System.getProperty("userId"));
        groupId = Integer.valueOf(System.getProperty("groupId"));
        accessToken =  System.getProperty("token");
        fileSeparator = System.getProperty("file.separator");

        apiWrapper = ApiWrapper.getInstance();

        actor = new UserActor(userId, accessToken);
    }

    @AfterMethod(description = "Attach logs")
    public void attachLogs() {
        appendLogToAllure(new File( "target" + fileSeparator + "logs" + fileSeparator + "test-logs.log"));
    }

    @Attachment(value = "tests-log", type = "text/html")
    private byte[] appendLogToAllure(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ignored) {
        }
        return null;
    }

    public static ApiWrapper api() {
        return apiWrapper;
    }

}
