package com.dante.chat.commons;

import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class UriUtils {

    private UriUtils(){}

    public static Map<String, List<String>> splitQuery(URI uri) {
        return splitQuery(uri.getQuery());
    }

    public static Map<String, List<String>> splitQuery(URL url) {
        return splitQuery(url.getQuery());
    }

    public static Map<String, List<String>> splitQuery(String query) {
        if (StringUtils.isEmpty(query)) {
            return Collections.emptyMap();
        }

        return Arrays.stream(query.split("&"))
                .map(UriUtils::splitQueryParameter)
                .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    @SneakyThrows
    private static SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf('=');
        final String key = idx > 0 ? URLDecoder.decode(it.substring(0, idx), "UTF-8") : it;
        final String value = idx > 0 && it.length() > idx + 1 ? URLDecoder.decode(it.substring(idx + 1), "UTF-8") : null;

        return new SimpleImmutableEntry<>(key, value);
    }
}
