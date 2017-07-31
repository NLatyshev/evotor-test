package com.github.nlatyshev.evotor;

import com.github.nlatyshev.evotor.dao.AccountDao;
import com.github.nlatyshev.evotor.dao.H2Dialect;
import com.github.nlatyshev.evotor.dao.SqlDialect;
import com.github.nlatyshev.evotor.exception.EvotorException;
import com.github.nlatyshev.evotor.net.EvotorDispatcher;
import com.github.nlatyshev.evotor.net.EvotorHttpContext;
import com.github.nlatyshev.evotor.net.ExceptionHandlerSupport;
import com.github.nlatyshev.evotor.net.RequestParser;
import com.github.nlatyshev.evotor.net.ResponseSerializer;
import com.github.nlatyshev.evotor.net.mapper.BigDecimalMapper;
import com.github.nlatyshev.evotor.net.mapper.StringMapper;
import com.github.nlatyshev.evotor.net.mapper.TypeMapper;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static Map<String, SqlDialect> sqlDialects = new HashMap<String, SqlDialect>() {{
        put("org.h2.Driver", new H2Dialect());
    }};
    private static List<TypeMapper<?>> typeMappers = new ArrayList<TypeMapper<?>>() {{
        add(new StringMapper());
        add(new BigDecimalMapper());
    }};


    public static void main(String[] args) {
        try {
            EvotorProperties properties = loadProperties(args);

            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(properties.getInt("port")), 0);
            server.setExecutor(Executors.newFixedThreadPool(properties.getInt("thread.count")));

            AccountDao dao = new AccountDao(dataSource(properties), getSqlDialect(properties));
            AccountController controller = new AccountController(dao);

            EvotorDispatcher evotorDispatcher = new EvotorDispatcher(typeMappers, Collections.singletonList(controller));

            server.createContext("/", new EvotorHttpContext(evotorDispatcher,
                    new RequestParser(), new ResponseSerializer(),
                    Collections.singletonList(new ExceptionHandlerSupport<>(EvotorException.class, EvotorException::getCode))));
            server.start();
        } catch (Exception e) {
            log.error("Cannot start application", e);
        }
    }

    private static DataSource dataSource(EvotorProperties properties) {
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setUrl(properties.get("jdbc.url"));
        ds.setUsername(properties.get("jdbc.user"));
        ds.setPassword(properties.get("jdbc.password"));
        ds.setDriverClassName(properties.get("jdbc.driver"));
        ds.setInitialSize(properties.getInt("jdbc.cp.initial"));
        ds.setMaxActive(properties.getInt("jdbc.cp.maxAlive"));
        ds.setMaxWait(600000);
        ds.setMinEvictableIdleTimeMillis(30000);
        ds.setValidationQuery("select 1 from DUAL");
        ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        ds.setIgnoreExceptionOnPreLoad(true);
        ds.setDefaultAutoCommit(true);
        return ds;
    }

    private static SqlDialect getSqlDialect(EvotorProperties properties) {
        SqlDialect dialect = sqlDialects.get(properties.get("jdbc.driver"));
        if (dialect == null) {
            throw new IllegalArgumentException("There is no dialect for " + properties.get("jdbc.driver"));
        }
        return dialect;
    }

    private static EvotorProperties loadProperties(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Path to application properties is not set");
        }
        try(InputStream is = new FileInputStream(args[0])) {
            Properties raw = new Properties();
            raw.load(is);
            return new EvotorProperties(raw);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot load application properties from " + args[0], e);
        }
    }

    private static class EvotorProperties {
        Properties properties;

        public EvotorProperties(Properties properties) {
            this.properties = properties;
        }

        String get(String name) {
            String val = properties.getProperty(name);
            if (val == null) {
                throw new IllegalArgumentException("Mandatory property is absent: " + name);
            }
            return val;
        }

        int getInt(String name) {
            try {
                return Integer.parseInt(get(name));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Is not integer: " + name, e);
            }
        }
    }
}