package com.xuannie.devatlas.workspace.common.utils;

import java.util.UUID;

public class SlugUtils {
    private SlugUtils() {};

    public static String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.generateBaseSlug(name);
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        return baseSlug + "-" + suffix;
    }

    public static String generateBaseSlug(String name) {
        return name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
    }
}
