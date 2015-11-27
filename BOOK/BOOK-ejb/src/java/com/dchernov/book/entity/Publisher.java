/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.book.entity;

import com.dchernov.book.entity.abstractitem.Item;
import com.dchernov.book.jaxb.PreventCycleXml;
import com.dchernov.book.jaxb.PreventCycleXmlJavaTypeAdapter;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Dc
 */
@Entity(name = "Publisher")
@Table(name = "publisher", uniqueConstraints = {
    @UniqueConstraint(name = "IDENTITY_KEY_Publisher", columnNames = {"name", "city"})})
@XmlJavaTypeAdapter(Publisher.PreventCycles.class)
public class Publisher extends Item implements PreventCycleXml {

    public static class PreventCycles extends PreventCycleXmlJavaTypeAdapter<Publisher> {
    }
    private static final Logger LOG = Logger.getLogger(Publisher.class.getName());

    private static final long serialVersionUID = 1L;
    @Column(name = "city")
    private String city;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "publisher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Book> booksPublished;

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
        return super.toString() + " = {" + name + "," + city + "}";
    }
}
