/*
 * MIT License
 *
 * Copyright (c) 2020 Airbyte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.airbyte.server.handlers;

import io.airbyte.api.model.OAuthConsentRead;
import io.airbyte.api.model.OAuthDestinationConsentRequestBody;
import io.airbyte.api.model.OAuthDestinationRequestBody;
import io.airbyte.api.model.OAuthRead;
import io.airbyte.api.model.OAuthSourceConsentRequestBody;
import io.airbyte.api.model.OAuthSourceRequestBody;
import io.airbyte.server.errors.ApplicationErrorKnownException;

public class OAuthHandler {

  public OAuthConsentRead getSourceOAuthConsent(OAuthSourceConsentRequestBody oAuthSourceConsentRequestBody) {
    // TODO: Implement OAuth module to be called here https://github.com/airbytehq/airbyte/issues/5641
    throw new ApplicationErrorKnownException("Source connector does not supports OAuth yet.");
  }

  public OAuthConsentRead getDestinationOAuthConsent(OAuthDestinationConsentRequestBody oAuthDestinationConsentRequestBody) {
    // TODO: Implement OAuth module to be called here https://github.com/airbytehq/airbyte/issues/5641
    throw new ApplicationErrorKnownException("Destination connector does not supports OAuth yet.");
  }

  public OAuthRead completeSourceOAuth(OAuthSourceRequestBody oauthSourceRequestBody) {
    // TODO: Implement OAuth module to be called here https://github.com/airbytehq/airbyte/issues/5641
    throw new ApplicationErrorKnownException("Source connector does not supports OAuth yet.");
  }

  public OAuthRead completeDestinationOAuth(OAuthDestinationRequestBody oauthDestinationRequestBody) {
    // TODO: Implement OAuth module to be called here https://github.com/airbytehq/airbyte/issues/5641
    throw new ApplicationErrorKnownException("Destination connector does not supports OAuth yet.");
  }

}
