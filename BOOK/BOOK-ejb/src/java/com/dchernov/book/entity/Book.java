/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.entity;

import com.dchernov.book.entity.abstractitem.Item;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Dc
 */
@Entity(name = "Book")
@Table(name = "book", uniqueConstraints = {
    @UniqueConstraint(name = "IDENTITY_KEY_Book", columnNames = {"title", "authorshash", "publisher_id", "yearofpublishing"})}, indexes = {
    @Index(columnList = "publisher_id")}
)
public class Book extends Item {

    private static final Logger LOG = Logger.getLogger(Book.class.getName());

    private static final long serialVersionUID = 1L;
    String title;
    Set<Author> authors;
    Publisher publisher;
    Integer yearOfPublishing;
    String authorsHash;

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "authorshash")
    public String getAuthorsHash() {
        StringBuilder r = new StringBuilder();
        Set<Author> atrs = getAuthors();
        if (atrs != null) {
            for (Author a : atrs) {
                r.append(a.getId()).append(';');
            }
        }
        return r.toString();
    }

    public void setAuthorsHash(String v) {

    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(name = "publisher_fk", foreignKeyDefinition = "FOREIGN KEY ( publisher_id ) REFERENCES publisher ON DELETE RESTRICT"))
    @XmlJavaTypeAdapter(Publisher.PreventCycles.class)
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Column(name = "yearofpublishing")
    public Integer getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(Integer yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    @Override
    public String toString() {
        Set<Author> atrs = getAuthors();
        String[] authors = atrs == null ? new String[]{""} : new String[atrs.size()];
        if (atrs != null) {
            int i = 0;
            for (Author a : atrs) {
                authors[i++] = a.toString();
            }
        }
        return super.toString()
                + " = {" + title + " {" + String.join(",", Arrays.asList(authors)) + "} "
                + publisher + " " + yearOfPublishing + "}";
    }

}
