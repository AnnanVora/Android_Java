package annan.example.flickrbrowser;

import java.io.Serializable;

class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String author;
    private String authorId;
    private String tags;
    private String link;
    private String image;

    public Photo(String title, String author, String authorId, String tags, String link, String image) {
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.tags = tags;
        this.link = link;
        this.image = image;
    }

    String getTitle() {
        return title;
    }

    String getAuthor() {
        return author;
    }

    String getAuthorId() {
        return authorId;
    }

    String getTags() {
        return tags;
    }

    String getLink() {
        return link;
    }

    String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Photo" +
                " title = " + title +
                ", author = " + author +
                ", authorId = " + authorId +
                ", tags = " + tags +
                ", link = " + link +
                ", image = " + image;
    }


}
