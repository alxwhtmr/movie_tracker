package com.github.alxwhtmr.cinematracker;

/**
 * {@code Constants} contains all the invariable data
 *
 * @since 27.01.2015
 */
public class Constants {
    public static final double APP_VERSION = 0.3; // 01.02.2015
//            0.2; //31.01.2015

    public class Logs {
        public static final String FORMAT = "%-10s %s : %s";
        public class Debug {
            public static final boolean ENABLED = false;
            public static final String TITLE = "[DEBUG]";
        }
        public class Errors {
            public static final boolean ENABLED = true;
            public static final String TITLE = "[ERROR]";
        }
        public class Info {
            public static final boolean ENABLED = true;
            public static final String TITLE = "[INFO]";
        }
    }

    public static class UI {
        public static final String TABLE_LABEL_FONT = "Arial Narrow Bold";
        public static final String APP_TITLE    = "Movie tracker v" + APP_VERSION;
        public static final String FORMAT_LABEL1 = "Recommended movies for the next %d days";
        public static final String FORMAT_LABEL2 = "with a minimum rating of %.1f";
        public static final String CSS_FILE = "style.css";

        public static final int IMAGE_HEIGHT = 100;
        public static final int STAGE_WIDTH  = 700;
        public static final int STAGE_HEIGHT = 700;
        public static final int TABLE_WIDTH  = STAGE_WIDTH - 100;
        public static final double RATING_COL_MULTIPLIER = 0.5;

        public class Images {
            public static final String LOAD_IMG = "load_icon.gif";
            public static final String APP_ICON = "app_icon.png";
        }
    }

    public static class IMDB {
        public static final String API              = "http://sg.media-imdb.com/suggests/";
        public static final String LINK_BEGIN       = "http://www.imdb.com/title/";
        public static final String TABLE_LINK_PREFIX= "imdb.com/title/";
        public static final String CHARS_TO_DELETE  = "imdb$%s(";
        public static final double RATING_NOT_SET   = -1.0;
        public static final int[]
                LETTERS_FOR_JSON_QUERY_SUBSTRING    = {4, 3, 5, 6, 7};
    }

    public class URL {
        public static final String CURRENT_MOVIES = "http://www.kinonews.ru/premiers/";
    }

    public static class Misc {
        public static int RANGE = 5;
    }

    public static class Movies {
        public static double MIN_RATING = 7.0;

        public static final String TITLE_LABEL      = "Title";
        public static final String RATING_LABEL     = "Rating";
        public static final String PREMIERE_LABEL   = "Premiere";
        public static final String GENRE_LABEL      = "Genre";
        public static final String COUNTRY_LABEL    = "Country";
        public static final String DIRECTOR_LABEL   = "Director";
        public static final String DATE_FORMATTER   = "dd.MM.yyyy";
        public static final String IMDB_LINK_LABEL  = "IMDB";
        public static final String IMAGE_LINK_LABEL = "img";
        public static final String TO_STRING_FORMAT = "%-10s%-10s (%s)\n%-10s%-10.1f\n"+
                                                           "%-10s%-10s\n%-10s%-10s\n"+
                                                           "%-10s%-10s\n%-10s%-10s\n"+
                                                           "%-10s%-10s\n%-10s%-10s";
    }

    public class DOM {
        public class Classes {
            public static final String TITLE_FILM   = "titlefilm";
            public static final String PREMIER_DATE = "premier-date";
            public static final String TEXT         = "text";
            public static final String TEXTGRAY     = "textgray";
            public static final String BIGTEXT      = "bigtext";
        }
        public class Tags {
            public static final String TR = "tr";
        }
        public class Attributes {
            public static final String ITEMPROP_KEY             = "itemprop";
            public static final String ITEMPROP_VALUE_DIRECTOR  = "director";
            public static final String ITEMPROP_VALUE_RATING    = "ratingValue";
        }
    }

    public class Replacements {
        public class Replaceable {
            public static final String COUNTRY  = "Страна[:?]";
            public static final String GENRE    = "Жанр[:?]";
            public static final String BRACKETS = "\\(|\\)";
            public static final String ARTICLE = "the|an|a|un|el|to|of";
        }
        public class Replacement {
            public static final String NULL = "";
        }
    }
}
