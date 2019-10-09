package client;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.likes.responses.AddResponse;
import com.vk.api.sdk.objects.likes.responses.DeleteResponse;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import com.vk.api.sdk.objects.wall.responses.PostResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiWrapper {

    private static final int sleepUntilNextRequest = 340;

    private static TransportClient transportClient;
    protected static VkApiClient vk;

    private static ApiWrapper instance;

    private ApiWrapper()  {
       transportClient = HttpTransportClient.getInstance();
       vk = new VkApiClient(transportClient);
    }

    public static ApiWrapper getInstance() {
        if (instance == null) {
            instance = new ApiWrapper();
        }
        return instance;
    }

    @Step("Create a new post. OwnerId is {2}, fromGroup is {1}")
    public PostResponse createPost(UserActor actor, boolean isFromGroup, int ownerId) throws ClientException, ApiException {
        String guid = UUID.randomUUID().toString();
        waitBeforeRequest();
        return vk.wall()
                .post(actor)
                .fromGroup(isFromGroup)
                .ownerId(ownerId)
                .message("Hi, there this is my test message " + guid)
                .guid(guid)
                .execute();
    }


    @Step("Add like to {1} with id {2} owned by {3}")
    public AddResponse addLikeToItem(UserActor actor, LikesType type, int itemId, int ownerId) throws ClientException, ApiException {
        waitBeforeRequest();
        return vk.likes()
                .add(actor, type, itemId)
                .ownerId(ownerId)
                .execute();
    }


    @Step("Delete like from {1} with id {2} owned by {3}")
    public DeleteResponse removeLike(UserActor actor, LikesType type, int itemId, int ownerId) throws ClientException, ApiException {
        waitBeforeRequest();
        return vk.likes()
                .delete(actor, type, itemId)
                .ownerId(ownerId)
                .execute();
    }


    @Step("Request if {1} with id {2} and owned by {3} is liked by user {4}")
    public IsLikedResponse isItemLiked(UserActor actor, LikesType type, int itemId, int ownerId, int userId) throws ClientException, ApiException {
        waitBeforeRequest();
        return vk.likes()
                .isLiked(actor, type, itemId)
                .ownerId(ownerId)
                .userId(userId)
                .execute();
    }

   @Step("Get all likes from {1} with id {2} owned by {3}")
   public List<GetListResponse> getLikesForAnItem(UserActor actor, LikesType type, int itemId, int ownerId ) throws ClientException, ApiException {
        List<GetListResponse> getListResponses = new ArrayList<>();
        int likesLimit = 1000;
        int offset = 0;

        GetListResponse response = getLikes(actor, type, itemId, ownerId, likesLimit, offset);
        getListResponses.add(response);

        double numberOfRequestsToBeMade = response.getCount() / likesLimit;

        if (numberOfRequestsToBeMade > 1.00) {
            for (int i = 1; i < numberOfRequestsToBeMade; i++ ) {
                offset += likesLimit;
                getListResponses.add(getLikes(actor, type, itemId, ownerId, likesLimit, offset));
            }
        }
        return getListResponses;
    }

    private GetListResponse getLikes(UserActor actor, LikesType type, int itemId, int ownerId, int likesLimit, int offset) throws ClientException, ApiException {
        waitBeforeRequest();
        return vk.likes()
                .getList(actor, type)
                .ownerId(ownerId)
                .itemId(itemId)
                .count(likesLimit)
                .offset(offset)
                .execute();
    }

    private void waitBeforeRequest() {
        try {
            Thread.sleep(sleepUntilNextRequest);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        };
    }
}
