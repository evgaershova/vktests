import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class App {


    //public static final String CLIENT_SECRET = "qRoI9ytckc5RD9dCuhr1";
 //   public static final String CODE = "2c2829b75776e7fa51";
    public static final String REDIRECT_URI = "https://vk.com/chisnog";
    public static final int CLIENT_ID = 7141863;

    //TODO брать эти данные из проперти
    public static final String ACCESS_TOKEN  = "0a1a407ca72b08ae365b39dcdcd6aaaf2e9f8f1bef2506fb8e0ff1493d5b698a7afddbc04269b3996c569";
    public static final int USER_ID = 57653;
    private TransportClient transportClient;
    private VkApiClient vk;

    @BeforeClass
    public void init() {
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
    }

    @Test
    public void getListTest() throws ClientException, ApiException {
        UserActor actor = new UserActor(USER_ID, ACCESS_TOKEN);

        //надо будет создать тестового пользователя
        // затем создать тестовый пост и навешать на него тестовых лайков

        GetListResponse getResponse = vk.likes()
                .getList(actor, LikesType.POST)
                .ownerId(USER_ID)
                //TODO обязательно избавиться от этих констант
                .itemId(1580)
                .count(1000)
                .execute();

        System.out.println(getResponse);
    }



}
