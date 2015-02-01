package com.github.alxwhtmr.cinematracker;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * The {@code AllMovies} class represents a collections
 * of all the processed movies.
 * First, it processes the movie from url resources (html & json)
 * Then, it adds this movie to the collection
 * It uses {@code Movie} class methods to process each movie
 *
 * @since 28.01.2015
 */
public class AllMovies {
    LinkedList<Movie> allMovies;

    public AllMovies() {
        allMovies = new LinkedList<Movie>();
    }

    @Override
    public String toString() {
        Utils.logInfo("allMovies.size()="+allMovies.size());
        StringBuffer buf = new StringBuffer();
        allMovies.forEach((movie) -> {
            if (movie.getImdbLink() != null) {
                buf.append(movie + "\n\n");
            }
        });
        return buf.toString();
    }



    public void sort() {
        allMovies.sort(Comparator.<Movie>naturalOrder());
    }

    public LinkedList<Movie> getAllMovies() {
        return allMovies;
    }

    public void setAllMovies() {
        Utils.logInfo(getClass());
        Set<Element> moviesNotParsed = getMoviesAsHtml();
        //
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, Constants.Misc.RANGE);
        Date until = c.getTime();
        //

        for (Element el : moviesNotParsed) {
            Movie movie = new Movie();
            String release = Arrays.asList(el.getElementsByClass(Constants.DOM.Classes.PREMIER_DATE).text()).get(0).trim();
            movie.setPremiere(release);
            if (movie.getPremiereAsDate().compareTo(until) == 1) {
                continue;
            }

            String titleRus = Arrays.asList(el.getElementsByClass(Constants.DOM.Classes.TITLE_FILM).text()).get(0).trim();
            movie.setTitleRus(titleRus);

            String titleEng = Arrays.asList(el.getElementsByClass(Constants.DOM.Classes.BIGTEXT).text())
                    .get(0).replaceAll(Constants.Replacements.Replaceable.BRACKETS, Constants.Replacements.Replacement.NULL).trim();
            if (titleEng.length() != 0) {
                movie.setTitleEng(titleEng);
            } else {
                movie.setTitleEng(null);
            }
            Utils.logDebug("new title=" + titleEng);
            movie.setTitleWordsList();
            movie.parseAndSetFieldsFromJson();

            Elements countryNotParsed = el.getElementsByClass(Constants.DOM.Classes.TEXT);

            try {
                String country = countryNotParsed.get(1).text().replaceAll(Constants.Replacements.Replaceable.COUNTRY, Constants.Replacements.Replacement.NULL).trim();
                movie.setCountry(country);
            } catch (IndexOutOfBoundsException e) {
                Utils.logErr("AllMovies.getMovies: set country " + e);
            }

            String director = Arrays.asList(el.getElementsByAttributeValue(Constants.DOM.Attributes.ITEMPROP_KEY, Constants.DOM.Attributes.ITEMPROP_VALUE_DIRECTOR).text()).get(0).trim();
            movie.setDirector(director);

            Elements genreNotParsed = el.getElementsByClass(Constants.DOM.Classes.TEXTGRAY);
            String genre = genreNotParsed.get(0).text().replaceAll(Constants.Replacements.Replaceable.GENRE, Constants.Replacements.Replacement.NULL).trim();
            movie.setGenre(genre);

            movie.setRating();
            if (movie.getRating() != Constants.IMDB.RATING_NOT_SET && movie.getRating() >= Constants.Movies.MIN_RATING) {
                allMovies.add(movie);
            }
        }
        Utils.logDebug("allMovies=" + allMovies);
    }

    private Set<Element> getMoviesAsHtml() {
        Utils.logInfo(getClass());
        final Set<Element> moviesNotParsed = new HashSet<Element>();
        Element body = Utils.getHtmlBody(Constants.URL.CURRENT_MOVIES);
        Elements titles = body.getElementsByClass(Constants.DOM.Classes.TITLE_FILM);
        titles.forEach((t) -> {
            moviesNotParsed.addAll(t.parent().parent().getElementsByTag(Constants.DOM.Tags.TR));
        });
        Utils.logDebug("AllMovies.getMoviesAsHtml " + moviesNotParsed);
        return moviesNotParsed;
    }
}
