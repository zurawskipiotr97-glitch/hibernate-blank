package pl.edu.agh.mwo.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();

		//Add New Data
		main.databaseRESET();
		main.addNewData();
		main.printState("Po addNewData");

		//Remove Like
		main.preparing();
		String likeUserName = "bob";
		String likePhotoName = "alice_photo_1";
		main.case1_removeLike(likeUserName, likePhotoName);
		main.printState("Po case1_removeLike");

		//Remove Photo
		main.preparing();
		String photoName = "bob_photo_1";
		main.case2_deletePhoto(photoName);
		main.printState("Po case2_deletePhoto");

		//Remove Album
		main.preparing();
		String albumName = "Alice Album 1";
		main.case3_deleteAlbum(albumName);
		main.printState("Po case3_deleteAlbum");

		//Delete User
		main.preparing();
		String username = "alice";
		main.case4_deleteUser(username);
		main.printState("Po case4_deleteUser");

		// Like when FRIENDS (should succeed)
		main.preparing();
		main.case5_likeWhenFriends("bob", "alice", "alice_photo_2");
		main.printState("Po case5_likeWhenFriends");

		// Like when NOT FRIENDS (should be blocked)
		main.preparing();
		main.case6_likeWhenNonFriends("charlie", "alice", "alice_photo_2");
		main.printState("Po case6_likeWhenNonFriends");

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

		List<User> userList = session
				.createQuery("from User u order by u.username", User.class)
				.list();

		for (User u : userList) {

			System.out.println("\nUser: " + u.getUsername());

			// Albums
			System.out.println("  Albums:");
			if (u.getAlbums().isEmpty()) {
				System.out.println("    -------");
			} else {
				for (Album album : u.getAlbums()) {
					System.out.println("    - " + album.getName());
				}
			}

			// Friends
			System.out.println("  Friends:");
			if (u.getFriends().isEmpty()) {
				System.out.println("    -------");
			} else {
				for (User friend : u.getFriends()) {
					System.out.println("    - " + friend.getUsername());
				}
			}

			// Liked photos
			System.out.println("  LikedPhotos:");
			if (u.getLikedPhotos().isEmpty()) {
				System.out.println("    -------");
			} else {
				for (Photo likedP : u.getLikedPhotos()) {
					System.out.println("    - " + likedP.getName());
				}
			}
		}
	}

	private void case1_removeLike(String likeUserName, String likePhotoName) {
		User bob = session.createQuery(
				"from User u where u.username= :lun", User.class)
				.setParameter("lun", likeUserName)
				.uniqueResult();

		Photo photo = session.createQuery(
				"from Photo p where p.name= :lpn", Photo.class)
				.setParameter("lpn", likePhotoName)
				.uniqueResult();

		bob.getLikedPhotos().remove(photo);

		Transaction deleteTransaction = session.beginTransaction();
		deleteTransaction.commit();
	}

	private void case2_deletePhoto(String photoName) {
		Photo photo = session.createQuery(
				"from Photo p where p.name= :pn", Photo.class)
				.setParameter("pn", photoName)
				.uniqueResult();

		Transaction deleteTransaction = session.beginTransaction();
		for (User u : photo.getLikedByUsers()) {
			u.removeLikedPhoto(photo);
		}
		Album album = session.createQuery(
				"Select a from Album a inner join a.photos p where p.name= :pn", Album.class)
				.setParameter("pn", photoName)
				.uniqueResult();

		album.getPhotos().remove(photo);

		session.delete(photo);
		deleteTransaction.commit();
	}

	private	void case3_deleteAlbum(String albumName) {
		Transaction deleteTransaction = session.beginTransaction();

		Album album = session.createQuery(
				"from Album a where a.name = :an", Album.class)
				.setParameter("an", albumName)
				.uniqueResult();

		for (Photo p : album.getPhotos()){
			for (User u : p.getLikedByUsers()){
				u.removeLikedPhoto(p);
			}
		}

		User user = session.createQuery(
				"Select u from User u inner join u.albums a where a.name = :an", User.class)
				.setParameter("an", albumName)
				.uniqueResult();

		user.getAlbums().remove(album);

		session.delete(album);
		deleteTransaction.commit();
	}

	private void case4_deleteUser(String username) {
		Transaction deleteTransaction = session.beginTransaction();

		User user = session.createQuery(
				"from User u where u.username = :un", User.class)
				.setParameter("un", username)
				.uniqueResult();

		for (User u : user.getFriends()) {
			u.removeFriend(user);
			user.removeFriend(u);
		}

		session.delete(user);
		deleteTransaction.commit();
	}

	private void databaseRESET() {
		Transaction deleteTransaction = session.beginTransaction();

		Query<User> query = session.createQuery("from User", User.class);
		List<User> users = query.list();

		for (User u : users) {
			for (User uf : u.getFriends()) {
				u.removeFriend(uf);
				uf.removeFriend(u);
			}
			session.delete(u);
		}
		deleteTransaction.commit();

		System.out.println("\n==============================================");
		System.out.println("Przygotowanie do prezentacji następnej metody");
		System.out.println("==============================================");
	}

	private void preparing() {
		databaseRESET();
		addNewData();
		printState("Po Przygotowaniu");
	}

	private void case5_likeWhenFriends(String likerUsername, String ownerUsername, String photoName) {
		Transaction transaction = session.beginTransaction();

		User liker = session.createQuery(
				"from User u where u.username = :lun", User.class)
				.setParameter("lun", likerUsername)
				.uniqueResult();

		User owner = session.createQuery(
				"from User u where u.username = :oun", User.class)
				.setParameter("oun", ownerUsername)
				.uniqueResult();

		Photo photo = session.createQuery(
				"from Photo p where p.name = :pn", Photo.class)
				.setParameter("pn", photoName)
				.uniqueResult();

		likeIfFriends(liker, owner, photo);
		System.out.println("[OK] Like dodany: " + likerUsername + " -> " + photoName);

		transaction.commit();
	}

	private void case6_likeWhenNonFriends(String likerUsername, String ownerUsername, String photoName) {
		Transaction transaction = session.beginTransaction();

		User liker = session.createQuery(
						"from User u where u.username = :lun", User.class)
				.setParameter("lun", likerUsername)
				.uniqueResult();

		User owner = session.createQuery(
						"from User u where u.username = :oun", User.class)
				.setParameter("oun", ownerUsername)
				.uniqueResult();

		Photo photo = session.createQuery(
						"from Photo p where p.name = :pn", Photo.class)
				.setParameter("pn", photoName)
				.uniqueResult();

		try {
			likeIfFriends(liker, owner, photo);
			System.out.println("UWAGA: Like przeszedł, a nie powinien!");
		} catch (IllegalStateException e) {
			System.out.println("[OK] Zablokowano like (brak znajomości): "
					+ likerUsername + " -> " + ownerUsername + " / " + photoName);
		}

		transaction.commit();
	}
}
