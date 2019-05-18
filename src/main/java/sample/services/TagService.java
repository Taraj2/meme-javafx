package sample.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sample.dto.in.Post;
import sample.dto.in.Tag;

import java.util.List;

public interface TagService {

    @GET("tags")
    Call<List<Tag>> getTags(@Query("offset") int offset);

    @GET("tags/{name}/posts")
    Call<List<Post>> getPosts(@Path("id") String name, @Query("offset") int offset);
}