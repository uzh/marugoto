package ch.uzh.marugoto.backend.security;

import java.time.Duration;

public class Constants {
    public static final long ACCESS_TOKEN_VALIDITY_MS = Duration.ofHours(4).toMillis();
    public static final String SIGNING_KEY = "938jf983fjioejf09834ujfd2x23r2904u430";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
