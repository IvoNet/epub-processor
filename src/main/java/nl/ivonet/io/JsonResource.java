/*
 * Copyright (c) 2020 Ivo Woltring
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ivonet.io;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * GETs a JSON resource from the web and marshals it to a java object (T).
 *
 * @param <T> the class to marshall to.
 */
public class JsonResource<T> {

    private final Class<T> typeParameterClass;

    public JsonResource(final Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }


    public T get(final String url) {
        final URL endpoint = toUrl(url);
        final HttpURLConnection con = getCon(endpoint);
        setConnectionMethod(con);
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        try {
            final int status = con.getResponseCode();

            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputline;
            final StringBuilder content = new StringBuilder();
            while (null != (inputline = in.readLine())) {
                content.append(inputline);
            }
            in.close();

            if (200 == status) {
                final Gson gson = new Gson();
                return gson.fromJson(content.toString(), typeParameterClass);
            } else {
                throw new IllegalStateException("Something went wrong with status: " + status);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setConnectionMethod(final HttpURLConnection con) {
        try {
            con.setRequestMethod("GET");
        } catch (final ProtocolException e) {
            throw new IllegalStateException(e);
        }
    }

    private HttpURLConnection getCon(final URL endpoint) {
        try {
            return (HttpURLConnection) endpoint.openConnection();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private URL toUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }


}
