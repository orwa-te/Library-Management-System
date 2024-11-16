package com.task.library.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String contactInformation;

    @OneToMany(mappedBy = "patron")
    private Set<BorrowingRecord> borrowingRecords;

    // Getters and Setters
}
