package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "visitors")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "visitor", cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE } )
    private Set<HistoryOfLending> historyOfLending = new LinkedHashSet<>();
}
