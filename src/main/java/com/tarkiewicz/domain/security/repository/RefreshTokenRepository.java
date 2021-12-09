package com.tarkiewicz.domain.security.repository;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.tarkiewicz.exception.MongodbConnectionException;
import com.tarkiewicz.domain.security.repository.model.RefreshTokenModel;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.BooleanUtils;
import org.bson.conversions.Bson;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Singleton
public class RefreshTokenRepository {

    private static final String REFRESH_TOKEN_ATTRIBUTE = "refreshToken";
    private static final String USERNAME_ATTRIBUTE = "username";
    private static final String REVOKED_ATTRIBUTES = "revoked";

    private final MongoClient mongoClient;
    private final String database;
    private final String collectionName;

    public RefreshTokenRepository(final MongoClient mongoClient, final @Value("${mongodb.database}") String database,
                                  final @Value("${mongodb.collection.refreshToken}") String collectionName) {
        this.mongoClient = mongoClient;
        this.database = database;
        this.collectionName = collectionName;
    }

    @Transactional
    public Mono<Void> save(final @NonNull @NotBlank String username,
                           final @NonNull @NotBlank String refreshToken,
                           final @NonNull @NotNull Boolean revoked) {

        return Mono.from(getRefreshTokenCollection()
                        .insertOne(RefreshTokenModel.of(username, refreshToken, revoked, Instant.now())))
                .map(InsertOneResult::wasAcknowledged)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new MongodbConnectionException()))
                .then();

    }

    @Transactional
    public Mono<Void> updateByUsername(final @NonNull @NotBlank String username, final boolean revoked) {
        return Mono.from(getRefreshTokenCollection()
                        .updateOne(usernameQuery(username), revokeUpdate(revoked)))
                .map(UpdateResult::wasAcknowledged)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new MongodbConnectionException()))
                .then();

    }

    public Mono<RefreshTokenModel> findByRefreshToken(@NonNull @NotBlank String refreshToken) {
        return Mono.from(getRefreshTokenCollection().find(refreshTokenQuery(refreshToken)));
    }

    private Bson refreshTokenQuery(final String refreshToken) {
        return Filters.and(Filters.eq(REFRESH_TOKEN_ATTRIBUTE, refreshToken));
    }

    private Bson usernameQuery(final String username) {
        return Filters.and(Filters.eq(USERNAME_ATTRIBUTE, username));
    }

    private Bson revokeUpdate(final boolean revoked) {
        return Updates.set(REVOKED_ATTRIBUTES, revoked);
    }

    private MongoCollection<RefreshTokenModel> getRefreshTokenCollection() {
        return mongoClient.
                getDatabase(database).
                getCollection(collectionName, RefreshTokenModel.class);
    }
}
