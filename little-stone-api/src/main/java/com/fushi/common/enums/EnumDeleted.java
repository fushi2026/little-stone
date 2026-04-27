package com.fushi.common.enums;

import lombok.Getter;

@Getter
public enum EnumDeleted {
    DELETED(1),
    NOT_DELETED(0);

    private final Integer value;

    EnumDeleted(Integer value) {
        this.value = value;
    }
}
