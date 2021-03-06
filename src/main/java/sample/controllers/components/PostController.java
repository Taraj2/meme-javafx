package sample.controllers.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sample.State;
import sample.controllers.pages.MainPage;
import sample.controllers.pages.MemePage;
import sample.controllers.pages.TagPage;
import sample.controllers.pages.UserPage;
import sample.dto.in.Post;
import sample.dto.in.Tag;
import sample.dto.out.AddFeedback;
import sample.util.AlertsFactory;
import sample.util.SuperComponent;

import java.util.List;
import java.util.stream.Collectors;


public class PostController extends SuperComponent {


    @FXML
    private Label title;

    @FXML
    private Label author;

    @FXML
    private Label date;

    @FXML
    private ImageView image;

    @FXML
    private Button likeButton;

    @FXML
    private Button dislikeButton;


    private Post post;
    @FXML
    private HBox tagContainer;

    @FXML
    private void like() {
        addFeedback(true);
    }

    @FXML
    private void dislike() {
        addFeedback(false);
    }

    @FXML
    private void add() {
        postsService.confirmPost(post.getId(), State.getToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    AlertsFactory.responseStatusError(response.errorBody());
                    return;
                }
                router.accept(MainPage.class, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                AlertsFactory.apiCallError(throwable);
            }
        });
    }

    @FXML
    private void delete() {

        postsService.deletePost(post.getId(), State.getToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    AlertsFactory.responseStatusError(response.errorBody());
                    return;
                }
                router.accept(MainPage.class, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                AlertsFactory.apiCallError(throwable);
            }
        });
    }

    private void addFeedback(boolean isLike) {
        postsService.addFeedback(post.getId(), new AddFeedback(
                isLike
        )).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    AlertsFactory.responseStatusError(response.errorBody());
                    return;
                }
                reLoadPost();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                AlertsFactory.apiCallError(throwable);
            }
        });
    }

    private void reLoadPost() {
        postsService.getPostById(post.getId()).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    AlertsFactory.responseStatusError(response.errorBody());
                    return;
                }

                if (response.body() != null) {
                    post = response.body();
                    Platform.runLater(PostController.this::setPostData);
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable throwable) {
                AlertsFactory.apiCallError(throwable);
            }
        });
    }

    private void setPostData() {
        title.setText(post.getTitle());
        author.setText(post.getAuthor().getNickname());
        Image img = new Image(post.getUrl());
        date.setText(post.getCreatedAt().toString());
        likeButton.setText("+" + post.getLikes());
        dislikeButton.setText("-" + post.getDislikes());
        image.setImage(img);
        setTags(post.getTags());
        image.setFitHeight(img.getHeight() * 2);
    }

    private void setTags(List<Tag> tags) {
        List<Label> tagList = tags.stream().map(this::addTag)
                .collect(Collectors.toList());
        Platform.runLater(() -> tagContainer.getChildren().setAll(tagList));
    }

    private Label addTag(Tag tag) {
        Label label = new Label();
        label.setText("#" + tag.getName());
        label.setTextFill(Paint.valueOf("#FFFFFF"));
        label.setFont(Font.font(15));
        label.setUserData(tag);
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(this::openTag);
        return label;
    }

    public void load(Post post) {
        this.post = post;
        setPostData();
    }


    @FXML
    private void openMeme() {
        router.accept(MemePage.class, new MemePage.Props((int) post.getId()));
    }

    @FXML
    private void openUser() {
        router.accept(UserPage.class, new UserPage.Props(1, post.getAuthor().getNickname()));
    }

    private void openTag(MouseEvent event) {
        Label label = (Label) event.getSource();
        Tag tag = (Tag) label.getUserData();
        router.accept(TagPage.class, new TagPage.Props(1, tag.getName()));
    }
}
