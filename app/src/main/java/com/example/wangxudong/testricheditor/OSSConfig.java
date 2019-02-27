package com.example.wangxudong.testricheditor;

import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;

/**
 * 创建信用凭证
 */
public class OSSConfig {
    // Access Key id
    public static final String AK = "LTAIlZmNO0KtyozO";
    // SecretKeyId
    public static final String SK = "kzyrnLtmfu4SWoY6w1uLHqT2Eqen19";

    public static OSSCredentialProvider newCustomSignerCredentialProvider() {
        return new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                return OSSUtils.sign(AK, SK, content);
            }
        };
    }
}