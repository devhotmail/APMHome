package webapp.framework.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class WithoutTrailingImplBeanNameGenerator extends AnnotationBeanNameGenerator {
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanName = super.generateBeanName(definition, registry);
        if (!beanName.endsWith("Impl")) {
            return beanName;
        }
        return StringUtils.substringBeforeLast(beanName, "Impl");
    }
}
