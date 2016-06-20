/*
 * Copyright (c) 2016 Ivo Woltring
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

package nl.ivonet.isbndb.model;

/**
 * @author Ivo Woltring
 */
public class Keystats {
    private int keyUseRequests;
    private String keyLimit;
    private int freeUseLimit;
    private int memberUseRequests;
    private String keyId;
    private int dailyMaxPayUses;
    private int keyUseGranted;
    private int memberUseGranted;

    public String getKeyId() {
        return keyId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Keystats{");
        sb.append("keyUseRequests=")
          .append(keyUseRequests);
        sb.append(", keyLimit='")
          .append(keyLimit)
          .append('\'');
        sb.append(", freeUseLimit=")
          .append(freeUseLimit);
        sb.append(", memberUseRequests=")
          .append(memberUseRequests);
        sb.append(", keyId='")
          .append(keyId)
          .append('\'');
        sb.append(", dailyMaxPayUses=")
          .append(dailyMaxPayUses);
        sb.append(", keyUseGranted=")
          .append(keyUseGranted);
        sb.append(", memberUseGranted=")
          .append(memberUseGranted);
        sb.append('}');
        return sb.toString();
    }
}
