package cn.turing.firecontrol.auth.module.oauth.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClusterRedisTokenStore extends RedisTokenStore {

    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";

    private RedisTemplate<String, byte[]> redisTemplate;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private String prefix = "TURING:OAUTH:";

    public ClusterRedisTokenStore(RedisConnectionFactory connectionFactory,RedisTemplate<String, byte[]> redisTemplate) {
        super(connectionFactory);
        this.redisTemplate = redisTemplate;
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private String serializeKey(String object) {
        return prefix + object;
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] serializedAccessToken = serialize(token);
        byte[] serializedAuth = serialize(authentication);
        String accessKey = serializeKey(ACCESS + token.getValue());
        String authKey = serializeKey(AUTH + token.getValue());
        String authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
        String approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
        String clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
        redisTemplate.opsForValue().set(accessKey, serializedAccessToken);
        redisTemplate.opsForValue().set(authKey, serializedAuth);
        redisTemplate.opsForValue().set(authToAccessKey, serializedAccessToken);
        if (!authentication.isClientOnly()) {
            redisTemplate.opsForList().rightPush(approvalKey, serializedAccessToken);
        }
        redisTemplate.opsForList().rightPush(clientId, serializedAccessToken);
        if (token.getExpiration() != null) {
            int seconds = token.getExpiresIn();
            redisTemplate.expire(accessKey, seconds,TimeUnit.SECONDS);
            redisTemplate.expire(authKey, seconds,TimeUnit.SECONDS);
            redisTemplate.expire(authToAccessKey, seconds,TimeUnit.SECONDS);
            redisTemplate.expire(clientId, seconds,TimeUnit.SECONDS);
            redisTemplate.expire(approvalKey, seconds,TimeUnit.SECONDS);
        }
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null && refreshToken.getValue() != null) {
            byte[] refresh = serialize(token.getRefreshToken().getValue());
            byte[] auth = serialize(token.getValue());
            String refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
            redisTemplate.opsForValue().set(refreshToAccessKey, auth);
            String accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());
            redisTemplate.opsForValue().set(accessToRefreshKey, refresh);
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                Date expiration = expiringRefreshToken.getExpiration();
                if (expiration != null) {
                    int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                            .intValue();
                    redisTemplate.expire(refreshToAccessKey, seconds,TimeUnit.SECONDS);
                    redisTemplate.expire(accessToRefreshKey, seconds,TimeUnit.SECONDS);
                }
            }
        }
    }


    @Override
    public void removeAccessToken(String tokenValue) {
        String accessKey = serializeKey(ACCESS + tokenValue);
        String authKey = serializeKey(AUTH + tokenValue);
        String accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        redisTemplate.opsForValue();
        byte[] access = (byte[])redisTemplate.opsForValue().get(accessKey);
        byte[] auth = (byte[])redisTemplate.opsForValue().get(authKey);
        redisTemplate.delete(accessKey);
        redisTemplate.delete(accessToRefreshKey);
        redisTemplate.delete(authKey);
        OAuth2Authentication authentication = deserializeAuthentication(auth);
        if (authentication != null) {
            String key = authenticationKeyGenerator.extractKey(authentication);
            String authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
            String unameKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
            String clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
            redisTemplate.delete(authToAccessKey);
            redisTemplate.opsForList().remove(unameKey,1,access);
            redisTemplate.opsForList().remove(clientId,1,access);
            redisTemplate.delete(ACCESS + key);
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        String refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        String refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
        byte[] serializedRefreshToken = serialize(refreshToken);
        redisTemplate.opsForValue().set(refreshKey, serializedRefreshToken);
        redisTemplate.opsForValue().set(refreshAuthKey, serialize(authentication));
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                        .intValue();
                redisTemplate.expire(refreshKey, seconds,TimeUnit.SECONDS);
                redisTemplate.expire(refreshAuthKey, seconds,TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void removeRefreshToken(String tokenValue) {
        String refreshKey = serializeKey(REFRESH + tokenValue);
        String refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        String refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        String access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        redisTemplate.delete(refreshKey);
        redisTemplate.delete(refreshAuthKey);
        redisTemplate.delete(refresh2AccessKey);
        redisTemplate.delete(access2RefreshKey);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        String key = serializeKey(REFRESH_TO_ACCESS + refreshToken);
        byte[] bytes = (byte[]) redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        String accessToken = deserializeString(bytes);
        if (accessToken != null) {
            removeAccessToken(accessToken);
        }
    }

}
