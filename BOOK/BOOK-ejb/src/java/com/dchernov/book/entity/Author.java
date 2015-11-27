/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.entity;

import com.dchernov.book.entity.abstractitem.Item;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Dc
 */
@Entity(name = "Author")
@Table(name = "author", uniqueConstraints = {
    @UniqueConstraint(name = "IDENTITY_KEY_Author", columnNames = {"firstname", "lastname"})})
public class Author extends Item {

    private static final long serialVersionUID = 1L;

    @Column(name = "firstname")
    String firstName;

    @Column(name = "lastname")
    String lastName;

    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    Set<Book> booksWritten;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Book> getBooksWritten() {
        return booksWritten;
    }

    public void setBooksWritten(Set<Book> booksWritten) {
        this.booksWritten = booksWritten;
    }

    @Override
    public String toString() {
        return super.toString() + " = {" + firstName + "," + lastName + "}";
    }

}
