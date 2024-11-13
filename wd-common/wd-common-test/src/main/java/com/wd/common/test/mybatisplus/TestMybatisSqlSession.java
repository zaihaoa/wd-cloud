package com.wd.common.test.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.wd.common.mybatisplus.MySqlInjector;
import com.wd.common.mybatisplus.config.MybatisPlusConfig;
import com.wd.common.test.util.YmlUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author huangwenda
 * @date 2022/11/26 10:50
 **/
public class TestMybatisSqlSession {
    private SqlSession session;

    public SqlSession getSqlSession() {
        return this.session;
    }

    public <T> T getMapper(Class<T> target) {
        return this.session.getMapper(target);
    }

    private MybatisProperties getMybatisProperties() {
        MybatisProperties mybatisProperties = new MybatisProperties();

        Map<String, Object> resourceMap = YmlUtil.getResource("mock-data.yml");

        Object jdbcUrl = Objects.requireNonNull(resourceMap.get("mysql.url"), "数据库url不能为空");
        Object driver = resourceMap.get("mysql.driver");
        Object username = Objects.requireNonNull(resourceMap.get("mysql.username"), "数据库账号不能为空");
        Object password = Objects.requireNonNull(resourceMap.get("mysql.password"), "数据库密码不能为空");
        Object mapperPackage = Objects.requireNonNull(resourceMap.get("mybatis.mapper-base-package"), "Mybatis的mapper文件扫描基本路径不能为空");
        Object xmlPackage = resourceMap.get("mybatis.xml-base-package");

        mybatisProperties.setJdbcUrl(jdbcUrl.toString());
        mybatisProperties.setDriver(Optional.ofNullable(driver).map(Object::toString).orElse("com.mysql.cj.jdbc.Driver"));
        mybatisProperties.setUsername(username.toString());
        mybatisProperties.setPassword(password.toString());
        mybatisProperties.setMapperPackage(mapperPackage.toString());
        mybatisProperties.setXmlPackage(Optional.ofNullable(xmlPackage).map(Object::toString).orElse(null));
        return mybatisProperties;
    }

    public void open() throws IOException {
        MybatisProperties mybatisProperties = getMybatisProperties();

        //构建mybatis-plus需要的globalconfig
        GlobalConfig globalConfig = new GlobalConfig();
        //此参数会自动生成实现baseMapper的基础方法映射
        globalConfig.setSqlInjector(new MySqlInjector());
        //设置id生成器
        globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
        //设置超类mapper
        globalConfig.setSuperMapperClass(BaseMapper.class);

        globalConfig.setDbConfig(new GlobalConfig.DbConfig());

        globalConfig.setMetaObjectHandler(new MybatisPlusConfig().metaObjectHandler());

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        MybatisConfiguration configuration = new MybatisConfiguration();
        initConfiguration(configuration);
        configuration.addInterceptor(initInterceptor());

        //给configuration注入GlobalConfig里面的配置
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);

        //扫描mapper接口所在包
        configuration.addMappers(mybatisProperties.getMapperPackage());
        //配置日志实现
        configuration.setLogImpl(Slf4jImpl.class);

        //设置数据源
        Environment environment = new Environment("1", new JdbcTransactionFactory(), initDataSource(mybatisProperties));
        configuration.setEnvironment(environment);

        registryMapperXml(configuration, mybatisProperties.getXmlPackage());
        //构建sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = builder.build(configuration);
        this.session = sqlSessionFactory.openSession(true);

    }


    /**
     * 初始化配置
     */
    private void initConfiguration(MybatisConfiguration configuration) {
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);
    }

    /**
     * 初始化数据源
     */
    private DataSource initDataSource(MybatisProperties mybatisProperties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(mybatisProperties.getJdbcUrl());
        dataSource.setDriverClassName(mybatisProperties.getDriver());
        dataSource.setUsername(mybatisProperties.getUsername());
        dataSource.setPassword(mybatisProperties.getPassword());
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }

    /**
     * 初始化拦截器
     */
    private Interceptor initInterceptor() {
        //创建mybatis-plus插件对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //构建分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }


    private void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
        if (classPath == null || "".equals(classPath)) {
            return;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
        while (mapper.hasMoreElements()) {
            URL url = mapper.nextElement();
            if (url.getProtocol().equals("file")) {
                String path = url.getPath();
                File file = new File(path);
                File[] files = file.listFiles();

                registryMapperXml(configuration, files);
            } else {
                JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = urlConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.endsWith(".xml") && !jarEntryName.contains("META-INF")) {
                        InputStream in = jarFile.getInputStream(jarEntry);
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntryName, configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        in.close();
                    }
                }
            }
        }
    }

    private void registryMapperXml(MybatisConfiguration configuration, File[] files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                registryMapperXml(configuration, f.listFiles());
            }
            if (f.getName().endsWith(".xml")) {
                FileInputStream in = new FileInputStream(f);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
                xmlMapperBuilder.parse();
                in.close();
            }
        }
    }
}
