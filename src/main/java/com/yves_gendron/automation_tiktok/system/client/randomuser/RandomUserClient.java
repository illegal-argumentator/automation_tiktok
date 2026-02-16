package com.yves_gendron.automation_tiktok.system.client.randomuser;

import com.yves_gendron.automation_tiktok.common.helper.OkHttpHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Dob;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Name;
import com.yves_gendron.automation_tiktok.system.client.randomuser.common.dto.NinjaRandomResponse;
import com.yves_gendron.automation_tiktok.system.client.randomuser.common.dto.RandomUserResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RandomUserClient {

    private static final String RANDOM_USER_API = "https://api.api-ninjas.com/v2/randomuser?count=1";

    private static final String API_KEY = "W00Mqfx8DeLzRMjAGTQTE4NlcQnjkDsunuwl6okK";

    private final OkHttpHelper okHttpHelper;

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    public RandomUserResponse.RandomResult getRandomUser() {
        try {
            Request request = new Request.Builder()
                    .url(RANDOM_USER_API)
                    .header("X-Api-Key", API_KEY)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "RandomUserApi: " + response.code() + " - " + response.message());
            response.close();

            List<NinjaRandomResponse.NinjaUser> data = objectMapper.readValue(responseBody, new TypeReference<>() {
            });

            if (data.isEmpty()) {
                throw new RuntimeException("No random user data was generated.");
            }

            NinjaRandomResponse.NinjaUser ninjaUser = data.get(0);
            return RandomUserResponse.RandomResult.builder()
                    .email(ninjaUser.email())
                    .name(new Name(ninjaUser.firstName(), ninjaUser.lastName()))
                    .picture(RandomUserResponse.RandomResult.Picture.builder().large(ninjaUser.picture()).build())
                    .dob(new Dob(ninjaUser.dob(), ninjaUser.age()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Exception while getting random user from RandomUserApi");
        }
    }
}
