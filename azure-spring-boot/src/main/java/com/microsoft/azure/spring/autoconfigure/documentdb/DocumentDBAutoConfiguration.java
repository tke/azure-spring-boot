/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.documentdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.autoconfigure.common.GetHashMac;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Persistent;

@Configuration
@ConditionalOnClass({DocumentClient.class, DocumentDbTemplate.class})
@ConditionalOnProperty(prefix = "azure.documentdb", value = {"uri", "key"})
@EnableConfigurationProperties(DocumentDBProperties.class)
public class DocumentDBAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentDBAutoConfiguration.class);
    private static final String USER_AGENT_SUFFIX = "spring-boot-starter/0.1.9-SNAPSHOT";

    private final DocumentDBProperties properties;
    private final ConnectionPolicy connectionPolicy;
    private final ApplicationContext applicationContext;

    public DocumentDBAutoConfiguration(DocumentDBProperties properties,
                                       ObjectProvider<ConnectionPolicy> connectionPolicyObjectProvider,
                                       ApplicationContext applicationContext) {
        this.properties = properties;
        this.connectionPolicy = connectionPolicyObjectProvider.getIfAvailable();
        this.applicationContext = applicationContext;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public DocumentClient documentClient() {
        return createDocumentClient();
    }

    private DocumentClient createDocumentClient() {
        LOG.debug("createDocumentClient");
        final ConnectionPolicy policy = connectionPolicy == null ? ConnectionPolicy.GetDefault() : connectionPolicy;

        String userAgent = (policy.getUserAgentSuffix() == null ? "" : ";" + policy.getUserAgentSuffix()) +
                ";" + USER_AGENT_SUFFIX;

        if (properties.isAllowTelemetry() && GetHashMac.getHashMac() != null) {
            userAgent += ";" + GetHashMac.getHashMac();
        }
        policy.setUserAgentSuffix(userAgent);

        return new DocumentClient(properties.getUri(), properties.getKey(), policy,
                properties.getConsistencyLevel() == null ?
                        ConsistencyLevel.Session : properties.getConsistencyLevel());
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentDbFactory documentDbFactory(DocumentClient documentClient) {
        return new DocumentDbFactory(documentClient);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.documentdb", value = "database")
    public DocumentDbTemplate documentDbTemplate(DocumentDbFactory documentDbFactory,
                                                 MappingDocumentDbConverter mappingDocumentDbConverter) {
        return new DocumentDbTemplate(documentDbFactory, mappingDocumentDbConverter,
                properties.getDatabase());
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentDbMappingContext documentDbMappingContext() {
        try {
            final DocumentDbMappingContext documentDbMappingContext = new DocumentDbMappingContext();
            documentDbMappingContext.setInitialEntitySet(new EntityScanner(this.applicationContext)
                    .scan(Persistent.class));

            return documentDbMappingContext;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingDocumentDbConverter mappingDocumentDbConverter(
            DocumentDbMappingContext documentDbMappingContext) {
        return new MappingDocumentDbConverter(documentDbMappingContext);
    }
}
