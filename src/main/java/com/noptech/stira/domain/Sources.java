package com.noptech.stira.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Sources.
 */
@Entity
@Table(name = "sources")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Sources implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "synced_to")
    private LocalDate syncedTo;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getSyncedTo() {
        return syncedTo;
    }
    
    public void setSyncedTo(LocalDate syncedTo) {
        this.syncedTo = syncedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sources sources = (Sources) o;
        if(sources.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sources.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sources{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", syncedTo='" + syncedTo + "'" +
            '}';
    }
}
