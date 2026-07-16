package com.ndjana.rate.posts;

import com.ndjana.rate.models.User;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    private byte[] profile_picture;
    private byte[] cover_video;
    private List<String> musical_genres = new ArrayList<>();
    private User user;
}
