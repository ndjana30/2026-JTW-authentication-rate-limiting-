package com.ndjana.rate.posts;

import com.ndjana.rate.models.*;
import com.ndjana.rate.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class Rest {

    private final BiographieRepo biographieRepo;
    private final UserRepository userRepository;
    private final ProfileRepo profileRepo;
    private final GalleryRepo galleryRepo;
    private final MediaRepository mediaRepository;


    @PostMapping("create-bio")
    public ResponseEntity<?> createBiography(@RequestBody BiographieRequest biographieRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user  = userRepository.findByEmail(userDetails.getUsername());
        if (user.isPresent())
        {
            var bio = Biographie.builder()
                    .user(user.get())
                    .text(biographieRequest.getText())
                    .build();
            biographieRepo.save(bio);
            user.get().setBiographie(bio);
            userRepository.save(user.get());
            return new ResponseEntity<>("Bio created", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("User not existing",HttpStatus.BAD_REQUEST);


    }

    @PostMapping("modify-profile")
    public Object modifyProfile(
            @RequestParam("profile_picture")MultipartFile profile_picture,
            @RequestParam("cover_video")MultipartFile cover_video,
            @RequestParam("musical_genres") String[] musical_genres
    )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        Optional<User> user  = userRepository.findByEmail(userDetails.getUsername());
        if (user.isPresent())
        {
            try{
                Profile profile = user.get().getProfile();
                profile.setProfile_picture(profile_picture.getBytes());
                profile.setCover_video(cover_video.getBytes());
                profile.setMusical_genres(Arrays.stream(musical_genres).toList());
                profile.setUser(user.get());
                profileRepo.save(profile);
                return new ResponseEntity<>("Profile updated", HttpStatus.OK);
            }
            catch (Exception e)
            {
                return new ResponseEntity<>("Could not modify profile, because: "+e.getMessage(),HttpStatus.OK);
            }

        }
        return new ResponseEntity<>("User not authenticated",HttpStatus.FORBIDDEN);
    }

    @PostMapping("gallery-modify")
    @Transactional
    public ResponseEntity<?> modifyGallery(@RequestParam("medias") MultipartFile[] data) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if (user.isPresent()) {
            Gallerie gallerie = user.get().getGallerie();
            if (gallerie == null) {
                return new ResponseEntity<>("Gallery not found for user", HttpStatus.NOT_FOUND);
            }

            for (MultipartFile file : data) {
                if (!file.isEmpty()) {
                    Media media = Media.builder()
                            .data(file.getBytes())
                            .build();
                    gallerie.addMedia(media);
                }
            }
            galleryRepo.save(gallerie);
            return new ResponseEntity<>("Gallery Updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not present", HttpStatus.FORBIDDEN);
    }

    /*@PostMapping("gallery-modify")
    @Transactional
    public Object modifyGallery(@RequestParam ("medias") MultipartFile[] data) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        Optional<User> user  = userRepository.findByEmail(userDetails.getUsername());
        if(user.isPresent())
        {
            Gallerie gallerie = user.get().getGallerie();
            System.out.println("Initial gallery is:  " + gallerie.getMedias() + "With ID "+gallerie.getId());
            for(MultipartFile file: data)
            {
                var media = Media.builder()
                        .data(file.getBytes())
                        .build();
                gallerie.addMedia(media);
                galleryRepo.save(gallerie);
                return new ResponseEntity<>("Gallery Updated", HttpStatus.OK);
            }


        }
        return new ResponseEntity<>("User not present",HttpStatus.FORBIDDEN);
    }*/


    @GetMapping("gallery-view")
    public Object ViewGallery() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        Optional<User> user  = userRepository.findByEmail(userDetails.getUsername());
        if(user.isPresent())
        {
            Gallerie gallerie = user.get().getGallerie();
            List<Media> medias = gallerie.getMedias();
            List<String> objects = new ArrayList<>();

            for(Media file : medias)
            {
                System.out.println(Arrays.toString(file.getData()) +" NEXT FILE: ");
                objects.add(Arrays.toString(file.getData()));
            }

            return new ResponseEntity<>(objects,HttpStatus.OK);
        }
        return new ResponseEntity<>("User not present",HttpStatus.FORBIDDEN);
    }




}
