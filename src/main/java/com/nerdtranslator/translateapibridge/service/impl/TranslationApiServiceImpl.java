package com.nerdtranslator.translateapibridge.service.impl;

import com.google.cloud.translate.v3.*;
import com.nerdtranslator.translateapibridge.service.CredentialsProviderFactory;
import com.nerdtranslator.translateapibridge.service.TranslationApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@PropertySource("classpath:sensitive.properties")
@RequiredArgsConstructor
public class TranslationApiServiceImpl implements TranslationApiService {
    private final CredentialsProviderFactory credentialsProviderFactory;
    private final Environment env;

    @Override
    public String getSingleTranslationFromApi(String originalText, String originalLanguage, String targetLanguage) {
        try (TranslationServiceClient client = TranslationServiceClient.create(
                TranslationServiceSettings
                        .newBuilder()
                        .setCredentialsProvider(credentialsProviderFactory.getCredentialsProvider())
                        .build())) {
            LocationName parent = LocationName.of(env.getProperty("PROJECT_ID"), "global");
            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(originalText)
                            .build();

            TranslateTextResponse response = client.translateText(request);
            return response.getTranslations(0).getTranslatedText();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
