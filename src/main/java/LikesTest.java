import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.likes.responses.AddResponse;
import com.vk.api.sdk.objects.likes.responses.DeleteResponse;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.objects.wall.responses.PostResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LikesTest extends BaseTest{

    private static final int USER_ID = 57653;
    private static final int GROUP_ID = - 187261338;

    private TransportClient transportClient;
    private VkApiClient vk;
    private UserActor actor;
    private UserActor groupActor;

    @BeforeClass
    public void init() {
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        actor = new UserActor(USER_ID, ACCESS_TOKEN);
        groupActor = new UserActor(GROUP_ID, GROUP_ACCESS_TOKEN);
    }

    @Test
    public void getList() throws ClientException, ApiException {

        GetListResponse getResponse = vk.likes()
                .getList(actor, LikesType.POST)
                .ownerId(USER_ID)
                //TODO обязательно избавиться от этих констант
                .itemId(1580)
                .count(1000)
                .execute();

   //     System.out.println(getResponse);
    }

    @Test
    public void addLike() throws ClientException, ApiException {
        String guid = UUID.randomUUID().toString();

        PostResponse response = vk.wall()
                .post(actor)
                .fromGroup(false)
                .ownerId(GROUP_ID)
                .message("Hi, there this is my test message " + guid)
                .guid(guid)
                .execute();

        int postId = response.getPostId();

        AddResponse addLike = vk.likes()
                .add(actor, LikesType.POST, postId)
                .ownerId(GROUP_ID)
                .execute();

        assertThat(addLike.getLikes())
                .as("Number of likes is 1")
                .isEqualTo(1);

       // System.out.println(addLike);
    }

    @Test
    public void deleteLike() throws ClientException, ApiException {
        String guid = UUID.randomUUID().toString();

        PostResponse response = vk.wall()
                .post(actor)
                .fromGroup(false)
                .ownerId(GROUP_ID)
                .message("Hi, there this is my test message " + guid)
                .guid(guid)
                .execute();

        int postId = response.getPostId();

        AddResponse addLike = vk.likes()
                .add(actor, LikesType.POST, postId)
                .ownerId(GROUP_ID)
                .execute();

        DeleteResponse removeLike = vk.likes()
                .delete(actor, LikesType.POST, postId)
                .ownerId(GROUP_ID)
                .execute();

        assertThat(removeLike.getLikes())
                .as("Number of likes is 0")
                .isEqualTo(0);
    }


    @Test
    public void isLiked() throws ClientException, ApiException {

        String guid = UUID.randomUUID().toString();

        PostResponse response = vk.wall()
                .post(actor)
                .fromGroup(false)
                .ownerId(GROUP_ID)
                .message("Hi, there this is my test message " + guid)
                .guid(guid)
                .execute();

        int postId = response.getPostId();

        AddResponse addLike = vk.likes()
                .add(actor, LikesType.POST, postId)
                .ownerId(GROUP_ID)
                .execute();

        IsLikedResponse isLikedResponse = vk.likes()
                .isLiked(actor, LikesType.POST, postId)
                .ownerId(GROUP_ID)
                .userId(USER_ID)
                .execute();

        assertThat(isLikedResponse.isLiked())
                .as("Liked is true").isTrue();
    }


}
