package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @auther ff
 * @create 2018-08-03 15:05
 */
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    @PostConstruct
    public void init() {
        try {
            //读取key
            File prikey = new File(priKeyPath);
            File pubkey = new File(pubKeyPath);
            if (!prikey.exists() || !pubkey.exists()) {
                //不存在生成key
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            //存在
            PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
            PrivateKey privateKey = RsaUtils.getPrivateKey(priKeyPath);
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        } catch (Exception e) {
            logger.error("初始化失败" + e);
            throw new RuntimeException(e);
        }

    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
