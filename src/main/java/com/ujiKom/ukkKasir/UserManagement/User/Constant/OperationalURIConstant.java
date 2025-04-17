package com.ujiKom.ukkKasir.UserManagement.User.Constant;

import java.util.Map;

public class OperationalURIConstant {
    public static final Map<String, String> LIST_URI_BY_OP = Map.of(
            "VIEW_EXT_INT", "/role",
            "VIEW_USER", "/user",
            "RULE", "/rule",
            "VIEW_TRANSACTION", "/transaction_data"
    );
}
