package com.campus.banking.persistence;

import java.util.List;

import lombok.With;

@With
public record Page<E>(List<E> list, long total, int page,int size) {
}

