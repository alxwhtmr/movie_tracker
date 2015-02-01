package com.github.alxwhtmr.cinematracker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The {@code Movie} class represents a movie entity.
 * It contains all the necessary fields and methods
 * for processing movies
 *
 * @since 28.01.2015
 */
public class Movie implements Comparable {
    private String titleRus;
    private String titleEng;
    private String titleAbbr;
    private String genre;
    private String country;
    private String director;
    private String imdbLink = null;
    private String imdbLinkShort;
    private String imgLink;
    private LinkedList<String> titleWordsList = null;
    private Date premiere;
    private double rating = Constants.IMDB.RATING_NOT_SET;
    private SimpleDateFormat formatter;

    public Movie() {
        formatter = new SimpleDateFormat(Constants.Movies.DATE_FORMATTER);
        titleEng = "blank";
        genre = "blank";
        director = "blank";
    }

    @Override
    public String toString() {
        StringBuffer titleWords  = new StringBuffer();
        if (titleWordsList != null) {
            titleWordsList.forEach((w) -> {
                titleWords.append(w + " ");
            });
        }
        return String.format(Constants.Movies.TO_STRING_FORMAT,
                Constants.Movies.TITLE_LABEL     , (titleEng != null ? titleEng : titleRus), (titleWords != null ? titleWords.toString().trim() : "blank"),
                Constants.Movies.RATING_LABEL    , rating,
                Constants.Movies.PREMIERE_LABEL  , formatter.format(premiere),
                Constants.Movies.GENRE_LABEL     , genre,
                Constants.Movies.COUNTRY_LABEL   , country,
                Constants.Movies.DIRECTOR_LABEL  , director,
                Constants.Movies.IMDB_LINK_LABEL , imdbLink,
                Constants.Movies.IMAGE_LINK_LABEL, imgLink);
    }

    @Override
    public int compareTo(Object o) {
        Movie otherMovie = (Movie)o;
        int result = getPremiereAsDate().compareTo(otherMovie.getPremiereAsDate());
        return result;
    }

    public Date getPremiereAsDate() {
        return premiere;
    }

    public void setTitleWordsList(LinkedList<String> titleWordsList) {
        this.titleWordsList = titleWordsList;
    }

    public void setTitleWordsList() {
        LinkedList<String> titleWordsList = new LinkedList<String>();
        String title = null;
        if (getTitleEng() != null) {
            title = getTitleEng().toLowerCase();
            String[] words = title.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].matches(Constants.Replacements.Replaceable.ARTICLE)) {
                    continue;
                }
                titleWordsList.add(words[i].replaceAll(":", ""));
            }
            setTitleWordsList(titleWordsList);
        } else {
            setTitleWordsList(null);
        }
    }

    public void parseAndSetFieldsFromJson() {
        if (titleEng != null) {
            for (String titleWord : titleWordsList) {
                Set<String> queries = new TreeSet<String>();
                for (int letters : Constants.IMDB.LETTERS_FOR_JSON_QUERY_SUBSTRING) {
                    StringBuffer querySubstring = new StringBuffer();
                    for (int j = 0; j < letters && j < titleWord.length(); j++) {
                        querySubstring.append(titleWord.charAt(j));
                    }
                    queries.add(querySubstring.toString());
                }
                for (String query : queries) {
                    String jsonContent = Utils.getJsonFileFromUrl(Constants.IMDB.API, query);
                    if (jsonContent != null) {
                        if (parseJson(jsonContent)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean parseJson(String jsonContent) {
        Object obj = JSONValue.parse(jsonContent);
        JSONObject jsonObject = (JSONObject)obj;

        // Get all records from json file and the array:
        JSONArray mainContent = (JSONArray) jsonObject.get("d");

        String imdbLink = null;
        String imgLink = null;

        // Parse every record:
        for (Object o : mainContent) {
            JSONObject jo = (JSONObject) o;
            String title = (String)jo.get("l");

            //@TODO Delete this
            if (jo.get("y") != null) {
                try {
                    Long year = (Long) jo.get("y");
                    if (year < 2013) continue;
                } catch (NullPointerException e) {
                    Utils.logErr("Movie.parseJson " + getTitleEng() + ": " + e);
                }
            }

            if (title.equalsIgnoreCase(getTitleEng())) {
                try {
                    imdbLink = (String) jo.get("id");
                } catch (NullPointerException e) {
                    Utils.logDebug("Movie.parseJson: " + e);
                }
                imdbLinkShort = Constants.IMDB.TABLE_LINK_PREFIX + imdbLink;
                imdbLink = Constants.IMDB.LINK_BEGIN + imdbLink;
                this.imdbLink = imdbLink;

                try {
                    JSONArray img = (JSONArray) jo.get("i");
                    imgLink = img.get(0).toString();
                } catch (NullPointerException e) {
                    Utils.logDebug("Movie.parseJson: " + e);
                }
                setImgLink(imgLink);
                return true;
            }
        }
        return false;
    }

    public void setRating() {
        if (getImdbLink() != null) {
            Element body = Utils.getHtmlBody(getImdbLink());

            try {
                Element ratingEl = body.getElementsByAttributeValue(Constants.DOM.Attributes.ITEMPROP_KEY, Constants.DOM.Attributes.ITEMPROP_VALUE_RATING).get(0);
                double rating = Double.parseDouble(ratingEl.text());
                setRating(rating);
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
            }
        }
    }

    public String getTitleRus() {
        return titleRus;
    }

    public void setTitleRus(String titleRus) {
        this.titleRus = titleRus;
    }

    public String getTitleEng() {
        return titleEng;
    }

    public void setTitleEng(String titleEng) {
        this.titleEng = titleEng;
    }

    public String getPremiere() {
        return formatter.format(premiere);
    }

    public void setPremiere(String premiere) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.Movies.DATE_FORMATTER);
        Date date = null;
        try {
            date = formatter.parse(premiere);
        } catch (ParseException e) {
            Utils.logErr("Movie.setPremiere: " + e);
        }
        this.premiere = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitleAbbr() {
        return titleAbbr;
    }

    public void setTitleAbbr(String titleAbbr) {
        this.titleAbbr = titleAbbr;
    }

    public LinkedList<String> getTitleWordsList() {
        return titleWordsList;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public String getImdbLinkShort() {
        return imdbLinkShort;
    }

    public void setImdbLink(String imdbLink) {
        this.imdbLink = imdbLink;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}
