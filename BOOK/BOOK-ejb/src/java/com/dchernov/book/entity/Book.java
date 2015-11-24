/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Dc
 */
@Entity(name = "Book")
@Table(name = "book", uniqueConstraints = {
    @UniqueConstraint(name = "IDENTITY_KEY", columnNames = {"title", "publisher_id", "year"})}, indexes = {
    @Index(columnList = "publisher_id")}
)
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    Publisher publisher;
    Integer yearOfPublishing;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY ( publisher_id ) REFERENCES publisher ON DELETE RESTRICT"))
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Column(name = "year_of_publishing")
    public Integer getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(Integer yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    @Override
    public String toString() {
        return "com.dchernov.book.entity.Book[ id=" + id + " ] = {" + title + "," + publisher + "}";
    }

}
