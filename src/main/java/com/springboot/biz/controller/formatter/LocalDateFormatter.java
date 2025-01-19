package com.springboot.biz.controller.formatter;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    public LocalDate parse(String text, Locale locale) {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern("yy-MM-dd"));
    }

    public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ofPattern("yy-MM-dd").format(object);
    }
}
