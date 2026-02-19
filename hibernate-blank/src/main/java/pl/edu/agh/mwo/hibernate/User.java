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
    private long id;

    @Column(unique = true)
    private String username;

    @Column
    private Date joinDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Album> albums = new HashSet<Album>();

    @ManyToMany(mappedBy = "likedPhotos", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Photo> likedPhotos = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
