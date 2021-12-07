package com.tarkiewicz.repository;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.tarkiewicz.endpoint.dto.Account;
import com.tarkiewicz.exception.DuplicateKeyErrorException;
import com.tarkiewicz.exception.MongodbConnectionException;
import com.tarkiewicz.exception.NotFoundException;
import com.tarkiewicz.repository.model.UserAccountModel;
import com.tarkiewicz.security.BCryptPasswordEncoderService;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.bson.conversions.Bson;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Singleton
@Slf4j
public class AccountRepository {

    private static final String USERNAME_ATTRIBUTE = "username";
    private static final String PASSWORD_ATTRIBUTE = "password";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String CANNOT_FIND_USER_MSG = "Cannot find user with username: %s";

    private final MongoClient mongoClient;
    private final BCryptPasswordEncoderService bCryptPasswordEncoderService;
    private final String database;
    private final String collectionName;

    public AccountRepository(final MongoClient mongoClient, final BCryptPasswordEncoderService bCryptPasswordEncoderService,
                             final @Value("${mongodb.database}") String database, final @Value("${mongodb.collection.account}") String collectionName) {
        this.mongoClient = mongoClient;
        this.bCryptPasswordEncoderService = bCryptPasswordEncoderService;
        this.database = database;
        this.collectionName = collectionName;
    }

    @PostConstruct
    public void collectionConfiguration() {
        createUniqueUsernameIndex().block();
    }

    public Mono<String> register(final String username, final String password, final String email) {
        return Mono.from(getAccountCollection()
                        .insertOne(buildUserAccountModel(username, password, email)))
                .map(InsertOneResult::wasAcknowledged)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new MongodbConnectionException()))
                .map(success -> username)
                .onErrorResume(err -> Mono.error(new DuplicateKeyErrorException(username)));
    }

    public Mono<Boolean> validCredentials(final String username, final String password) {
        return Mono.from(getAccountCollection()
                        .find(usernameQuery(username))
                        .projection(Projections.include(USERNAME_ATTRIBUTE, PASSWORD_ATTRIBUTE)))
                .switchIfEmpty(Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.USER_NOT_FOUND)))
                .map(UserAccountModel::getPassword)
                .map(pass -> bCryptPasswordEncoderService.matches(password, pass));
    }

    public Mono<Account> getUser(final String username) {
        return Mono.from(getAccountCollection()
                        .find(usernameQuery(username))
                        .projection(Projections.include(USERNAME_ATTRIBUTE, EMAIL_ATTRIBUTE)))
                .map(user -> Account.of(user.getUsername(), user.getEmail()))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format(CANNOT_FIND_USER_MSG, username))));
    }

    private Bson usernameQuery(final String username) {
        return Filters.and(Filters.eq(USERNAME_ATTRIBUTE, username));
    }

    private UserAccountModel buildUserAccountModel(final String username, final String password, final String email) {
        return UserAccountModel.of(username, bCryptPasswordEncoderService.encode(password), email, Instant.now());
    }

    private MongoCollection<UserAccountModel> getAccountCollection() {
        return mongoClient.getDatabase(database).getCollection(collectionName, UserAccountModel.class);
    }

    private Mono<String> createUniqueUsernameIndex() {
        final IndexOptions option = new IndexOptions().unique(Boolean.TRUE);
        return Mono.from(getAccountCollection().createIndex(Indexes.ascending(USERNAME_ATTRIBUTE), option));
    }
}
