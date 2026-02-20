package pl.edu.agh.mwo.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();

//		HQL version for Laboratories, more: https://github.com/zurawskipiotr97-glitch/hibernate-blank.git

		main.addNewData();
		main.printState("Po addNewData");

//		main.case1_removeLike();
//		main.printState("Po case1_removeLike");

//		main.case2_deletePhoto();
//		main.printState("Po case2_deletePhoto");
//
		main.case3_deleteAlbum();
		main.printState("Po case3_deleteAlbum");
//
//		main.case4_deleteUser();
//		main.printState("Po case4_deleteUser");
//
//		main.executeQueries();

		// tu wstaw kod aplikacji
		
		main.close();
	}

	public Main() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void close() {
		session.close();
		HibernateUtil.shutdown();
	}

	private void addNewData() {

		User alice = new User();
		alice.setUsername("alice");

		User bob = new User();
		bob.setUsername("bob");

		User charlie = new User();
		charlie.setUsername("charlie");

		// znajomości (dwustronnie)
		alice.addFriend(bob);
		bob.addFriend(alice);

		Album aliceAlbum = new Album();
		aliceAlbum.setName("Alice Album 1");
		aliceAlbum.setDescription("Album Alice");

		Album bobAlbum = new Album();
		bobAlbum.setName("Bob Album 1");
		bobAlbum.setDescription("Album Boba");

		Photo alicePhoto1 = new Photo();
		alicePhoto1.setName("alice_photo_1");

		Photo alicePhoto2 = new Photo();
		alicePhoto2.setName("alice_photo_2");

		Photo bobPhoto1 = new Photo();
		bobPhoto1.setName("bob_photo_1");

		aliceAlbum.addPhoto(alicePhoto1);
		aliceAlbum.addPhoto(alicePhoto2);
		bobAlbum.addPhoto(bobPhoto1);

		alice.addAlbum(aliceAlbum);
		bob.addAlbum(bobAlbum);

		// likes (tylko znajomi)
		likeIfFriends(bob, alice, alicePhoto1);
		likeIfFriends(alice, bob, bobPhoto1);

		Transaction transaction = session.beginTransaction();
		session.save(alice);
		session.save(bob);
		session.save(charlie);
		transaction.commit();
	}

	private void likeIfFriends(User liker, User owner, Photo photo) {
		if (!liker.getFriends().contains(owner)) {
			throw new IllegalStateException("Brak znajomości");
		}
		liker.addLikedPhoto(photo);
		photo.addLikingUser(liker);
	}

	private void printState(String title) {

		System.out.println("\n==============================");
		System.out.println(title);
		System.out.println("==============================");

		Long users = session.createQuery("select count(u) from User u", Long.class).uniqueResult();
		Long albums = session.createQuery("select count(a) from Album a", Long.class).uniqueResult();
		Long photos = session.createQuery("select count(p) from Photo p", Long.class).uniqueResult();
		Long likes = session.createQuery("select count(p) from User u join u.likedPhotos p", Long.class).uniqueResult();

		System.out.println("Users:  " + users);
		System.out.println("Albums: " + albums);
		System.out.println("Photos: " + photos);
		System.out.println("Likes:  " + likes);

		List<User> userList = session.createQuery("from User", User.class).list();
		for (User u : userList) {
			System.out.println("\nUser: " + u.getUsername());
            if (!u.getAlbums().isEmpty()) {
                System.out.println("Albums:");
                for (Album album : u.getAlbums()) {
                    System.out.println("- " + album.getName());
                    if (!u.getFriends().isEmpty()) {
                        System.out.println("  Friends: ");
                        for (User friend : u.getFriends()) {
                            System.out.println("  - " + friend.getUsername());
                            if (!u.getLikedPhotos().isEmpty()) {
                                System.out.println("    LikedPhotos: ");
                                for (Photo likedP : u.getLikedPhotos()) {
                                    System.out.println("    - " + likedP.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
	}

	private void case1_removeLike() {
		User bob = session.createQuery(
				"from User u where u.username='bob'", User.class)
				.uniqueResult();

		Photo photo = session.createQuery(
				"from Photo p where p.name='alice_photo_1'", Photo.class)
				.uniqueResult();

		bob.getLikedPhotos().remove(photo);

		Transaction deleteTransaction = session.beginTransaction();
		deleteTransaction.commit();
	}

	private void case2_deletePhoto() {
		Photo photo = session.createQuery(
				"from Photo p where p.name='bob_photo_1'", Photo.class)
				.uniqueResult();

		Transaction deleteTransaction = session.beginTransaction();
		for (User u : photo.getLikedByUsers()) {
			u.removeLikedPhoto(photo);
		}
		Album album = session.createQuery(
				"Select a from Album a inner join a.photos p where p.name='bob_photo_1'", Album.class)
				.uniqueResult();

		album.getPhotos().remove(photo);

		session.delete(photo);
		deleteTransaction.commit();
	}

	private	void case3_deleteAlbum() {
		Transaction deleteTransaction = session.beginTransaction();

		Album album = session.createQuery(
						"from Album a where a.name='Alice Album 1'", Album.class)
				.uniqueResult();

		session.delete(album);
		deleteTransaction.commit();

	}
}
