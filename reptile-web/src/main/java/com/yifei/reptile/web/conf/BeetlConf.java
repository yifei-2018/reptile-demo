package com.yifei.reptile.web.conf;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * beetl模块配置
 *
 * @author yifei
 * @date 2019/8/18
 */
@Configuration
public class BeetlConf {
    /**
     * 额外模板配置文件路径 ，比如 "beetl.properties"
     */
    @Value("${beetl.extPropertiesPath}")
    String extPropertiesPath;
    /**
     * 模板根目录 ，比如 "/templates"
     */
    @Value("${beetl.templatesPath}")
    String templatesPath;
    /**
     * 模板后缀 ，比如 ".btl"
     */
    @Value("${beetl.suffix}")
    String suffix;

    @Bean(name = "beetlConfig")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        // 获取Spring Boot 的ClassLoader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = BeetlConf.class.getClassLoader();
        }
        // 额外的配置，可以覆盖默认配置，一般不需要
///        beetlGroupUtilConfiguration.setConfigProperties(extProperties);
        ResourcePatternResolver patternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());
        beetlGroupUtilConfiguration.setConfigFileResource(patternResolver.getResource(ResourcePatternResolver.CLASSPATH_URL_PREFIX + extPropertiesPath));
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(loader, templatesPath);
        beetlGroupUtilConfiguration.setResourceLoader(resourceLoader);
        beetlGroupUtilConfiguration.init();
        // 如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(loader);
        return beetlGroupUtilConfiguration;

    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        // Beetl不建议使用spring前缀，会导致include,layout找不到对应的模板，请使用beetl.properties文件的配置RESOURCE.ROOT来配置模板根目录
        /// beetlSpringViewResolver.setPrefix("/");
        beetlSpringViewResolver.setSuffix(suffix);
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }
}
