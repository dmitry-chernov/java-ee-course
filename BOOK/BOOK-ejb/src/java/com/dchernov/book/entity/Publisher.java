/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Dc
 */
@Entity(name = "Publisher")
@Table(name = "publisher", uniqueConstraints = {
    @UniqueConstraint(name = "IDENTITY_KEY", columnNames = {"name", "city"})})
public class Publisher implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "city")
    private String city;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "publisher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Book> booksPublished;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooksPublished() {
        return booksPublished;
    }

    public void setBooksPublished(Set<Book> booksPublished) {
        this.booksPublished = booksPublished;
    }

    @Override
    public String toString() {
        String r = "com.dchernov.book.entity.Publisher[ id=" + id + " ]={" + name + "," + city
                + ", [";
        for (Book b : booksPublished) {
            r += "{" + b + "}";
        }
        r += "]}";
        return r;
    }
}
