package com.example.ex02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "tbl_memo")
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mno;

    @Column(length=200, nullable = false)
    private String memoText;

}
