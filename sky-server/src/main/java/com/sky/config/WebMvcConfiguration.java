package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private HandlerInterceptor[] interceptors;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        for (HandlerInterceptor interceptor : interceptors) {
//            List<String> addPaths = new ArrayList<>();
//            List<String> excludePaths = new ArrayList<>();
//            if (interceptor instanceof JwtTokenAdminInterceptor) {
//                addPaths.add("/admin/**");
//                excludePaths.add("/admin/employee/login");
//            } else if (interceptor instanceof JwtTokenUserInterceptor) {
//                addPaths.add("/user/**");
//                excludePaths.add("/user/user/login");
//                excludePaths.add("/user/shop/status");
//            } else {
//                log.warn("未知的拦截器，请手动未其配置映射路径: {}", interceptor.getClass().getSimpleName());
//                continue;
//            }
            InterceptorWithPaths interceptorWithPaths = InterceptorWithPaths.value(interceptor.getClass().getSimpleName());
            if (interceptorWithPaths != null) {
                registry.addInterceptor(interceptor)
                        .addPathPatterns(interceptorWithPaths.getAddPaths())
                        .excludePathPatterns(interceptorWithPaths.getExcludePaths());
            } else {
                log.warn("未知的拦截器，请手动未其配置映射路径: {}", interceptor.getClass().getSimpleName());
            }
        }
    }

    @Bean
    public Docket AdminDocket() {
        log.info("生成管理端接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                // 指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket UserDocket() {
        log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.user"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    /**
     * 设置静态资源映射
     *
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展 Spring MVC 框架的消息转化器
     *
     * @param converters
     */
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 为转换器设置一个对象转换器，对象可以将 Java 对象序列化为 json 数据
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将自己的消息转换器加入容器
        converters.add(0, converter);
    }
}

/**
 * 通过类名与枚举的name匹配来找到对应的值
 */
@Getter
@Slf4j
enum InterceptorWithPaths {
    // TODO: 写到配置文件上能更方便的决定是否启用？
    //  那就不应当用枚举来写了，实际上，可以把需要配置的路径让拦截器本身自己携带着，注册时直接调用即可
    JwtTokenAdminInterceptor(
            Collections.singletonList("/admin/**"),
            Collections.singletonList("/admin/employee/login")
    ),
    JwtTokenUserInterceptor(
            Collections.singletonList("/user/**"),
            Arrays.asList("/user/user/login", "/user/shop/status")
    );

    // 生效路径集合
    private final List<String> addPaths;
    // 不生效路径集合
    private final List<String> excludePaths;

    private static final String INTERCEPTOR_PACKAGE = "com.sky.interceptor";
    // EnumMap 不是用来解决优化 valueOf 操作的方案，它是用来做关联额外信息用的
    private static final Map<String, InterceptorWithPaths> values = new HashMap<>();

    InterceptorWithPaths(List<String> addPaths, List<String> excludePaths) {
        this.addPaths = addPaths;
        this.excludePaths = excludePaths;
    }

//  枚举通常不应该与外部依赖耦合，枚举是用于表示固定值的，而不是动态的或可变的值。
//    @PostConstruct
//    private static void init(@Autowired Map<String, Class<HandlerInterceptor>> interceptors) {
//        for (InterceptorWithPaths value : values()) {
//            value.interceptorFullName = interceptors
//                    .get(value.name().replace("WithPaths", ""))
//                    .getName();
//            values.put(value.interceptorFullName, value);
//        }
//        log.debug("----------------\n待注册的拦截器: {}", values);
//    }

    static {
        for (InterceptorWithPaths value : values()) {
            values.put(value.name(), value);
        }
    }

    public static InterceptorWithPaths value(String interceptorFullName) {
        return values.get(interceptorFullName);
    }

    /**
     * 已弃用
     * <p>
     * 扫描所有 HandlerInterceptor 的实现类
     */
    private static Map<String, Class<?>> findHandlerInterceptors(String basePackage) {
        // 创建一个扫描器，不使用默认的类型过滤器
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        // 过滤出实现了 HandlerInterceptor 接口的类
        TypeFilter filter = new AssignableTypeFilter(org.springframework.web.servlet.HandlerInterceptor.class);
        provider.addIncludeFilter(filter);

        // 存放找到的类
        Map<String, Class<?>> interceptorClasses = new HashMap<>();

        // 扫描包，获取候选的 Bean 定义
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            try {
                // 通过反射加载类
                Class<?> clazz = Class.forName(component.getBeanClassName());
                interceptorClasses.put(clazz.getSimpleName(), clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found for bean: " + component.getBeanClassName(), e);
            }
        }

        return interceptorClasses;
    }

}
