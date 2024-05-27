package com.chaewsstore.core.infra.importer;

import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

class ChaewsstoreConfigImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return Arrays.stream(getValues(metadata))
            .map(v -> v.getConfigClass().getName())
            .toArray(String[]::new);
    }

    private ChaewsstoreConfigGroup[] getValues(AnnotationMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(
            EnableChaewsstoreConfig.class.getName());
        return (ChaewsstoreConfigGroup[]) MapUtils.getObject(attributes, "value", new ChaewsstoreConfigGroup[]{});
    }
}
