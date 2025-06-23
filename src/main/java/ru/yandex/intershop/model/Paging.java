package ru.yandex.intershop.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Paging {
    @Setter
    private int pageNumber;
    @Setter
    private int pageSize;
    @Setter
    private boolean hasNext;
    @Setter
    private boolean hasPrevious;
}