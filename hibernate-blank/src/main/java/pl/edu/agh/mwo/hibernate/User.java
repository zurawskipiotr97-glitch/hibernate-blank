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
    private Set<Photo> likedPhotosS = new HashSet<>();


}
