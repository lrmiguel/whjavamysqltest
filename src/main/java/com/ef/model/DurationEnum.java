package com.ef.model;

public enum DurationEnum {

    DAILY("daily"),
    HOURLY("hourly");


    private String durationType;

    DurationEnum(String durationType) {
        this.durationType = durationType;
    }

    public static DurationEnum getEnumByDurationType(String durationType) {
        return DurationEnum.valueOf(durationType.toUpperCase());
    }

}
