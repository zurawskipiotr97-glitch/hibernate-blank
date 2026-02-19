package pl.edu.agh.mwo.hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private Date joinDate = new Date();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Album> albums = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "photo_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "photo_id")
    )
    private Set<Photo> likedPhotos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public Set<Photo> getLikedPhotos() {
        return likedPhotos;
    }

    public void setLikedPhotos(Set<Photo> likedPhotos) {
        this.likedPhotos = likedPhotos;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    public void addLikedPhoto(Photo photo) {
        likedPhotos.add(photo);
    }

    public void removeLikedPhoto(Photo photo) {
        likedPhotos.remove(photo);
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User user) {
        if (user == null || user == this) {return;}
        if (this.friends.add(user)) {
            user.friends.add(this);
        }
    }

    public void removeFriend (User user) {
        if (user == null || user == this) {return;}
        if (this.friends.remove(user)) {
            user.friends.remove(this);
        }
    }

    public boolean isFriendWith(User user) {
        return user != null && friends.contains(user);
    }

//    public void likePhoto(Photo photo) {
//        if (photo == null) return;
//
//        // Wymaga: Photo -> Album -> User
//        Album album = photo.getAlbum();
//        if (album == null) {
//            throw new IllegalStateException("Photo nie ma ustawionego albumu - nie da się sprawdzić właściciela.");
//        }
//
//        User owner = album.getOwner(); // albo getUser(), zależnie jak nazwałeś
//        if (owner == null) {
//            throw new IllegalStateException("Album nie ma ustawionego właściciela - nie da się sprawdzić znajomości.");
//        }
//
//        boolean canLike = this.equals(owner) || this.isFriendWith(owner);
//        if (!canLike) {
//            throw new IllegalStateException("Możesz polubić tylko zdjęcie znajomego (lub swoje).");
//        }
//
//        // Spójność obu stron:
//        if (likedPhotos.add(photo)) {
//            photo.getLikedByUsers().add(this); // Wymaga: Photo ma kolekcję likedByUsers
//        }
//    }
//
//    public void unlikePhoto(Photo photo) {
//        if (photo == null) return;
//
//        if (likedPhotos.remove(photo)) {
//            photo.getLikedByUsers().remove(this); // spójność
//        }
//    }

}
