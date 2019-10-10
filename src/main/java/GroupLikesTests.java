import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupLikesTests extends BaseTest{

    @Test(description = "[likes.getList] Get list of all likes from a a post")
    @Description("Get all likes to a given post, and check that actual likes count and actual" +
            " number of userIds is equal to expected")
    public void getList() throws ClientException, ApiException {
        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);
        int expectedLikesCount = 1;

        List<GetListResponse> responsesList = api().getLikesForAnItem(actor, LikesType.POST, postId, groupId);

        int actualLikesCount = responsesList.get(0).getCount();

        int actualNumberOfUserIds = responsesList.stream()
                .map(GetListResponse::getItems)
                .map(List::size)
                .reduce(0, Integer::sum);

        assertThat(actualLikesCount).as("Actual likes count is equal to expected")
                .isEqualTo(expectedLikesCount);

        assertThat(actualNumberOfUserIds).as("Actual number of userIds (items) is equal to expected likes count")
                .isEqualTo(expectedLikesCount);
    }

    @Test(expectedExceptions = ApiAuthException.class,
            description = "[likes.getList] Fail to get likes with incorrect <access_token>")
    @Description("Expect to get ApiAuthException")
    public void getListWithIncorrectTokenFails() throws ClientException, ApiException {

        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);

        api().getLikesForAnItem(new UserActor(userId, "wrong_token"), LikesType.POST, postId, groupId);
    }


    @Test(description = "[likes.add] Add like to a post")
    public void addLike() throws ClientException, ApiException {
        UserActor actor = new UserActor(userId, accessToken);

        int postId = api().createPost(actor, false, groupId).getPostId();

        int numberOfLikes = api().addLikeToItem(actor, LikesType.POST, postId, groupId).getLikes();

        assertThat(numberOfLikes)
                .as("Number of likes from likes.add response is 1")
                .isEqualTo(1);

        List<GetListResponse> allLikes = api().getLikesForAnItem(actor, LikesType.POST, postId, groupId);

        assertThat(allLikes.get(0).getCount())
                .as("Number of likes from likes.getList response is 1")
                .isEqualTo(1);

        assertThat(allLikes.get(0).getItems().get(0))
                .as("Post was added to liked list of user with id " + userId)
                .isEqualTo(userId);

    }

    @Test(expectedExceptions = ApiParamException.class,
            description = "[likes.add] Fail to add like with incorrect <type>")
    @Description("Expect to get ApiParamException")
    public void addLikeWithIncorrectLikesTypeFails() throws ClientException, ApiException {
        int postId = api().createPost(actor, false, groupId).getPostId();

        api().addLikeToItem(actor, LikesType.PHOTO, postId, groupId).getLikes();
    }

    @Test(description = "[likes.delete] Delete like from a post")
    public void deleteLike() throws ClientException, ApiException{
        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);

        int likesCount = api().removeLike(actor, LikesType.POST, postId, groupId).getLikes();

        assertThat(likesCount)
                .as("Number of likes is 0")
                .isEqualTo(0);

        List<GetListResponse> allLikes = api().getLikesForAnItem(actor, LikesType.POST, postId, groupId);
        int usersWhoLikedThePostList = allLikes.get(0).getItems().size();

        assertThat(usersWhoLikedThePostList).as("Post is added to 0 number of users")
                .isEqualTo(0);

    }

    @Test(expectedExceptions = ApiAccessException.class, description = "[likes.delete] Fail to delete the same like twice")
    @Description("Expect to get ApiAccessException")
    public void deleteLikeTheSecondTimeFails() throws ClientException, ApiException{

        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);

        api().removeLike(actor, LikesType.POST, postId, groupId).getLikes();
        api().removeLike(actor, LikesType.POST, postId, groupId).getLikes();
    }


    @Test(description = "[likes.isLiked] Check if Post is liked by user")
    public void isLiked() throws ClientException, ApiException {
        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);

        boolean actualIsLiked = api().isItemLiked(actor,  LikesType.POST, postId, groupId, userId).isLiked();

        assertThat(actualIsLiked).as("Liked should be 1 (true)")
                .isTrue();
    }

    @Test(expectedExceptions = ApiParamException.class,
            description = "[likes.isLikes] Fail to get isLiked if <owner_id> is a negative number")
    @Description("Expect to get ApiParamException")
    public void isLikedByIncorrectUserIdFails() throws ClientException, ApiException {
        int postId = api().createPost(actor, false, groupId).getPostId();
        api().addLikeToItem(actor, LikesType.POST, postId, groupId);

        api().isItemLiked(actor,  LikesType.POST, postId, groupId, -88888).isLiked();
    }
}
