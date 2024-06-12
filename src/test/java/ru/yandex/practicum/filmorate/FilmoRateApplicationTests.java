package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FilmDbStorage filmStorage;

    @Test
    public void findUserByIdTest() {
        User user = userStorage.add(createUser1());
        User userTest = userStorage.get(user.getId());
        Assertions.assertEquals(user, userTest);
    }

    @Test
    public void deleteUserTest() {
        User user1 = userStorage.add(createUser1());
        userStorage.delete(user1);
        UserNotFoundException ex = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userStorage.get(user1.getId())
        );
        Assertions.assertEquals(ex.getMessage(), "Пользователь с id " + user1.getId() + " не найден!");
    }

    @Test
    public void getAllUsersTest() {
        userStorage.add(createUser1());
        userStorage.add(createUser2());
        List<User> users = userStorage.getAllUser();
        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    public void updateUserTest() {
        User user = userStorage.add(createUser1());
        user.setLogin("Login update");
        userStorage.update(user);
        User userTest = userStorage.get(user.getId());
        Assertions.assertEquals(userTest.getLogin(), "Login update");
    }

    @Test
    public void addFriendTest() {
        User user2 = userStorage.add(createUser2());
        User user1 = userStorage.add(createUser1());
        createFriends(user1, user2);
        User user1Test = userStorage.get(user1.getId());
        User user2Test = userStorage.get(user2.getId());
        Assertions.assertEquals(user1Test.getFriends().size(), 1);
        Assertions.assertEquals(user2Test.getFriends().size(), 1);
    }

    @Test
    public void removeFriend() {
        User user2 = userStorage.add(createUser2());
        User user1 = userStorage.add(createUser1());
        createFriends(user1, user2);
        userStorage.removeFriend(user1.getId(), user2.getId());
        Assertions.assertEquals(user1.getFriends().size(), 0);
    }

    @Test
    @Sql(scripts = "/sql/ratings_and_genres.sql")
    public void findFilmByIdTest() {
        Film film1 = filmStorage.add(createFilm1());
        Film filmTest = filmStorage.get(film1.getId());
        Assertions.assertEquals(filmTest, film1);
    }

    @Test
    public void updateFilmTest() {
        Film film1 = filmStorage.add(createFilm1());
        film1.setName("Update film");
        filmStorage.update(film1);
        Film filmTest = filmStorage.get(film1.getId());
        Assertions.assertEquals(filmTest.getName(), "Update film");
    }

    @Test
    public void deleteFilmTest() {
        Film film1 = filmStorage.add(createFilm1());
        filmStorage.delete(film1);
        FilmNotFoundException ex = Assertions.assertThrows(
                FilmNotFoundException.class,
                () -> filmStorage.get(film1.getId())
        );
        Assertions.assertEquals(ex.getMessage(), "Ошибка получения фильма " + film1.getId() + "!");
    }

    @Test
    public void getAllFilmsTest() {
        filmStorage.add(createFilm1());
        filmStorage.add(createFilm2());
        List<Film> films = filmStorage.getAllFilms();
        Assertions.assertEquals(films.size(), 2);
    }

    @Test
    public void addLikeTest() {
        User user = userStorage.add(createUser1());
        Film film = filmStorage.add(createFilm1());
        filmStorage.addLike(user.getId(), film.getId());
        Film filmTest = filmStorage.get(film.getId());
        Assertions.assertEquals(filmTest.getLikesCount(), 1);
    }

    @Test
    public void deleteLikeTest() {
        User user = userStorage.add(createUser1());
        Film film = filmStorage.add(createFilm1());
        filmStorage.addLike(user.getId(), film.getId());
        filmStorage.deleteLike(user.getId(), film.getId());
        Film filmTest = filmStorage.get(film.getId());
        Assertions.assertEquals(filmTest.getLikesCount(), 0);
    }

    private Film createFilm1() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Desc film");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120L);
        film.setRatingId(1L);
        film.setMpa(new Rating(1, "G"));
        return film;
    }

    private Film createFilm2() {
        Film film = new Film();
        film.setName("Test Film2");
        film.setDescription("Desc film2");
        film.setReleaseDate(LocalDate.now().minusDays(100));
        film.setDuration(120L);
        film.setRatingId(1L);
        return film;
    }

    private User createUser1() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());
        user.setEmail("test@mail.ru");
        return user;
    }

    private User createUser2() {
        User user = new User();
        user.setLogin("Login2");
        user.setName("Name2");
        user.setBirthday(LocalDate.now().minusDays(100));
        user.setEmail("test2@mail.ru");
        return user;
    }

    private void createFriends(User user1, User user2) {
        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user2.getId(), user1.getId());
    }
}